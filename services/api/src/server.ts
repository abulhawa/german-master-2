import { createServer, type IncomingMessage } from "node:http";
import { randomUUID } from "node:crypto";
import { ProfileRequestSchema, AttemptBatchSchema, AttemptBatchResponseSchema, SessionRequestSchema, FocusedSessionRequestSchema, ExposureBatchSchema, ExposureBatchResponseSchema, type ApiError } from "@german-master/contracts";
import { ApiFailure, FoundationStore } from "./store";
import { ContentReportRequestSchema } from '@german-master/contracts';
import { SessionCompletionRequestSchema } from '@german-master/contracts';

export type Authenticate = (request: IncomingMessage) => Promise<string | null>;
const UUID = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
const BODY_LIMIT = 128 * 1024;

async function body(request: IncomingMessage): Promise<unknown> {
  let size = 0;
  const chunks: Buffer[] = [];
  for await (const chunk of request) {
    size += chunk.length;
    if (size > BODY_LIMIT) throw new ApiFailure("payload_too_large", 413);
    chunks.push(chunk);
  }
  try { return JSON.parse(Buffer.concat(chunks).toString("utf8")); }
  catch { throw new ApiFailure("invalid_json", 400); }
}

/** No default authentication. A verified subject must be supplied by the host adapter. */
export function createApi(store: FoundationStore, authenticate: Authenticate) {
  return createServer(async (request, response) => {
    const requestId = randomUUID();
    response.setHeader("Content-Type", "application/json; charset=utf-8");
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("X-Content-Type-Options", "nosniff");
    try {
      const url = new URL(request.url ?? '/', 'http://localhost');
      const completion = /^\/v2\/sessions\/([0-9a-f-]+)\/complete$/i.exec(url.pathname);
      const isWrite = request.method === 'POST' && (['/v2/packs', '/v2/sessions', '/v2/attempts:batch', '/v2/exposures:batch', '/v2/profile', '/v2/content-reports'].includes(request.url ?? '') || (!!completion && !url.search));
      const isRead = request.method === 'GET' && ['/v2/targets', '/v2/sync', '/v2/catalog', '/v2/profile'].includes(url.pathname);
      if (!isWrite && !isRead) throw new ApiFailure('not_found', 404);
      const userId = await authenticate(request);
      if (!userId || !UUID.test(userId)) throw new ApiFailure("authentication_required", 401);
      if (request.method === 'GET') {
        if (url.pathname === '/v2/profile') {
          if (url.search) throw new ApiFailure('invalid_request', 400);
          response.end(JSON.stringify(await store.profile(userId))); return;
        }
        if (url.pathname === '/v2/catalog') {
          if (url.search) throw new ApiFailure('invalid_request', 400);
          response.end(JSON.stringify(await store.catalog(userId)));
          return;
        }
        for (const key of url.searchParams.keys())
          if (!['cursor', 'limit'].includes(key) || url.searchParams.getAll(key).length !== 1)
            throw new ApiFailure('invalid_request', 400);
        const cursor = url.searchParams.get('cursor') ?? undefined;
        const rawLimit = url.searchParams.get('limit') ?? '50';
        if ((cursor !== undefined && !UUID.test(cursor)) || !/^[1-9]\d{0,2}$/.test(rawLimit) || Number(rawLimit) > 100)
          throw new ApiFailure('invalid_request', 400);
        response.end(JSON.stringify(url.pathname === '/v2/targets'
          ? await store.targets(userId, Number(rawLimit), cursor)
          : await store.sync(userId, Number(rawLimit), cursor)));
        return;
      }
      if (request.headers["content-type"]?.split(";")[0].trim() !== "application/json")
        throw new ApiFailure("json_required", 415);
      const input = await body(request);
      if (request.url === '/v2/packs') {
        const parsed = SessionRequestSchema.safeParse(input);
        if (!parsed.success) throw new ApiFailure('invalid_request',400);
        response.end(JSON.stringify(await store.preparePack(userId,parsed.data)));
      } else if (completion) {
        const parsed = SessionCompletionRequestSchema.safeParse(input);
        if (!parsed.success || !UUID.test(completion[1])) throw new ApiFailure('invalid_request',400);
        response.end(JSON.stringify(await store.complete(userId,completion[1],parsed.data)));
      } else if (request.url === '/v2/content-reports') {
        const parsed = ContentReportRequestSchema.safeParse(input);
        if (!parsed.success) throw new ApiFailure('invalid_request',400);
        response.end(JSON.stringify(await store.report(userId,parsed.data)));
      } else if (request.url === "/v2/profile") {
        const parsed = ProfileRequestSchema.safeParse(input);
        if (!parsed.success) throw new ApiFailure("invalid_request", 400);
        response.end(JSON.stringify(await store.saveProfile(userId, parsed.data)));
      } else if (request.url === "/v2/sessions") {
        const parsed = (input && typeof input === "object" && "focus" in input ? FocusedSessionRequestSchema : SessionRequestSchema).safeParse(input);
        if (!parsed.success) throw new ApiFailure("invalid_request", 400);
        response.end(JSON.stringify(await store.createSession(userId, parsed.data)));
      } else if (request.url === "/v2/exposures:batch") {
        const parsed = ExposureBatchSchema.safeParse(input);
        if (!parsed.success) throw new ApiFailure("invalid_request", 400);
        const acknowledgments = [];
        for (const event of parsed.data.events) acknowledgments.push(await store.expose(userId, event, requestId));
        response.end(JSON.stringify(ExposureBatchResponseSchema.parse({ apiVersion: "v2", acknowledgments })));
      } else {
        // Envelope failures reject the request; valid typed items are committed independently.
        const parsed = AttemptBatchSchema.safeParse(input);
        if (!parsed.success) throw new ApiFailure("invalid_request", 400);
        const acknowledgments = [];
        for (const attempt of parsed.data.attempts) acknowledgments.push(await store.submit(userId, attempt, requestId));
        response.end(JSON.stringify(AttemptBatchResponseSchema.parse({ apiVersion: "v2", acknowledgments })));
      }
    } catch (error) {
      const known = error instanceof ApiFailure;
      response.statusCode = known ? error.status : 500;
      const payload: ApiError = { code: known ? error.code : "internal_error", message: "The request could not be completed.", requestId, retryable: !known };
      response.end(JSON.stringify(payload));
    }
  });
}

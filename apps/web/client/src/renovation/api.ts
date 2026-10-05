import { LearnerProfileSchema, type LearnerProfile, type ProfileRequest, CatalogSchema, SessionSchema, type Catalog, type FocusedSessionRequest, type Session, TargetPageSchema, SyncPageSchema, type TargetPage, type SyncPage } from "@german-master/contracts";
import { localFoundationApi, type FoundationApi } from "../foundation/api";
import { ExposureBatchResponseSchema, type ExposureEvent, type ExposureAcknowledgment } from "@german-master/contracts";
import { ContentReportReceiptSchema, type ContentReportRequest, type ContentReportReceipt } from '@german-master/contracts';
import { SessionCompletionReceiptSchema, type SessionCompletionRequest, type SessionCompletionReceipt } from '@german-master/contracts';
import { type PreparedPack, type SessionRequest } from '@german-master/contracts';
import { validatePreparedPack } from '@german-master/learning-engine';
import type { AccountBinding } from './account';
import { LearnerExportSchema, type LearnerExport } from '@german-master/contracts';
import { PrivacyDeleteReceiptSchema, type PrivacyDeleteRequest, type PrivacyDeleteReceipt } from '@german-master/contracts';

export interface LearnerApi extends FoundationApi {
  deleteLearner?(request: PrivacyDeleteRequest): Promise<PrivacyDeleteReceipt>;
  exportLearner?(): Promise<LearnerExport>;
  preparePack?(request: SessionRequest): Promise<PreparedPack>;
  complete?(sessionId: string, request: SessionCompletionRequest): Promise<SessionCompletionReceipt>;
  report?(request: ContentReportRequest): Promise<ContentReportReceipt>;
  expose(event: ExposureEvent): Promise<ExposureAcknowledgment>;
  profile(): Promise<LearnerProfile>;
  saveProfile(request: ProfileRequest): Promise<LearnerProfile>;
  catalog(): Promise<Catalog>;
  createFocusedSession(request: FocusedSessionRequest): Promise<Session>;
  targets(cursor?: string): Promise<TargetPage>;
  sync(cursor: string): Promise<SyncPage>;
}
// The local server uses invalid_cursor for unknown or reset cursor records.
export class SyncCursorReset extends Error {
  constructor() { super("Sync cursor requires a fresh snapshot"); }
}
export function localLearnerApi(account?: AccountBinding, transport: typeof fetch = fetch): LearnerApi {
  const binding = account ? { subject: account.identity.subject, assertCurrent: () => account.assertCurrent() } : undefined;
  async function transportRequest(input: string, init: RequestInit) {
    binding?.assertCurrent();
    const response = await transport(input, { ...init, headers: { ...init.headers, ...(binding ? { 'X-Learner-Subject': binding.subject } : {}) } });
    binding?.assertCurrent();
    return response;
  }
  async function json(response: Response) {
    const body: unknown = await response.json(); binding?.assertCurrent(); return body;
  }
  async function get(path: string, cursor?: string) {
    const response = await transportRequest(`${path}?limit=50${cursor ? `&cursor=${encodeURIComponent(cursor)}` : ""}`, {
      headers: { Authorization: "Bearer foundation-local-demo" }, cache: "no-store",
    });
    if (!response.ok) {
      if (path === "/v2/sync" && response.status === 400) {
        const error: unknown = await json(response);
        if (error && typeof error === "object" && "code" in error && error.code === "invalid_cursor") throw new SyncCursorReset();
      }
      throw Error("Confirmed read unavailable");
    }
    return json(response);
  }
  return { ...localFoundationApi(binding, transport),
    async deleteLearner(request) {
      const response = await transportRequest('/v2/me', {method:'DELETE',headers:{'Content-Type':'application/json',Authorization:'Bearer foundation-local-demo'},body:JSON.stringify(request)});
      if (!response.ok) throw Error('Deletion unavailable');
      const receipt = PrivacyDeleteReceiptSchema.parse(await json(response));
      if (receipt.requestId !== request.requestId || binding && receipt.subject.toLowerCase() !== binding.subject) throw Error('Deletion receipt mismatch');
      return receipt;
    },
    async exportLearner() {
      const response = await transportRequest('/v2/me/export', {headers:{Authorization:'Bearer foundation-local-demo'},cache:'no-store'});
      if (!response.ok) throw Error('Export unavailable');
      const data = LearnerExportSchema.parse(await json(response));
      if (binding && data.subject.toLowerCase() !== binding.subject) throw Error('Export account mismatch');
      return data;
    },
    async preparePack(request) {
      const response = await transportRequest('/v2/packs', {method:'POST',headers:{'Content-Type':'application/json',Authorization:'Bearer foundation-local-demo'},body:JSON.stringify(request)});
      if(!response.ok) throw Error('Pack unavailable');
      const pack = await validatePreparedPack(await json(response));
      binding?.assertCurrent();
      if(pack.packId !== request.requestId) throw Error('Pack request mismatch');
      return pack;
    },
    async complete(sessionId, request) {
      const response = await transportRequest(`/v2/sessions/${encodeURIComponent(sessionId)}/complete`, {method:'POST',headers:{'Content-Type':'application/json',Authorization:'Bearer foundation-local-demo'},body:JSON.stringify(request)});
      if (!response.ok) throw Error('Completion unavailable');
      const receipt = SessionCompletionReceiptSchema.parse(await json(response));
      if (receipt.sessionId !== sessionId || receipt.requestId !== request.requestId || receipt.mode !== request.mode) throw Error('Completion receipt mismatch');
      return receipt;
    },
    async report(request) {
      const response = await transportRequest('/v2/content-reports', {method:'POST',headers:{'Content-Type':'application/json',Authorization:'Bearer foundation-local-demo'},body:JSON.stringify(request)});
      if (!response.ok) throw Error('Report unavailable');
      const receipt = ContentReportReceiptSchema.parse(await json(response));
      if (receipt.reportId !== request.reportId) throw Error('Report receipt mismatch');
      return receipt;
    },
    async expose(event) {
      const response = await transportRequest("/v2/exposures:batch", { method: "POST", headers: { "Content-Type": "application/json", Authorization: "Bearer foundation-local-demo" }, body: JSON.stringify({ apiVersion: "v2", events: [event] }) });
      if (!response.ok) throw Error("Exposure unavailable");
      const parsed = ExposureBatchResponseSchema.parse(await json(response));
      if (parsed.acknowledgments.length !== 1 || parsed.acknowledgments[0].eventId !== event.eventId) throw Error("Exposure acknowledgment linkage mismatch");
      return parsed.acknowledgments[0];
    },
    async profile() {
      const response = await transportRequest("/v2/profile", { headers: { Authorization: "Bearer foundation-local-demo" }, cache: "no-store" });
      if (!response.ok) throw Error("Profile unavailable");
      return LearnerProfileSchema.parse(await json(response));
    },
    async saveProfile(request) {
      const response = await transportRequest("/v2/profile", { method: "POST", headers: { "Content-Type": "application/json", Authorization: "Bearer foundation-local-demo" }, body: JSON.stringify(request) });
      if (!response.ok) throw Error("Profile update unavailable");
      return LearnerProfileSchema.parse(await json(response));
    },
    async catalog() {
      const response = await transportRequest("/v2/catalog", { headers: { Authorization: "Bearer foundation-local-demo" }, cache: "no-store" });
      if (!response.ok) throw Error("Catalog unavailable");
      return CatalogSchema.parse(await json(response));
    },
    async createFocusedSession(request) {
      const response = await transportRequest("/v2/sessions", { method: "POST", headers: { "Content-Type": "application/json", Authorization: "Bearer foundation-local-demo" }, body: JSON.stringify(request) });
      if (!response.ok) throw Error("Focused session unavailable");
      return SessionSchema.parse(await json(response));
    },
    async targets(cursor) { return TargetPageSchema.parse(await get("/v2/targets", cursor)); },
    async sync(cursor) { return SyncPageSchema.parse(await get("/v2/sync", cursor)); },
  };
}

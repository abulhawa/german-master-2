import { LearnerProfileSchema, type LearnerProfile, type ProfileRequest, CatalogSchema, SessionSchema, type Catalog, type FocusedSessionRequest, type Session, TargetPageSchema, SyncPageSchema, type TargetPage, type SyncPage } from "@german-master/contracts";
import { localFoundationApi, type FoundationApi } from "../foundation/api";
import { ExposureBatchResponseSchema, type ExposureEvent, type ExposureAcknowledgment } from "@german-master/contracts";
import { ContentReportReceiptSchema, type ContentReportRequest, type ContentReportReceipt } from '@german-master/contracts';

export interface LearnerApi extends FoundationApi {
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
export function localLearnerApi(): LearnerApi {
  async function get(path: string, cursor?: string) {
    const response = await fetch(`${path}?limit=50${cursor ? `&cursor=${encodeURIComponent(cursor)}` : ""}`, {
      headers: { Authorization: "Bearer foundation-local-demo" }, cache: "no-store",
    });
    if (!response.ok) {
      if (path === "/v2/sync" && response.status === 400) {
        const error: unknown = await response.json();
        if (error && typeof error === "object" && "code" in error && error.code === "invalid_cursor") throw new SyncCursorReset();
      }
      throw Error("Confirmed read unavailable");
    }
    return response.json();
  }
  return { ...localFoundationApi(),
    async report(request) {
      const response = await fetch('/v2/content-reports', {method:'POST',headers:{'Content-Type':'application/json',Authorization:'Bearer foundation-local-demo'},body:JSON.stringify(request)});
      if (!response.ok) throw Error('Report unavailable');
      const receipt = ContentReportReceiptSchema.parse(await response.json());
      if (receipt.reportId !== request.reportId) throw Error('Report receipt mismatch');
      return receipt;
    },
    async expose(event) {
      const response = await fetch("/v2/exposures:batch", { method: "POST", headers: { "Content-Type": "application/json", Authorization: "Bearer foundation-local-demo" }, body: JSON.stringify({ apiVersion: "v2", events: [event] }) });
      if (!response.ok) throw Error("Exposure unavailable");
      const parsed = ExposureBatchResponseSchema.parse(await response.json());
      if (parsed.acknowledgments.length !== 1 || parsed.acknowledgments[0].eventId !== event.eventId) throw Error("Exposure acknowledgment linkage mismatch");
      return parsed.acknowledgments[0];
    },
    async profile() {
      const response = await fetch("/v2/profile", { headers: { Authorization: "Bearer foundation-local-demo" }, cache: "no-store" });
      if (!response.ok) throw Error("Profile unavailable");
      return LearnerProfileSchema.parse(await response.json());
    },
    async saveProfile(request) {
      const response = await fetch("/v2/profile", { method: "POST", headers: { "Content-Type": "application/json", Authorization: "Bearer foundation-local-demo" }, body: JSON.stringify(request) });
      if (!response.ok) throw Error("Profile update unavailable");
      return LearnerProfileSchema.parse(await response.json());
    },
    async catalog() {
      const response = await fetch("/v2/catalog", { headers: { Authorization: "Bearer foundation-local-demo" }, cache: "no-store" });
      if (!response.ok) throw Error("Catalog unavailable");
      return CatalogSchema.parse(await response.json());
    },
    async createFocusedSession(request) {
      const response = await fetch("/v2/sessions", { method: "POST", headers: { "Content-Type": "application/json", Authorization: "Bearer foundation-local-demo" }, body: JSON.stringify(request) });
      if (!response.ok) throw Error("Focused session unavailable");
      return SessionSchema.parse(await response.json());
    },
    async targets(cursor) { return TargetPageSchema.parse(await get("/v2/targets", cursor)); },
    async sync(cursor) { return SyncPageSchema.parse(await get("/v2/sync", cursor)); },
  };
}

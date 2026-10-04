import { TargetPageSchema, SyncPageSchema, type TargetPage, type SyncPage } from "@german-master/contracts";
import { localFoundationApi, type FoundationApi } from "../foundation/api";

export interface LearnerApi extends FoundationApi {
  targets(cursor?: string): Promise<TargetPage>;
  sync(cursor: string): Promise<SyncPage>;
}
export function localLearnerApi(): LearnerApi {
  async function get(path: string, cursor?: string) {
    const response = await fetch(`${path}?limit=50${cursor ? `&cursor=${encodeURIComponent(cursor)}` : ""}`, {
      headers: { Authorization: "Bearer foundation-local-demo" }, cache: "no-store",
    });
    if (!response.ok) throw Error("Confirmed read unavailable");
    return response.json();
  }
  return { ...localFoundationApi(),
    async targets(cursor) { return TargetPageSchema.parse(await get("/v2/targets", cursor)); },
    async sync(cursor) { return SyncPageSchema.parse(await get("/v2/sync", cursor)); },
  };
}

import { describe, expect, it } from 'vitest';
import { cleanV2Project, legacyV2Project, resolveV2Project } from './production-project';

describe('GM2 dedicated runtime project selection', () => {
  it.each([legacyV2Project, cleanV2Project])('permits an explicitly selected production project %s', project => {
    expect(resolveV2Project({ GM_ENVIRONMENT: 'production', VITE_V2_AUTH_PROJECT: project })).toBe(project);
  });
  it('allows replacement verification only against the clean project', () => {
    expect(resolveV2Project({ GM_ENVIRONMENT: 'replacement-verification', VITE_V2_AUTH_PROJECT: cleanV2Project })).toBe(cleanV2Project);
    expect(() => resolveV2Project({ GM_ENVIRONMENT: 'replacement-verification', VITE_V2_AUTH_PROJECT: legacyV2Project })).toThrow();
  });
  it('fails closed on missing or unrelated project and invalid modes', () => {
    for (const env of [
      {}, { GM_ENVIRONMENT: 'production' },
      { GM_ENVIRONMENT: 'production', VITE_V2_AUTH_PROJECT: 'unknown' },
      { GM_ENVIRONMENT: 'development', VITE_V2_AUTH_PROJECT: cleanV2Project },
      { GM_ENVIRONMENT: 'replacement-verification' },
    ]) expect(() => resolveV2Project(env)).toThrow();
  });
});

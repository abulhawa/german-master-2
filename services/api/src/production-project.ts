/** Restrict production learner connections to the two owner-authorized GM2 projects. */
export const legacyV2Project = 'zgmyrpzwgtydwlzponih';
export const cleanV2Project = 'sqgjsmiaprsuilcjmaav';
export type V2Project = typeof legacyV2Project | typeof cleanV2Project;

export function resolveV2Project(env: Record<string, string | undefined>): V2Project {
  if (env.GM_ENVIRONMENT !== 'production' && env.GM_ENVIRONMENT !== 'replacement-verification') {
    throw Error('Dedicated production environment required');
  }
  const project = env.VITE_V2_AUTH_PROJECT;
  if (project !== legacyV2Project && project !== cleanV2Project) {
    throw Error('Dedicated v2 Supabase project required');
  }
  // Isolated verification must never accidentally target existing production.
  if (env.GM_ENVIRONMENT === 'replacement-verification' && project !== cleanV2Project) {
    throw Error('Replacement verification must target the clean project');
  }
  return project;
}

import { createLearnerProvider } from './provider';

export function configuredLearnerHost(project: string | undefined, key: string | undefined, origin: string | undefined) {
  if(!project || !key || !origin) throw Error('Complete learner configuration required');
  if(!['zgmyrpzwgtydwlzponih','sqgjsmiaprsuilcjmaav'].includes(project) || !/^sb_publishable_[A-Za-z0-9_-]+$/.test(key)) throw Error('Dedicated v2 configuration required');
  const url=new URL(origin);
  if(url.protocol!=='https:' || url.username || url.password || url.pathname!=='/' || url.search || url.hash) throw Error('HTTPS API origin required');
  return {host:createLearnerProvider(project,key),origin};
}

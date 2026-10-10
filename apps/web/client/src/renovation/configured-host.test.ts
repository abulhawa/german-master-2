import {expect,it} from 'vitest';
import {configuredLearnerHost} from './configured-host';

it.each(['zgmyrpzwgtydwlzponih','sqgjsmiaprsuilcjmaav'])('accepts the explicitly configured v2 project %s',project=>{
  expect(configuredLearnerHost(project,'sb_publishable_localfixture','https://api.example.test').origin).toBe('https://api.example.test');
});
it('rejects unknown projects and credential-bearing or insecure API origins',()=>{
  expect(()=>configuredLearnerHost('unapproved','sb_publishable_localfixture','https://api.example.test')).toThrow();
  for(const origin of ['http://api.example.test','https://user:password@api.example.test','https://api.example.test/private','https://api.example.test?key=x'])
    expect(()=>configuredLearnerHost('sqgjsmiaprsuilcjmaav','sb_publishable_localfixture',origin)).toThrow();
});

import { expect, it, vi } from 'vitest';
import { createApi } from './server';
import type { AddressInfo } from 'node:net';

it('permits only configured HTTPS origin and bounded learner preflights, without bypassing auth',async()=> {
  const authenticate=vi.fn(async()=>null); const source=vi.fn(()=>{throw Error('No unauthorized store access');});
  const server=createApi(source,authenticate,'https://learn.example');
  await new Promise<void>(resolve=>server.listen(0,'127.0.0.1',resolve));
  const base=`http://127.0.0.1:${(server.address() as AddressInfo).port}`;
  try {
    const headers={Origin:'https://learn.example','Access-Control-Request-Method':'POST','Access-Control-Request-Headers':'authorization, content-type, x-learner-subject'};
    const allowed=await fetch(`${base}/v2/sessions`,{method:'OPTIONS',headers});
    expect(allowed.status).toBe(204);expect(allowed.headers.get('Access-Control-Allow-Origin')).toBe(headers.Origin);
    expect(allowed.headers.get('Access-Control-Allow-Credentials')).toBeNull();expect(authenticate).not.toHaveBeenCalled();expect(source).not.toHaveBeenCalled();
    const request=await fetch(`${base}/v2/profile`,{headers:{Origin:headers.Origin,Authorization:'Bearer synthetic'}});
    expect(request.status).toBe(401);expect(request.headers.get('Access-Control-Allow-Origin')).toBe(headers.Origin);expect(authenticate).toHaveBeenCalledOnce();
    const denied=await fetch(`${base}/v2/sessions`,{method:'OPTIONS',headers:{...headers,Origin:'https://foreign.example'}});
    expect(denied.status).toBe(403);expect(denied.headers.get('Access-Control-Allow-Origin')).toBeNull();
    expect((await fetch(`${base}/v2/sessions`,{method:'OPTIONS',headers:{...headers,'Access-Control-Request-Headers':'authorization,x-admin-token'}})).status).toBe(400);
    expect((await fetch(`${base}/internal/publish`,{method:'OPTIONS',headers})).status).toBe(404);
    expect(authenticate).toHaveBeenCalledOnce();expect(source).not.toHaveBeenCalled();
  } finally {await new Promise<void>(resolve=>server.close(()=>resolve()));}
});

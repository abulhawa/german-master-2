import assert from 'node:assert/strict';
import { createRequire } from 'node:module';

const require = createRequire(import.meta.url);
const deepmerge = require('deepmerge');
const resolved = require.resolve('deepmerge');
assert.match(resolved.replaceAll('\\', '/'), /(?:^|\/)vendor\/deepmerge\/index\.js$|(?:^|\/)node_modules\/deepmerge\/index\.js$/, 'use the reviewed local deepmerge implementation');
assert.equal(typeof deepmerge, 'function');

function assertClean(object) {
  assert.equal(Object.getPrototypeOf(object), Object.prototype);
  assert.equal(Object.hasOwn(object, '__proto__'), false);
  assert.equal(object.poisoned, undefined);
}

const hostile = JSON.parse('{"__proto__":{"poisoned":true},"nested":{"__proto__":{"poisoned":true},"safe":2}}');
const fromSource = deepmerge({ safe: 1 }, hostile);
assertClean(fromSource);
assertClean(fromSource.nested);
assert.deepEqual(fromSource, { safe: 1, nested: { safe: 2 } });

const fromTarget = deepmerge(hostile, { safe: 3, nested: { another: 4 } });
assertClean(fromTarget);
assertClean(fromTarget.nested);
assert.equal(fromTarget.safe, 3);
assert.equal(fromTarget.nested.another, 4);
assert.equal(Object.prototype.poisoned, undefined);

const combined = deepmerge.all([{ a: { b: 1 }, list: [1] }, { a: { c: 2 }, list: [2] }]);
assert.deepEqual(combined, { a: { b: 1, c: 2 }, list: [1, 2] });
assert.deepEqual(deepmerge({list: [1]}, {list: [2]}, {arrayMerge: (_a,b) => b}), {list:[2]});
console.log('Verified locally patched deepmerge, prototype safety, recursive merges and Rollup-compatible API.');

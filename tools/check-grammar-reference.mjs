import fs from 'node:fs';
import assert from 'node:assert/strict';
const data=JSON.parse(fs.readFileSync(new URL('../content/reference/grammar-reference.json',import.meta.url),'utf8'));
assert.equal(data.schemaVersion,1);assert.equal(data.contentLanguage,'de');assert.equal(data.topics.length,14);
assert.equal(new Set(data.topics.map(t=>t.id)).size,data.topics.length);
assert.equal(data.topics.flatMap(t=>t.tables).length,16);
assert.equal(data.topics[0].tables.length,3);
for(const topic of data.topics) {
  assert.match(topic.id,/^[a-z]+(?:-[a-z]+)*$/);assert.ok(topic.title.en&&topic.title.de);
  for(const table of topic.tables) {
    assert.ok(table.title&&table.note&&table.rule);assert.ok(table.headers.length&&table.rows.length);
    for(const row of table.rows) {assert.equal(row.length,table.headers.length);assert.ok(row.every(cell=>typeof cell==='string'&&cell.length>0));}
    for(const field of ['examples','mistakes','coverageItems']) assert.ok(table[field].every(item=>typeof item==='string'));
    assert.ok(table.extraSections.every(section=>section.title&&section.items.every(item=>typeof item==='string')));
  }
}
console.log('Shared grammar reference: 14 topics, 16 complete tables.');

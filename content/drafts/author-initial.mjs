// Original agent-authored drafts. Re-running is intentional only before independent review.
import { writeFileSync } from 'node:fs';
const text = (en, de) => ({ en, de });
const id = n => `10000000-0000-4000-8000-${String(n).padStart(12, '0')}`;
const targets = [];
function add(title, objective, category, examples, explanation) {
  const n = targets.length;
  targets.push({ id: id(n), title, objective, category, level: 'B1',
    provenance: 'Original agent-authored examples for German Master 2.0; no imported dataset.',
    review: { status: 'pending', reviewer: null, date: null, notes: '', reviewedHash: null },
    variants: examples.map((e, v) => ({
      variantKey: `v-${n}-${v}`, contextKey: `context-${n}-${v}`,
      transferKey: v ? `transfer-${n}` : null,
      exercise: { schemaVersion: 1, id: id(1000 + n * 2 + v), revision: 1, targetId: id(n),
        hint: text('Identify the requested grammatical form before answering.', 'Bestimmen Sie vor der Antwort die verlangte grammatische Form.'),
        instruction: text('Complete the requested form; preserve capitalization and umlauts.', 'Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.'), ...e.exercise },
      rubric: { normalizationVersion: 'de-nfc-trim-v1', acceptedAnswers: [e.answer], explanation },
      ambiguityNotes: e.notes || 'The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.'
    })) });
}
const nouns = [['der Antrag','Anträge'],['die Rechnung','Rechnungen'],['der Termin','Termine'],['das Gespräch','Gespräche'],['der Vertrag','Verträge'],['die Erfahrung','Erfahrungen'],['das Angebot','Angebote'],['die Entscheidung','Entscheidungen'],['die Voraussetzung','Voraussetzungen'],['der Vorschlag','Vorschläge']];
for (const [lemma, plural] of nouns) add(text(`Plural: ${lemma}`, `Plural: ${lemma}`), `Recall the plural of ${lemma}.`, 'plural', [
  { exercise: { type: 'short_answer', prompt: `Schreiben Sie den Plural von „${lemma}“ ohne Artikel.` }, answer: { type: 'short_answer', text: plural } },
  { exercise: { type: 'cloze', prompt: `Ergänzen Sie die Pluralform von „${lemma}“: mehrere ___.`, slots: [{id:'plural',label:'Plural'}] }, answer: {type:'cloze',values:[{slotId:'plural',text:plural}]} }
], text(`The plural is ${plural}. Nouns are capitalized.`, `Der Plural lautet ${plural}. Nomen werden großgeschrieben.`));
const cases = [
 ['mit','dem','der','Dativ','Ich spreche mit ___ Arzt. (der Arzt)','Ich fahre mit ___ Zug. (der Zug)'],
 ['bei','der','die','Dativ','Ich bin bei ___ Ärztin. (die Ärztin)','Ich helfe bei ___ Vorbereitung. (die Vorbereitung)'],
 ['für','den','der','Akkusativ','Das ist für ___ Kunden. (der Kunde, Singular)','Ich kaufe das für ___ Kurs. (der Kurs)'],
 ['ohne','die','die','Akkusativ','Ich gehe ohne ___ Tasche. (die Tasche)','Das geht ohne ___ Zustimmung. (die Zustimmung)'],
 ['aus','dem','das','Dativ','Das kommt aus ___ Büro. (das Büro)','Ich komme aus ___ Haus. (das Haus)'],
 ['zu','der','die','Dativ','Ich gehe zu ___ Besprechung. (die Besprechung)','Ich fahre zu ___ Ausstellung. (die Ausstellung)'],
 ['nach','dem','das','Dativ','Nach ___ Gespräch gehe ich. (das Gespräch)','Nach ___ Essen lerne ich. (das Essen)'],
 ['durch','den','der','Akkusativ','Wir gehen durch ___ Park. (der Park)','Wir gehen durch ___ Eingang. (der Eingang)'],
 ['gegen','das','das','Akkusativ','Das verstößt gegen ___ Gesetz. (das Gesetz)','Ich bin gegen ___ Verbot. (das Verbot)'],
 ['seit','dem','der','Dativ','Seit ___ Umzug wohne ich hier. (der Umzug)','Seit ___ Besuch kenne ich ihn. (der Besuch)']
];
for (const [prep, answer, , grammaticalCase, ...prompts] of cases.slice(0,5)) add(text(`Article after ${prep}`, `Artikel nach ${prep}`), `Select the definite singular article after ${prep}.`, 'preposition', prompts.map(prompt => ({exercise:{type:'choice',prompt,instruction:text('Choose the definite singular article; do not contract the preposition.', 'Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition.'),options:['der','die','das','den','dem'].map(s=>({id:s,text:s}))},answer:{type:'choice',optionId:answer}})), text(`${prep} takes ${grammaticalCase}; the singular article here is ${answer}.`, `„${prep}“ verlangt den ${grammaticalCase}; der Artikel im Singular lautet hier „${answer}“.`));
const adjectives = [
 ['definite-masculine-nominative','e','Der neu___ Kollege kommt.','Der neu___ Kunde wartet.','neu'],
 ['definite-feminine-nominative','e','Die gut___ Nachricht freut mich.','Die gut___ Idee hilft uns.','gut'],
 ['definite-neuter-nominative','e','Das klein___ Büro ist frei.','Das klein___ Zimmer ist hell.','klein'],
 ['definite-masculine-accusative','en','Ich sehe den neu___ Kollegen.','Ich begrüße den neu___ Kunden.','neu'],
 ['definite-dative','en','Ich spreche mit dem nett___ Kollegen.','Ich spreche mit der nett___ Ärztin.','nett']
];
for (const [key, ending, ...data] of adjectives) {
  const lemma=data.pop();
  add(text(`Adjective ending: ${key}`, `Adjektivendung: ${key}`), `Supply the weak adjective ending for ${key}.`, 'adjective', data.map(prompt=>({exercise:{type:'cloze',prompt,instruction:text(`Type only the missing ending of ${lemma}.`, `Schreiben Sie nur die fehlende Endung von „${lemma}“.`),slots:[{id:'ending',label:'Endung'}]},answer:{type:'cloze',values:[{slotId:'ending',text:ending}]}})),text(`After the definite article in this case, the ending is -${ending}.`,`Nach dem bestimmten Artikel lautet die Endung in diesem Kasus -${ending}.`));
}
const verbs = [['fahren','fährst','fährt','mit dem Bus','nach Berlin'],['lesen','liest','liest','ein Buch','die Zeitung'],['geben','gibst','gibt','mir das Buch','ihr den Schlüssel'],['nehmen','nimmst','nimmt','den Bus','das Fahrrad'],['sprechen','sprichst','spricht','Deutsch','mit der Ärztin']];
for (const [verb, du, er, ...contexts] of verbs) add(text(`Present: ${verb}`, `Präsens: ${verb}`), `Recall singular present forms of ${verb}.`, 'verb', contexts.map(context=>({exercise:{type:'multi_slot',prompt:`Ergänzen Sie „${verb}“ im Präsens: Du ___ ${context}. Er ___ ${context}.`,slots:[{id:'du',label:'du'},{id:'er',label:'er'}]},answer:{type:'multi_slot',values:[{slotId:'du',text:du},{slotId:'er',text:er}]}})), text(`The forms are du ${du} and er ${er}.`, `Die Formen lauten: du ${du}, er ${er}.`));
for (const conjunction of ['weil','dass','obwohl','wenn','ob']) add(text(`Verb position: ${conjunction}`, `Verbstellung: ${conjunction}`), `Place the finite verb last after ${conjunction}.`, 'word-order', ['ich Deutsch lerne','sie Kaffee trinkt'].map(clause=>{
  const words=[conjunction,...clause.split(' ')];
  const tokens=[words[3],words[0],words[2],words[1]].map((s,i)=>({id:String(i),text:s}));
  return {exercise:{type:'word_order',prompt:tokens.map(t=>t.text).join(' / '),instruction:text('Build a subordinate clause: conjunction, subject, object, verb.', 'Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb.'),tokens},answer:{type:'word_order',tokenIds:['1','3','2','0']},notes:'An isolated subordinate clause is requested, not a complete main sentence. Instruction fixes constituent order.'};
}), text('The finite verb ends the subordinate clause.', 'Das finite Verb steht am Ende des Nebensatzes.'));
writeFileSync(new URL('./initial-30.json', import.meta.url), JSON.stringify({schemaVersion:1,status:'agent-authored-draft',publicationApproved:false,targets},null,2)+'\n');

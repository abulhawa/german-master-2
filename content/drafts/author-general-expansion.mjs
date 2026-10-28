// Original pilot only. Never overwrite editorial work or publish from this script.
import {existsSync, writeFileSync} from 'node:fs';
const destination = new URL('./general-expansion.json', import.meta.url);
if (existsSync(destination)) throw Error('Draft exists; edit in place to preserve review.');
const localized = (en,de) => ({en,de});
const id = n => `70000000-0000-4000-8000-${String(n).padStart(12,'0')}`;
const targets = [];
function add(level,en,de,explanationEn,explanationDe,examples) {
  const n = targets.length;
  targets.push({id:id(n),title:localized(en,de),description:localized(en,de),objective:en,
    category:'general_expansion',level,
    provenance:'Original Codex-authored general German pilot, 28 October 2026. AI-authored draft; no independent review or CEFR certification. No external dataset.',
    review:{status:'pending',reviewer:null,date:null,notes:'Awaiting editorial review of grammar, naturalness, distractors, alternatives, hints, level and distinct contexts.',reviewedHash:null},
    variants:examples.map(([prompt,answer,distractors],v)=>{
      const options=[...distractors]; options.splice((n+v)%4,0,answer);
      return {variantKey:`general-expansion-${n}-${v}`,contextKey:`general-expansion-context-${n}-${v}`,
        transferKey:`general-expansion-transfer-${n}-${v}`,
        exercise:{schemaVersion:1,id:id(1000+n*2+v),revision:1,targetId:id(n),type:'gap_choice',prompt,
          instruction:localized('Choose the form that completes the sentence.','Wähle die Form, die den Satz ergänzt.'),
          hint:localized('Check the grammatical relationship in the whole sentence.','Achte auf den grammatischen Zusammenhang im ganzen Satz.'),
          slots:[{id:'form',label:'Form',options:options.map((text,i)=>({id:String(i),text}))}]},
        rubric:{normalizationVersion:'de-nfc-trim-v1',acceptedAnswers:[{type:'gap_choice',selections:[{slotId:'form',optionId:String(options.indexOf(answer))}]}],
          explanation:localized(explanationEn,explanationDe),
          completedAnswer:{schemaVersion:1,parts:[{text:prompt.replace('___',answer),emphasis:false}]}},
        ambiguityNotes:'Pending AI editorial review: check every offered option in this exact context. A unique offered answer does not rule out other valid free-text paraphrases; context variation is not measured learning transfer.'};
    })});
}
add('B1','Perfect tense with haben','Perfekt mit haben',
  'These transitive verbs form the perfect with haben; the finite auxiliary agrees with the subject.',
  'Diese transitiven Verben bilden das Perfekt mit haben. Das Hilfsverb richtet sich nach dem Subjekt.',[
  ['Ich ___ gestern die Rechnung bezahlt.','habe',['bin','ist','habt']],
  ['Wir ___ am Samstag einen Kuchen gebacken.','haben',['sind','hat','seid']]]);
add('B1','Perfect tense with sein: destination','Perfekt mit sein: Ortswechsel',
  'Fahren and gehen form the perfect with sein when expressing movement to a destination.',
  'Fahren und gehen bilden bei einem Ortswechsel das Perfekt mit sein.',[
  ['Sie ___ gestern mit dem Zug nach Köln gefahren. (sie = eine Frau)','ist',['hat','haben','seid']],
  ['Wir ___ nach dem Essen nach Hause gegangen.','sind',['haben','ist','habt']]]);
add('B1','Reflexive pronouns: accusative','Reflexivpronomen im Akkusativ',
  'Sich beeilen and sich erinnern use accusative reflexive pronouns: ich/mich and wir/uns.',
  'Sich beeilen und sich erinnern verlangen hier ein Reflexivpronomen im Akkusativ: ich/mich und wir/uns.',[
  ['Ich muss ___ beeilen, sonst verpasse ich den Bus.','mich',['mir','sich','euch']],
  ['Wir erinnern ___ gern an den Urlaub.','uns',['sich','euch','mich']]]);
add('B1','Possessive articles: feminine nominative','Possessivartikel: Nominativ feminin',
  'Before a feminine singular noun in the nominative, use meine or unsere. The owner determines the stem.',
  'Vor einem femininen Nomen im Nominativ Singular steht meine oder unsere. Der Besitzer bestimmt den Wortstamm.',[
  ['Das ist ___ Schwester. (Besitzer: ich)','meine',['mein','meinen','meinem']],
  ['___ Wohnung liegt im dritten Stock. (Besitzer: wir)','Unsere',['Unser','Unseren','Unserem']]]);
add('B1','Seit with the dative: ongoing duration','Seit mit Dativ: andauernder Zeitraum',
  'Seit takes the dative. These present-tense sentences describe a situation that began earlier and is still true.',
  'Nach seit steht der Dativ. Diese Sätze im Präsens beschreiben einen früher begonnenen Zustand, der noch andauert.',[
  ['Ich wohne seit ___ Jahr in Berlin. (ein Jahr)','einem',['ein','einen','eines']],
  ['Sie lernt seit ___ Monat Deutsch. (ein Monat)','einem',['ein','einen','eines']]]);
add('B2','Comparisons with je … desto','Vergleiche mit je … desto',
  'A comparative clause introduced by je can be paired with desto plus another comparative.',
  'Ein Vergleichssatz mit je kann mit desto und einem weiteren Komparativ verbunden werden.',[
  ['Je früher wir buchen, ___ günstiger sind die Tickets.','desto',['denn','obwohl','sobald']],
  ['Je mehr sie übt, ___ sicherer spricht sie.','desto',['weil','damit','während']]]);
add('B2','Paired conjunction: weder … noch','Doppelkonjunktion: weder … noch',
  'Weder … noch excludes both coordinated alternatives.',
  'Weder … noch schließt beide verbundenen Möglichkeiten aus.',[
  ['Ich trinke weder Kaffee ___ schwarzen Tee.','noch',['oder','aber','denn']],
  ['Die Wohnung hat weder einen Balkon ___ einen Garten.','noch',['und','sondern','weil']]]);
add('B2','Paired conjunction: sowohl … als auch','Doppelkonjunktion: sowohl … als auch',
  'Sowohl … als auch includes both coordinated alternatives.',
  'Sowohl … als auch bezieht beide verbundenen Möglichkeiten ein.',[
  ['Der Kurs richtet sich sowohl an Studierende ___ an Berufstätige.','als auch',['sondern','obwohl','weder']],
  ['Sie spricht sowohl Deutsch ___ Französisch.','als auch',['noch','aber','denn']]]);
add('B2','Infinitive clauses with statt … zu','Infinitivgruppen mit statt … zu',
  'Statt can introduce an infinitive clause with zu when the understood subject is the same.',
  'Statt kann bei gleichem gedachten Subjekt eine Infinitivgruppe mit zu einleiten.',[
  ['Statt lange ___ warten, rufen wir direkt an.','zu',['um','dass','als']],
  ['Er fährt mit dem Fahrrad, statt den Bus ___ nehmen.','zu',['um','dass','als']]]);
add('B2','Lassen with a bare infinitive','Lassen mit Infinitiv ohne zu',
  'Lassen takes an infinitive without zu. Here it means arranging for someone else to do something.',
  'Lassen steht mit einem Infinitiv ohne zu. Hier bedeutet es, dass jemand anderes etwas erledigt.',[
  ['Ich lasse mein Fahrrad morgen ___. (reparieren)','reparieren',['zu reparieren','repariert','reparierst']],
  ['Wir lassen die Wohnung nächste Woche ___. (streichen)','streichen',['zu streichen','gestrichen','streichst']]]);
writeFileSync(destination,JSON.stringify({schemaVersion:1,status:'agent-authored-draft',publicationApproved:false,targets},null,2)+'\n');
console.log('Prepared 10 pending targets / 20 unpublished general-German pilot questions.');

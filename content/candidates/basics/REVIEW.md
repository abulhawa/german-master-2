# Basic practice candidate: editorial review

65 targets / 125 exercises: existing five B1 exercises, 60 prepared ChatGPT B1 exercises, 60 new Codex B2 exercises. This workbook describes source review drafts; independent German review remains pending. The separately owner-authorized live release is recorded in docs/operations/basic-content-release.md.

The B2 label selects a scaffolded practice pack for B2 learners; it is not a claim that every individual form is exclusive to B2 or that this assesses proficiency.

Check naturalness, requested form, alternatives, hint leakage, explanation, level suitability and context variation. Record independent sign-off in the source JSON with its matching content hash. AI editorial work is not human approval. The SQL installs only drafts in an isolated review database; do not use it to activate production.

## Additions

### B1: Plural: der Antrag

Target: 10000000-0000-4000-8000-000000000000; category: plural; review: pending.

Content hash: 96c9e92afb8d07eb202bd28389adabf6f222bec502e46acf962db49d5c30f8d3

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-0-0** — Schreibe den Plural von „der Antrag“ ohne Artikel.

Accepted: `[{"type":"short_answer","text":"Anträge"}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Anträge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

**v-0-1** — Für die Förderung liegen mehrere ___ vor. (Plural von „der Antrag“)

Accepted: `[{"type":"cloze","values":[{"slotId":"plural","text":"Anträge"}]}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Anträge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

### B1: Plural: die Rechnung

Target: 10000000-0000-4000-8000-000000000001; category: plural; review: pending.

Content hash: 885f63e63db28e29de1f9771103da24a11bbe56b0f57d5ef15e86bda2fe3262d

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-1-0** — Schreibe den Plural von „die Rechnung“ ohne Artikel.

Accepted: `[{"type":"short_answer","text":"Rechnungen"}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Rechnungen ist der Standardplural. Der Plural bekommt die Endung -en.

The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

**v-1-1** — Im Ordner liegen noch drei offene ___. (Plural von „die Rechnung“)

Accepted: `[{"type":"cloze","values":[{"slotId":"plural","text":"Rechnungen"}]}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Rechnungen ist der Standardplural. Der Plural bekommt die Endung -en.

The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

### B1: Plural: der Termin

Target: 10000000-0000-4000-8000-000000000002; category: plural; review: pending.

Content hash: fd1f9d587bf835483c692cb9dc54a7e49c179396f8930276436fff1c75b33ab7

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-2-0** — Schreibe den Plural von „der Termin“ ohne Artikel.

Accepted: `[{"type":"short_answer","text":"Termine"}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Termine ist der Standardplural. Der Plural bekommt die Endung -e.

The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

**v-2-1** — Nächste Woche habe ich zwei wichtige ___. (Plural von „der Termin“)

Accepted: `[{"type":"cloze","values":[{"slotId":"plural","text":"Termine"}]}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Termine ist der Standardplural. Der Plural bekommt die Endung -e.

The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

### B1: Plural: das Gespräch

Target: 10000000-0000-4000-8000-000000000003; category: plural; review: pending.

Content hash: aa88f06c38d5c405c50f1533318b6be9d95c78ced77778e6f94da475befe6831

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-3-0** — Schreibe den Plural von „das Gespräch“ ohne Artikel.

Accepted: `[{"type":"short_answer","text":"Gespräche"}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Gespräche ist der Standardplural. Der Plural bekommt -e; der Stammvokal bleibt ä.

The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

**v-3-1** — Heute stehen noch zwei schwierige ___ an. (Plural von „das Gespräch“)

Accepted: `[{"type":"cloze","values":[{"slotId":"plural","text":"Gespräche"}]}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Gespräche ist der Standardplural. Der Plural bekommt -e; der Stammvokal bleibt ä.

The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

### B1: Plural: der Vertrag

Target: 10000000-0000-4000-8000-000000000004; category: plural; review: pending.

Content hash: caaaaf6820092b78617e7cdd71ec2cbf4659bf4f5c485c2395143e9b93aa2906

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-4-0** — Schreibe den Plural von „der Vertrag“ ohne Artikel.

Accepted: `[{"type":"short_answer","text":"Verträge"}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Verträge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

**v-4-1** — Die Firma hat mehrere neue ___ abgeschlossen. (Plural von „der Vertrag“)

Accepted: `[{"type":"cloze","values":[{"slotId":"plural","text":"Verträge"}]}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Verträge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

### B1: Plural: die Erfahrung

Target: 10000000-0000-4000-8000-000000000005; category: plural; review: pending.

Content hash: ea651120e2544c18d3d190675a41dcd438eb831d912da67d511c3a61d71022a9

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-5-0** — Schreibe den Plural von „die Erfahrung“ ohne Artikel.

Accepted: `[{"type":"short_answer","text":"Erfahrungen"}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Erfahrungen ist der Standardplural. Der Plural bekommt die Endung -en.

The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

**v-5-1** — Im Lebenslauf beschreibt sie ihre beruflichen ___. (Plural von „die Erfahrung“)

Accepted: `[{"type":"cloze","values":[{"slotId":"plural","text":"Erfahrungen"}]}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Erfahrungen ist der Standardplural. Der Plural bekommt die Endung -en.

The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

### B1: Plural: das Angebot

Target: 10000000-0000-4000-8000-000000000006; category: plural; review: pending.

Content hash: f2f19786b03bb7bea52f7cddccad00b76c4253de6c27a72d3355deaa2f495256

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-6-0** — Schreibe den Plural von „das Angebot“ ohne Artikel.

Accepted: `[{"type":"short_answer","text":"Angebote"}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Angebote ist der Standardplural. Der Plural bekommt die Endung -e.

The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

**v-6-1** — Wir vergleichen drei verschiedene ___. (Plural von „das Angebot“)

Accepted: `[{"type":"cloze","values":[{"slotId":"plural","text":"Angebote"}]}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Angebote ist der Standardplural. Der Plural bekommt die Endung -e.

The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

### B1: Plural: die Entscheidung

Target: 10000000-0000-4000-8000-000000000007; category: plural; review: pending.

Content hash: 04e3d6aa7b67971612ed4019b63ae4674a0dabfb155d7e7043e38be462bc3745

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-7-0** — Schreibe den Plural von „die Entscheidung“ ohne Artikel.

Accepted: `[{"type":"short_answer","text":"Entscheidungen"}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Entscheidungen ist der Standardplural. Der Plural bekommt die Endung -en.

The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

**v-7-1** — Das Team muss heute mehrere wichtige ___ treffen. (Plural von „die Entscheidung“)

Accepted: `[{"type":"cloze","values":[{"slotId":"plural","text":"Entscheidungen"}]}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Entscheidungen ist der Standardplural. Der Plural bekommt die Endung -en.

The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

### B1: Plural: die Voraussetzung

Target: 10000000-0000-4000-8000-000000000008; category: plural; review: pending.

Content hash: 033471a16a9c7a5324087e80b28841c8f02297dab6e3755c76c1951940f877fa

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-8-0** — Schreibe den Plural von „die Voraussetzung“ ohne Artikel.

Accepted: `[{"type":"short_answer","text":"Voraussetzungen"}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Voraussetzungen ist der Standardplural. Der Plural bekommt die Endung -en.

The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

**v-8-1** — Für die Stelle musst du mehrere ___ erfüllen. (Plural von „die Voraussetzung“)

Accepted: `[{"type":"cloze","values":[{"slotId":"plural","text":"Voraussetzungen"}]}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Voraussetzungen ist der Standardplural. Der Plural bekommt die Endung -en.

The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

### B1: Plural: der Vorschlag

Target: 10000000-0000-4000-8000-000000000009; category: plural; review: pending.

Content hash: 1ee42ab05bbdcd25098e29116b71cf4290be0ba4059a32f12c1aa96ebe83ef05

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-9-0** — Schreibe den Plural von „der Vorschlag“ ohne Artikel.

Accepted: `[{"type":"short_answer","text":"Vorschläge"}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Vorschläge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

**v-9-1** — Im Meeting wurden drei konkrete ___ gemacht. (Plural von „der Vorschlag“)

Accepted: `[{"type":"cloze","values":[{"slotId":"plural","text":"Vorschläge"}]}]`

Hint: Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert.

Explanation: Vorschläge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

### B1: Dativartikel nach „mit“

Target: 10000000-0000-4000-8000-000000000010; category: preposition; review: pending.

Content hash: 7c4c1fc924f5b49df585b0ebba26a54e90ed56b4c00ae708f9044db4c507d18c

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-10-0** — Ich bespreche die Ergebnisse mit ___ Arzt. (der Arzt)

Accepted: `[{"type":"choice","optionId":"dem"}]`

Hint: „mit“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an.

Explanation: „mit“ verlangt den Dativ. „der Arzt“ ist maskulin; im Dativ Singular lautet der bestimmte Artikel „dem“.

The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

**v-10-1** — Morgen fahre ich mit ___ Zug nach Hamburg. (der Zug)

Accepted: `[{"type":"choice","optionId":"dem"}]`

Hint: „mit“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an.

Explanation: „mit“ verlangt den Dativ. „der Zug“ ist maskulin; im Dativ Singular lautet der bestimmte Artikel „dem“.

The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### B1: Dativartikel nach „bei“

Target: 10000000-0000-4000-8000-000000000011; category: preposition; review: pending.

Content hash: 05b8deded4c1004208e3cca0d44f6a7be54f860104d5d0ec5368726c2b11f5a7

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-11-0** — Wegen der Schmerzen bin ich heute bei ___ Ärztin. (die Ärztin)

Accepted: `[{"type":"choice","optionId":"der"}]`

Hint: „bei“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an.

Explanation: „bei“ verlangt den Dativ. „die Ärztin“ ist feminin; im Dativ Singular lautet der bestimmte Artikel „der“.

The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

**v-11-1** — Wir brauchen Hilfe bei ___ Vorbereitung der Präsentation. (die Vorbereitung)

Accepted: `[{"type":"choice","optionId":"der"}]`

Hint: „bei“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an.

Explanation: „bei“ verlangt den Dativ. „die Vorbereitung“ ist feminin; im Dativ Singular lautet der bestimmte Artikel „der“.

The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### B1: Akkusativartikel nach „für“

Target: 10000000-0000-4000-8000-000000000012; category: preposition; review: pending.

Content hash: d0818e9e9d5ec193a64d73552165a6d2aa179256bf95e95ab8cdcaf16253b614

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-12-0** — Dieses Formular ist für ___ Kunden am Schalter. (der Kunde, Singular)

Accepted: `[{"type":"choice","optionId":"den"}]`

Hint: „für“ verlangt immer den Akkusativ. Passe den Artikel an das Genus des Nomens an.

Explanation: „für“ verlangt den Akkusativ. „der Kunde“ ist maskulin; im Akkusativ Singular lautet der bestimmte Artikel „den“.

The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

**v-12-1** — Ich buche den Raum für ___ Kurs am Abend. (der Kurs)

Accepted: `[{"type":"choice","optionId":"den"}]`

Hint: „für“ verlangt immer den Akkusativ. Passe den Artikel an das Genus des Nomens an.

Explanation: „für“ verlangt den Akkusativ. „der Kurs“ ist maskulin; im Akkusativ Singular lautet der bestimmte Artikel „den“.

The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### B1: Akkusativartikel nach „ohne“

Target: 10000000-0000-4000-8000-000000000013; category: preposition; review: pending.

Content hash: 7b4c56ccd06c51583c094041cf4127063aaacc0602b264dafbcfd1e855cdf7b9

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-13-0** — Sie geht nie ohne ___ Tasche aus dem Haus. (die Tasche)

Accepted: `[{"type":"choice","optionId":"die"}]`

Hint: „ohne“ verlangt immer den Akkusativ. Passe den Artikel an das Genus des Nomens an.

Explanation: „ohne“ verlangt den Akkusativ. „die Tasche“ ist feminin; im Akkusativ Singular bleibt der bestimmte Artikel „die“.

The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

**v-13-1** — Wir dürfen das nicht ohne ___ Zustimmung veröffentlichen. (die Zustimmung)

Accepted: `[{"type":"choice","optionId":"die"}]`

Hint: „ohne“ verlangt immer den Akkusativ. Passe den Artikel an das Genus des Nomens an.

Explanation: „ohne“ verlangt den Akkusativ. „die Zustimmung“ ist feminin; im Akkusativ Singular bleibt der bestimmte Artikel „die“.

The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### B1: Dativartikel nach „aus“

Target: 10000000-0000-4000-8000-000000000014; category: preposition; review: pending.

Content hash: 5e99de0a26a422c7fb0b50551fa8c7f219bfe526241154f316939540a2e6086d

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-14-0** — Die Unterlagen kommen aus ___ Büro im Erdgeschoss. (das Büro)

Accepted: `[{"type":"choice","optionId":"dem"}]`

Hint: „aus“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an.

Explanation: „aus“ verlangt den Dativ. „das Büro“ ist neutral; im Dativ Singular lautet der bestimmte Artikel „dem“.

The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

**v-14-1** — Wir hören Musik aus ___ Haus nebenan. (das Haus)

Accepted: `[{"type":"choice","optionId":"dem"}]`

Hint: „aus“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an.

Explanation: „aus“ verlangt den Dativ. „das Haus“ ist neutral; im Dativ Singular lautet der bestimmte Artikel „dem“.

The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### B1: Adjektivendung: Maskulinum im Nominativ nach „der“

Target: 10000000-0000-4000-8000-000000000015; category: adjective; review: pending.

Content hash: 7453c8e0daa4c45cb2815c60cbda345fcc54f6d20327ca32ddfebce533cf421f

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-15-0** — Der neu___ Kollege beginnt heute.

Accepted: `[{"type":"cloze","values":[{"slotId":"ending","text":"e"}]}]`

Hint: Der bestimmte Artikel markiert bereits Maskulinum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination.

Explanation: Nach „der“ im maskulinen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

The article, noun and sentence role make the case/gender reading explicit; the blank asks only for the adjective ending.

**v-15-1** — Der freundlich___ Kunde wartet am Empfang.

Accepted: `[{"type":"cloze","values":[{"slotId":"ending","text":"e"}]}]`

Hint: Der bestimmte Artikel markiert bereits Maskulinum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination.

Explanation: Nach „der“ im maskulinen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

The article, noun and sentence role make the case/gender reading explicit; the blank asks only for the adjective ending.

### B1: Adjektivendung: Femininum im Nominativ nach „die“

Target: 10000000-0000-4000-8000-000000000016; category: adjective; review: pending.

Content hash: ddc28979ffaac8c1383dc8d5c12d6a55e3a61ccaa51ec703200f159c5e4d558b

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-16-0** — Die neu___ Kollegin arbeitet im Vertrieb.

Accepted: `[{"type":"cloze","values":[{"slotId":"ending","text":"e"}]}]`

Hint: Der bestimmte Artikel markiert bereits Femininum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination.

Explanation: Nach „die“ im femininen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

The article, noun and sentence role make the case/gender reading explicit; the blank asks only for the adjective ending.

**v-16-1** — Die wichtig___ Frage bleibt offen.

Accepted: `[{"type":"cloze","values":[{"slotId":"ending","text":"e"}]}]`

Hint: Der bestimmte Artikel markiert bereits Femininum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination.

Explanation: Nach „die“ im femininen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

The article, noun and sentence role make the case/gender reading explicit; the blank asks only for the adjective ending.

### B1: Adjektivendung: Neutrum im Nominativ nach „das“

Target: 10000000-0000-4000-8000-000000000017; category: adjective; review: pending.

Content hash: 688f2b0979b053cc8a28480538a02681347fe3ac5ce6b5dacfe7e45e286c357c

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-17-0** — Das klein___ Büro ist frei.

Accepted: `[{"type":"cloze","values":[{"slotId":"ending","text":"e"}]}]`

Hint: Der bestimmte Artikel markiert bereits Neutrum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination.

Explanation: Nach „das“ im neutralen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

The article, noun and sentence role make the case/gender reading explicit; the blank asks only for the adjective ending.

**v-17-1** — Das neu___ Gerät funktioniert gut.

Accepted: `[{"type":"cloze","values":[{"slotId":"ending","text":"e"}]}]`

Hint: Der bestimmte Artikel markiert bereits Neutrum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination.

Explanation: Nach „das“ im neutralen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

The article, noun and sentence role make the case/gender reading explicit; the blank asks only for the adjective ending.

### B1: Adjektivendung: Maskulinum im Akkusativ nach „den“

Target: 10000000-0000-4000-8000-000000000018; category: adjective; review: pending.

Content hash: 0efe8ae5aefcdafd8b070659b4cf0f55093505d55db2627e7dd9e8c265e0a9af

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-18-0** — Ich sehe den neu___ Kollegen.

Accepted: `[{"type":"cloze","values":[{"slotId":"ending","text":"en"}]}]`

Hint: Nach einem bestimmten Artikel im maskulinen Akkusativ folgt das Adjektiv der schwachen Deklination.

Explanation: Nach „den“ im maskulinen Akkusativ bekommt ein schwach dekliniertes Adjektiv die Endung -en.

The article, noun and sentence role make the case/gender reading explicit; the blank asks only for the adjective ending.

**v-18-1** — Wir begrüßen den wichtig___ Kunden.

Accepted: `[{"type":"cloze","values":[{"slotId":"ending","text":"en"}]}]`

Hint: Nach einem bestimmten Artikel im maskulinen Akkusativ folgt das Adjektiv der schwachen Deklination.

Explanation: Nach „den“ im maskulinen Akkusativ bekommt ein schwach dekliniertes Adjektiv die Endung -en.

The article, noun and sentence role make the case/gender reading explicit; the blank asks only for the adjective ending.

### B1: Adjektivendung: Dativ nach bestimmtem Artikel

Target: 10000000-0000-4000-8000-000000000019; category: adjective; review: pending.

Content hash: 7e0a39f4c7228efcecb0e20a2615bb118012a8997ea6b5227ca9d5945db88517

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-19-0** — Ich spreche mit dem nett___ Kollegen.

Accepted: `[{"type":"cloze","values":[{"slotId":"ending","text":"en"}]}]`

Hint: Nach einem bestimmten Artikel im Dativ haben schwach deklinierte Adjektive in diesen Genera dieselbe Endung.

Explanation: Nach einem bestimmten Artikel im Dativ bekommt ein schwach dekliniertes Adjektiv die Endung -en.

The article, noun and sentence role make the case/gender reading explicit; the blank asks only for the adjective ending.

**v-19-1** — Sie arbeitet mit der erfahren___ Ärztin.

Accepted: `[{"type":"cloze","values":[{"slotId":"ending","text":"en"}]}]`

Hint: Nach einem bestimmten Artikel im Dativ haben schwach deklinierte Adjektive in diesen Genera dieselbe Endung.

Explanation: Nach einem bestimmten Artikel im Dativ bekommt ein schwach dekliniertes Adjektiv die Endung -en.

The article, noun and sentence role make the case/gender reading explicit; the blank asks only for the adjective ending.

### B1: Präsens: fahren (du/er)

Target: 10000000-0000-4000-8000-000000000020; category: verb; review: pending.

Content hash: a9583570d321f5b927f91afbec51d2d39b8a0feab09efaccf07fbf13e28245a6

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-20-0** — Du ___ jeden Morgen mit dem Bus. Er ___ heute mit dem Zug.

Accepted: `[{"type":"multi_slot","values":[{"slotId":"du","text":"fährst"},{"slotId":"er","text":"fährt"}]}]`

Hint: Bei „fahren“ wird in den Formen mit „du“ und „er“ der Stammvokal a zu ä.

Explanation: Der Stammvokal wechselt a → ä: du fährst, er fährt.

Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

**v-20-1** — Du ___ morgen nach Leipzig. Er ___ am Wochenende zu seinen Eltern.

Accepted: `[{"type":"multi_slot","values":[{"slotId":"du","text":"fährst"},{"slotId":"er","text":"fährt"}]}]`

Hint: Bei „fahren“ wird in den Formen mit „du“ und „er“ der Stammvokal a zu ä.

Explanation: Der Stammvokal wechselt a → ä: du fährst, er fährt.

Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

### B1: Präsens: lesen (du/er)

Target: 10000000-0000-4000-8000-000000000021; category: verb; review: pending.

Content hash: 968aff47fbe9ab66528caa002de0d99679911854e0069c9d756a477fd7517a54

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-21-0** — Du ___ gerade den Bericht. Er ___ jeden Morgen die Zeitung.

Accepted: `[{"type":"multi_slot","values":[{"slotId":"du","text":"liest"},{"slotId":"er","text":"liest"}]}]`

Hint: Bei „lesen“ wird in den Formen mit „du“ und „er“ e zu ie.

Explanation: Der Stammvokal wechselt e → ie; beide Formen lauten „liest“: du liest, er liest.

Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

**v-21-1** — Du ___ die Nachricht noch einmal. Er ___ oft deutsche Romane.

Accepted: `[{"type":"multi_slot","values":[{"slotId":"du","text":"liest"},{"slotId":"er","text":"liest"}]}]`

Hint: Bei „lesen“ wird in den Formen mit „du“ und „er“ e zu ie.

Explanation: Der Stammvokal wechselt e → ie; beide Formen lauten „liest“: du liest, er liest.

Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

### B1: Präsens: geben (du/er)

Target: 10000000-0000-4000-8000-000000000022; category: verb; review: pending.

Content hash: 86043eddf17ca21891a2000b6474a10467d8bf5c8bcade21784f77ec766c0dd0

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-22-0** — Du ___ mir bitte den Schlüssel. Er ___ der Kollegin die Unterlagen.

Accepted: `[{"type":"multi_slot","values":[{"slotId":"du","text":"gibst"},{"slotId":"er","text":"gibt"}]}]`

Hint: Bei „geben“ wird in den Formen mit „du“ und „er“ e zu i.

Explanation: Der Stammvokal wechselt e → i: du gibst, er gibt.

Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

**v-22-1** — Du ___ dem Kind Wasser. Er ___ uns eine klare Antwort.

Accepted: `[{"type":"multi_slot","values":[{"slotId":"du","text":"gibst"},{"slotId":"er","text":"gibt"}]}]`

Hint: Bei „geben“ wird in den Formen mit „du“ und „er“ e zu i.

Explanation: Der Stammvokal wechselt e → i: du gibst, er gibt.

Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

### B1: Präsens: nehmen (du/er)

Target: 10000000-0000-4000-8000-000000000023; category: verb; review: pending.

Content hash: dbfdae355e0dafcde0b7128eb6256822ca3b9784429dc96938ef5d57d408efd0

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-23-0** — Du ___ morgens den Bus. Er ___ lieber das Fahrrad.

Accepted: `[{"type":"multi_slot","values":[{"slotId":"du","text":"nimmst"},{"slotId":"er","text":"nimmt"}]}]`

Hint: Die Formen mit „du“ und „er“ verwenden den unregelmäßigen Stamm „nimm-“.

Explanation: Im Singular lautet der Stamm „nimm-“: du nimmst, er nimmt.

Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

**v-23-1** — Du ___ noch einen Kaffee. Er ___ die letzte Tablette.

Accepted: `[{"type":"multi_slot","values":[{"slotId":"du","text":"nimmst"},{"slotId":"er","text":"nimmt"}]}]`

Hint: Die Formen mit „du“ und „er“ verwenden den unregelmäßigen Stamm „nimm-“.

Explanation: Im Singular lautet der Stamm „nimm-“: du nimmst, er nimmt.

Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

### B1: Präsens: sprechen (du/er)

Target: 10000000-0000-4000-8000-000000000024; category: verb; review: pending.

Content hash: 4886556e248473f2e2483938d6a7814f5b6c852927129a5d0cd2db402a9d709e

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-24-0** — Du ___ sehr gut Deutsch. Er ___ heute mit der Ärztin.

Accepted: `[{"type":"multi_slot","values":[{"slotId":"du","text":"sprichst"},{"slotId":"er","text":"spricht"}]}]`

Hint: Bei „sprechen“ wird in den Formen mit „du“ und „er“ e zu i.

Explanation: Der Stammvokal wechselt e → i: du sprichst, er spricht.

Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

**v-24-1** — Du ___ morgen mit deinem Chef. Er ___ oft über seine Arbeit.

Accepted: `[{"type":"multi_slot","values":[{"slotId":"du","text":"sprichst"},{"slotId":"er","text":"spricht"}]}]`

Hint: Bei „sprechen“ wird in den Formen mit „du“ und „er“ e zu i.

Explanation: Der Stammvokal wechselt e → i: du sprichst, er spricht.

Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

### B1: Nebensatz mit „weil“

Target: 10000000-0000-4000-8000-000000000025; category: word-order; review: pending.

Content hash: 6a831b4165c1c0c550d0bc06c0ccfb38e48c71ae1638108b26011474154bfcd7

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-25-0** — arbeite / weil / heute / ich

Accepted: `[{"type":"word_order","tokenIds":["1","3","2","0"]}]`

Hint: Nach „weil“ steht das finite Verb am Ende des Satzes.

Explanation: Das finite Verb steht am Ende: weil ich heute arbeite.

German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

**v-25-1** — braucht / weil / Hilfe / sie

Accepted: `[{"type":"word_order","tokenIds":["1","3","2","0"]}]`

Hint: Nach „weil“ steht das finite Verb am Ende des Satzes.

Explanation: Das finite Verb steht am Ende: weil sie Hilfe braucht.

German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

### B1: Nebensatz mit „dass“

Target: 10000000-0000-4000-8000-000000000026; category: word-order; review: pending.

Content hash: a13ed62d1c6010bc59ac1a63482e61c060f727da9f2c872347db77039832d166

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-26-0** — starten / dass / morgen / wir

Accepted: `[{"type":"word_order","tokenIds":["1","3","2","0"]}]`

Hint: Nach „dass“ steht das finite Verb am Ende des Satzes.

Explanation: Das finite Verb steht am Ende: dass wir morgen starten.

German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

**v-26-1** — bestätigt / dass / den Termin / er

Accepted: `[{"type":"word_order","tokenIds":["1","3","2","0"]}]`

Hint: Nach „dass“ steht das finite Verb am Ende des Satzes.

Explanation: Das finite Verb steht am Ende: dass er den Termin bestätigt.

German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

### B1: Nebensatz mit „obwohl“

Target: 10000000-0000-4000-8000-000000000027; category: word-order; review: pending.

Content hash: 44349f9b0efaa9fb61a1f0133bd07db119e444052c43485332a59ad7a3626044

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-27-0** — bin / obwohl / müde / ich

Accepted: `[{"type":"word_order","tokenIds":["1","3","2","0"]}]`

Hint: Nach „obwohl“ steht das finite Verb am Ende des Satzes.

Explanation: Das finite Verb steht am Ende: obwohl ich müde bin.

German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

**v-27-1** — hat / obwohl / wenig Zeit / sie

Accepted: `[{"type":"word_order","tokenIds":["1","3","2","0"]}]`

Hint: Nach „obwohl“ steht das finite Verb am Ende des Satzes.

Explanation: Das finite Verb steht am Ende: obwohl sie wenig Zeit hat.

German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

### B1: Nebensatz mit „wenn“

Target: 10000000-0000-4000-8000-000000000028; category: word-order; review: pending.

Content hash: d6c0468cf13bf17e0ba052e1c7ce560cd8b646d48487397c1767db3bbd195239

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-28-0** — habe / wenn / Zeit / ich

Accepted: `[{"type":"word_order","tokenIds":["1","3","2","0"]}]`

Hint: Nach „wenn“ steht das finite Verb am Ende des Satzes.

Explanation: Das finite Verb steht am Ende: wenn ich Zeit habe.

German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

**v-28-1** — kommt / wenn / pünktlich / der Zug

Accepted: `[{"type":"word_order","tokenIds":["1","3","2","0"]}]`

Hint: Nach „wenn“ steht das finite Verb am Ende des Satzes.

Explanation: Das finite Verb steht am Ende: wenn der Zug pünktlich kommt.

German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

### B1: Indirekte Frage mit „ob“

Target: 10000000-0000-4000-8000-000000000029; category: word-order; review: pending.

Content hash: d3d07ba4070afcce369c77d5c32a9837be96c831c2b7446224bd2be0af6d4378

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-29-0** — kommst / ob / morgen / du

Accepted: `[{"type":"word_order","tokenIds":["1","3","2","0"]}]`

Hint: Nach „ob“ steht das finite Verb am Ende des Satzes.

Explanation: Das finite Verb steht am Ende: ob du morgen kommst.

German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

**v-29-1** — hat / ob / genug Zeit / sie

Accepted: `[{"type":"word_order","tokenIds":["1","3","2","0"]}]`

Hint: Nach „ob“ steht das finite Verb am Ende des Satzes.

Explanation: Das finite Verb steht am Ende: ob sie genug Zeit hat.

German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

### B2: Passiv im Präsens

Target: 40000000-0000-4000-8000-000000000000; category: passive; review: pending.

Content hash: b7fda3cb3308ca7f0185d01bf451f6173dd57389506ee7c7daabc1f83f0f6b7c

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-0-0** — Der Antrag ___ von der Behörde geprüft. (werden, Präsens)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"wird"}]}]`

Hint: Verwende werden und das Partizip II.

Explanation: Das Vorgangspassiv im Präsens besteht aus werden und dem Partizip II: wird geprüft / wird bearbeitet.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-0-1** — Die Rechnung ___ heute bearbeitet. (werden, Präsens)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"wird"}]}]`

Hint: Verwende werden und das Partizip II.

Explanation: Das Vorgangspassiv im Präsens besteht aus werden und dem Partizip II: wird geprüft / wird bearbeitet.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Passiv im Präteritum

Target: 40000000-0000-4000-8000-000000000001; category: passive; review: pending.

Content hash: 41fd62d17073a0442b91a2187ae42d8a96e51f0b0b86939ae838aaef66691589

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-1-0** — Die Unterlagen ___ gestern geprüft. (werden, Präteritum)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"wurden"}]}]`

Hint: Setze werden ins Präteritum.

Explanation: Im Präteritum steht bei einem Subjekt im Singular wurde und im Plural wurden.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-1-1** — Die Lieferung ___ letzte Woche verschickt. (werden, Präteritum)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"wurde"}]}]`

Hint: Setze werden ins Präteritum.

Explanation: Im Präteritum steht bei einem Subjekt im Singular wurde und im Plural wurden.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Passiv im Perfekt

Target: 40000000-0000-4000-8000-000000000002; category: passive; review: pending.

Content hash: e8b2bbea9eecabbc767288b454198d208656b6cbca11f621319534b627c78e11

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-2-0** — Der Vertrag ist unterschrieben ___. (Passiv, Perfekt)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"worden"}]}]`

Hint: Im Perfekt des Vorgangspassivs steht am Ende worden.

Explanation: Das Passiv im Perfekt wird mit sein, Partizip II und worden gebildet. Geworden gehört zum Vollverb werden.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-2-1** — Die Geräte sind repariert ___. (Passiv, Perfekt)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"worden"}]}]`

Hint: Im Perfekt des Vorgangspassivs steht am Ende worden.

Explanation: Das Passiv im Perfekt wird mit sein, Partizip II und worden gebildet. Geworden gehört zum Vollverb werden.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Passiv mit Modalverb

Target: 40000000-0000-4000-8000-000000000003; category: passive; review: pending.

Content hash: 7ef7b7b547dbc625725e9e41fa9cae60b21c8e9ce32e8b94d53cb44ff28659b4

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-3-0** — Der Antrag muss bis Freitag geprüft ___. (Passivinfinitiv)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"werden"}]}]`

Hint: Nach einem Modalverb stehen Partizip II und werden.

Explanation: Beim Passiv mit Modalverb folgt werden auf das Partizip: muss geprüft werden.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-3-1** — Die Daten können digital übermittelt ___. (Passivinfinitiv)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"werden"}]}]`

Hint: Nach einem Modalverb stehen Partizip II und werden.

Explanation: Beim Passiv mit Modalverb folgt werden auf das Partizip: muss geprüft werden.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Irreale Bedingungen mit hätte

Target: 40000000-0000-4000-8000-000000000004; category: conditional; review: pending.

Content hash: 943e74c1551344716ca51f043ad98f9061a42f17f61c3ad1d17d20f0faafb267

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-4-0** — Wenn ich mehr Zeit ___, würde ich den Kurs besuchen. (haben, Konjunktiv II)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"hätte"}]}]`

Hint: Verwende den Konjunktiv II von haben.

Explanation: Hätte drückt eine irreale Bedingung aus: Wenn ich mehr Zeit hätte …

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-4-1** — Wenn sie einen Führerschein ___, könnte sie zur Arbeit fahren. (haben, Konjunktiv II)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"hätte"}]}]`

Hint: Verwende den Konjunktiv II von haben.

Explanation: Hätte drückt eine irreale Bedingung aus: Wenn ich mehr Zeit hätte …

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Höfliche Bitten mit könnte

Target: 40000000-0000-4000-8000-000000000005; category: conditional; review: pending.

Content hash: 0e0478bcc3d437dd8bac153289ccd67946cc0c839d608399d94ff2fd01363a87

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-5-0** — ___ Sie mir bitte die Unterlagen schicken? (können, Konjunktiv II)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"Könnten"}]}]`

Hint: Verwende den Konjunktiv II von können.

Explanation: Könnten Sie … ist eine höfliche Bitte. Könntest du … richtet sich an eine vertraute Person.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-5-1** — ___ du mir kurz helfen? (können, Konjunktiv II)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"Könntest"}]}]`

Hint: Verwende den Konjunktiv II von können.

Explanation: Könnten Sie … ist eine höfliche Bitte. Könntest du … richtet sich an eine vertraute Person.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Irreale Vergangenheit mit hätte

Target: 40000000-0000-4000-8000-000000000006; category: conditional; review: pending.

Content hash: 315d1b675f71b44327af1e73346208d6715038a6f490434c31a4c2f5cec6f4f1

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-6-0** — Wenn ich früher gelernt ___, hätte ich die Prüfung bestanden. (haben, Konjunktiv II)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"hätte"}]}]`

Hint: Verwende hätte mit einem Partizip II.

Explanation: Hätte und Partizip II beschreiben ein irreales Ereignis in der Vergangenheit bei Verben mit haben.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-6-1** — Wenn er die Nachricht gelesen ___, hätte er geantwortet. (haben, Konjunktiv II)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"hätte"}]}]`

Hint: Verwende hätte mit einem Partizip II.

Explanation: Hätte und Partizip II beschreiben ein irreales Ereignis in der Vergangenheit bei Verben mit haben.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Irreale Vergangenheit mit wäre

Target: 40000000-0000-4000-8000-000000000007; category: conditional; review: pending.

Content hash: 25e113024fedfe27dfe66a10b65b352d072d6c83a617fe5f69cf6a667fd6da37

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-7-0** — Wenn der Bus pünktlich gekommen ___, hätten wir den Zug erreicht. (sein, Konjunktiv II)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"wäre"}]}]`

Hint: Verwende wäre bei Verben, deren Perfekt mit sein gebildet wird.

Explanation: Wäre und Partizip II beschreiben ein irreales vergangenes Ereignis mit Verben wie kommen und fahren.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-7-1** — Wenn ich früher losgefahren ___, hätte ich den Termin geschafft. (sein, Konjunktiv II)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"wäre"}]}]`

Hint: Verwende wäre bei Verben, deren Perfekt mit sein gebildet wird.

Explanation: Wäre und Partizip II beschreiben ein irreales vergangenes Ereignis mit Verben wie kommen und fahren.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Gegensatz mit obwohl

Target: 40000000-0000-4000-8000-000000000008; category: connectors; review: pending.

Content hash: 7048d045ce905759f55875eb2aeb0af63ee329c806e3fdd4c40005599fe0c09b

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-8-0** — Obwohl es stark ___, fahren wir mit dem Rad. (regnen, Präsens)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"regnet"}]}]`

Hint: Obwohl leitet einen Nebensatz ein.

Explanation: Nach obwohl steht das konjugierte Verb am Ende: obwohl … regnet / … ist.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-8-1** — Obwohl die Aufgabe schwierig ___, geben wir nicht auf. (sein, Präsens)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"ist"}]}]`

Hint: Obwohl leitet einen Nebensatz ein.

Explanation: Nach obwohl steht das konjugierte Verb am Ende: obwohl … regnet / … ist.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Begründung mit da

Target: 40000000-0000-4000-8000-000000000009; category: connectors; review: pending.

Content hash: 0802063e8c19d84ffb3e719ffb2af29ebb984f0d9da11be134de38d8659e9883

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-9-0** — Da die Kollegin heute ___, übernimmt Tim ihre Aufgaben. (fehlen, Präsens)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"fehlt"}]}]`

Hint: Da leitet eine Begründung ein; das Verb steht am Ende.

Explanation: Im Nebensatz mit da steht das konjugierte Verb am Ende.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-9-1** — Da der Drucker nicht ___, schicken wir die Datei per E-Mail. (funktionieren, Präsens)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"funktioniert"}]}]`

Hint: Da leitet eine Begründung ein; das Verb steht am Ende.

Explanation: Im Nebensatz mit da steht das konjugierte Verb am Ende.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Folge mit sodass

Target: 40000000-0000-4000-8000-000000000010; category: connectors; review: pending.

Content hash: 888ef201db3bcf028ece3ad3a46cfb04c70e1ed41e4387e7c0635200b2856658

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-10-0** — Der Zug hatte Verspätung, sodass ich zu spät ___. (ankommen, Präteritum)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"ankam"}]}]`

Hint: Sodass leitet die Folge in einem Nebensatz ein.

Explanation: Im Nebensatz mit sodass steht das konjugierte Verb am Ende.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-10-1** — Die Erklärung war klar, sodass alle den Ablauf ___. (verstehen, Präteritum)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"verstanden"}]}]`

Hint: Sodass leitet die Folge in einem Nebensatz ein.

Explanation: Im Nebensatz mit sodass steht das konjugierte Verb am Ende.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Absicht mit um … zu

Target: 40000000-0000-4000-8000-000000000011; category: infinitive; review: pending.

Content hash: ca2d98fd45b5cf50007cf42971a3d16eeaedf1fb0429b621c79bb50627c1d28a

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-11-0** — Ich besuche einen Kurs, um meine Aussprache ___ verbessern. (Infinitivpartikel)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"zu"}]}]`

Hint: Vor dem Infinitiv steht zu.

Explanation: Um … zu beschreibt eine Absicht, wenn beide Satzteile dasselbe Subjekt haben.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-11-1** — Sie spart Geld, um eine Weiterbildung ___ finanzieren. (Infinitivpartikel)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"zu"}]}]`

Hint: Vor dem Infinitiv steht zu.

Explanation: Um … zu beschreibt eine Absicht, wenn beide Satzteile dasselbe Subjekt haben.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Infinitiv mit zu bei trennbaren Verben

Target: 40000000-0000-4000-8000-000000000012; category: infinitive; review: pending.

Content hash: 24b2c8f572b6ca3ed22c3d3020f8d89e98016dae77d3d98c6737a5c6fc131968

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-12-0** — Ich habe vor, mich auf das Gespräch ___. (vorbereiten, Infinitiv mit zu)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"vorzubereiten"}]}]`

Hint: Setze zu zwischen Vorsilbe und Verbstamm.

Explanation: Bei trennbaren Verben steht zu im Wort: vorbereiten → vorzubereiten; teilnehmen → teilzunehmen.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-12-1** — Sie hat beschlossen, am Seminar ___. (teilnehmen, Infinitiv mit zu)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"teilzunehmen"}]}]`

Hint: Setze zu zwischen Vorsilbe und Verbstamm.

Explanation: Bei trennbaren Verben steht zu im Wort: vorbereiten → vorzubereiten; teilnehmen → teilzunehmen.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Relativpronomen im Akkusativ

Target: 40000000-0000-4000-8000-000000000013; category: relative; review: pending.

Content hash: 99470528f50516b03f0daac43d49b44a6f96c8e4df35a8f5fb437163a6333d5d

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-13-0** — Der Vertrag, ___ ich gestern unterschrieben habe, gilt ab Mai. (Relativpronomen)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"den"}]}]`

Hint: Das Relativpronomen ist das Objekt im Relativsatz.

Explanation: Im Akkusativ steht bei einem maskulinen Bezugswort den, bei einem neutralen das.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-13-1** — Das Angebot, ___ wir erhalten haben, ist günstig. (Relativpronomen)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"das"}]}]`

Hint: Das Relativpronomen ist das Objekt im Relativsatz.

Explanation: Im Akkusativ steht bei einem maskulinen Bezugswort den, bei einem neutralen das.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Relativpronomen im Dativ

Target: 40000000-0000-4000-8000-000000000014; category: relative; review: pending.

Content hash: dfb66035a4ffa146cbd1b27dcdd6076bcab0f282b724e7a920159aa59a744373

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-14-0** — Die Kollegin, ___ ich geholfen habe, bedankt sich. (Relativpronomen)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"der"}]}]`

Hint: Helfen und mit verlangen den Dativ.

Explanation: Im Dativ lauten die Relativpronomen dem bei maskulinen und neutralen, der bei femininen Bezugswörtern.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-14-1** — Der Kunde, mit ___ ich gesprochen habe, kommt morgen. (Relativpronomen)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"dem"}]}]`

Hint: Helfen und mit verlangen den Dativ.

Explanation: Im Dativ lauten die Relativpronomen dem bei maskulinen und neutralen, der bei femininen Bezugswörtern.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Relativpronomen im Genitiv

Target: 40000000-0000-4000-8000-000000000015; category: relative; review: pending.

Content hash: 420b79181af8afb7c3ab805fb4639117cb8fc2dd3c06cb5657a50fe4aae7db29

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-15-0** — Der Mitarbeiter, ___ Vorschlag wir besprochen haben, ist heute da. (Relativpronomen im Genitiv)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"dessen"}]}]`

Hint: Dessen und deren drücken Zugehörigkeit aus.

Explanation: Dessen bezieht sich auf ein maskulines oder neutrales Bezugswort, deren auf ein feminines Bezugswort oder den Plural.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-15-1** — Die Firma, ___ Produkte wir verkaufen, hat ihren Sitz in Köln. (Relativpronomen im Genitiv)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"deren"}]}]`

Hint: Dessen und deren drücken Zugehörigkeit aus.

Explanation: Dessen bezieht sich auf ein maskulines oder neutrales Bezugswort, deren auf ein feminines Bezugswort oder den Plural.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Genitiv nach trotz

Target: 40000000-0000-4000-8000-000000000016; category: cases; review: pending.

Content hash: c1b2049142ec071e94612960e1198476bd21aceac89e8c0579f8753365b3beec

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-16-0** — Trotz ___ starken Regens fand die Veranstaltung statt. (der, Genitiv)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"des"}]}]`

Hint: Verwende den Genitiv der Standardschriftsprache.

Explanation: In der Standardschriftsprache steht trotz meist mit Genitiv: trotz des Regens / der Verspätung.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-16-1** — Trotz ___ Verspätung erreichten wir den Anschluss. (die, Genitiv)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"der"}]}]`

Hint: Verwende den Genitiv der Standardschriftsprache.

Explanation: In der Standardschriftsprache steht trotz meist mit Genitiv: trotz des Regens / der Verspätung.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Genitiv nach während

Target: 40000000-0000-4000-8000-000000000017; category: cases; review: pending.

Content hash: e9c5ef5b2c0bb247828a31bc689863f2045fb789a6ce996fc3fbf7d2e0ba7f3a

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-17-0** — Während ___ Gesprächs klingelte das Telefon. (das, Genitiv)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"des"}]}]`

Hint: Hier ist während eine Präposition und keine Konjunktion.

Explanation: Als Präposition steht während in der Standardschriftsprache mit dem Genitiv.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-17-1** — Während ___ Besprechung machen wir Notizen. (die, Genitiv)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"der"}]}]`

Hint: Hier ist während eine Präposition und keine Konjunktion.

Explanation: Als Präposition steht während in der Standardschriftsprache mit dem Genitiv.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Adjektive im Genitiv

Target: 40000000-0000-4000-8000-000000000018; category: adjective; review: pending.

Content hash: dad60dbe8475eae43053a95e8adbb9dca12a3ebb800670b61a629599415e93bd

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-18-0** — Wegen des ___ Wetters wurde das Fest abgesagt. (schlecht)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"schlechten"}]}]`

Hint: Nach dem bestimmten Artikel endet das Adjektiv hier auf -en.

Explanation: Nach des erhält das Adjektiv in diesen Genitivgruppen die schwache Endung -en.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-18-1** — Die Folgen des ___ Fehlers waren teuer. (technisch)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"technischen"}]}]`

Hint: Nach dem bestimmten Artikel endet das Adjektiv hier auf -en.

Explanation: Nach des erhält das Adjektiv in diesen Genitivgruppen die schwache Endung -en.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Adjektive ohne Artikel

Target: 40000000-0000-4000-8000-000000000019; category: adjective; review: pending.

Content hash: d6fff5940a3e5dccc5826a9c37d31d42e8c6e8742cdc562777d8db421824fe8e

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-19-0** — Mit ___ Interesse habe ich Ihre Anzeige gelesen. (groß)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"großem"}]}]`

Hint: Ohne Artikel werden Adjektive stark dekliniert.

Explanation: Ohne Artikel endet das Adjektiv im Dativ Maskulinum auf -em, im Dativ Plural auf -en.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-19-1** — Mit ___ Kollegen arbeiten wir gern zusammen. (zuverlässig)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"zuverlässigen"}]}]`

Hint: Ohne Artikel werden Adjektive stark dekliniert.

Explanation: Ohne Artikel endet das Adjektiv im Dativ Maskulinum auf -em, im Dativ Plural auf -en.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Abhängen von

Target: 40000000-0000-4000-8000-000000000020; category: verb_preposition; review: pending.

Content hash: efad173e6a5190e3e80e344c2d256e29c957efc95325d295c084c3bc41b901f5

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-20-0** — Der Erfolg hängt ___ der Erfahrung des Teams ab. (Präposition zu abhängen)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"von"}]}]`

Hint: Lerne die feste Präposition zu abhängen.

Explanation: Abhängen steht mit von und Dativ: von der Erfahrung / vom Wetter.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-20-1** — Unsere Planung hängt ___ dem Wetter ab. (Präposition zu abhängen)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"von"}]}]`

Hint: Lerne die feste Präposition zu abhängen.

Explanation: Abhängen steht mit von und Dativ: von der Erfahrung / vom Wetter.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Teilnehmen an

Target: 40000000-0000-4000-8000-000000000021; category: verb_preposition; review: pending.

Content hash: 649ff0c08e6059ece40aebb0f32a4f80d0118ab65dab388ffd1857ac23abe18d

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-21-0** — Sie nimmt ___ einer Fortbildung teil. (Präposition zu teilnehmen)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"an"}]}]`

Hint: Lerne die feste Präposition zu teilnehmen.

Explanation: Teilnehmen steht mit an und Dativ.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-21-1** — Wir nehmen ___ dem Projekt teil. (Präposition zu teilnehmen)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"an"}]}]`

Hint: Lerne die feste Präposition zu teilnehmen.

Explanation: Teilnehmen steht mit an und Dativ.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Überzeugen von

Target: 40000000-0000-4000-8000-000000000022; category: verb_preposition; review: pending.

Content hash: bec283a532e219ddcd09ecc4ddbc69e136bc2f3268a64653d5ba955e413aa606

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-22-0** — Die Daten überzeugen mich ___ der Qualität. (Präposition zu überzeugen)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"von"}]}]`

Hint: Lerne jemanden von etwas überzeugen.

Explanation: Überzeugen hat ein Akkusativobjekt für die Person; die Sache folgt mit von und Dativ.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-22-1** — Sie überzeugt das Team ___ ihrem Vorschlag. (Präposition zu überzeugen)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"von"}]}]`

Hint: Lerne jemanden von etwas überzeugen.

Explanation: Überzeugen hat ein Akkusativobjekt für die Person; die Sache folgt mit von und Dativ.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Sich befassen mit

Target: 40000000-0000-4000-8000-000000000023; category: verb_preposition; review: pending.

Content hash: 4b7329d1318705fa3cea665e2ff0bd90c50a81777beefd25ab626ea71b33803f

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-23-0** — Wir befassen uns ___ den neuen Regeln. (Präposition zu sich befassen)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"mit"}]}]`

Hint: Lerne die feste Präposition zu sich befassen.

Explanation: Sich befassen steht mit mit und Dativ.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-23-1** — Er befasst sich ___ der Auswertung. (Präposition zu sich befassen)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"mit"}]}]`

Hint: Lerne die feste Präposition zu sich befassen.

Explanation: Sich befassen steht mit mit und Dativ.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Sich ergeben aus

Target: 40000000-0000-4000-8000-000000000024; category: verb_preposition; review: pending.

Content hash: 1dfb7c91f6274bc38982885cc48122f9c66b60148aec1e52931f1d2d50068aa2

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-24-0** — Neue Möglichkeiten ergeben sich ___ dem Gespräch. (Präposition zu sich ergeben)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"aus"}]}]`

Hint: Ergeben bedeutet hier resultieren, nicht kapitulieren.

Explanation: Sich aus etwas ergeben bedeutet daraus resultieren: Möglichkeiten ergeben sich aus dem Gespräch.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-24-1** — Die nächsten Schritte ergeben sich ___ der Analyse. (Präposition zu sich ergeben)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"aus"}]}]`

Hint: Ergeben bedeutet hier resultieren, nicht kapitulieren.

Explanation: Sich aus etwas ergeben bedeutet daraus resultieren: Möglichkeiten ergeben sich aus dem Gespräch.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Einen Antrag stellen

Target: 40000000-0000-4000-8000-000000000025; category: work_vocabulary; review: pending.

Content hash: edf507059193f2163b79266992dcb3fee09dc088bcb8477d3f0e677824553fbe

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-25-0** — Ich ___ einen Antrag auf Förderung. (stellen, Präsens)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"stelle"}]}]`

Hint: Setze das angegebene Verb ins Präsens.

Explanation: Einen Antrag stellen ist eine übliche Verbindung für eine formelle Beantragung.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-25-1** — Sie ___ einen Antrag auf Weiterbildung. (stellen, Präsens, 3. Person Singular)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"stellt"}]}]`

Hint: Setze das angegebene Verb ins Präsens.

Explanation: Einen Antrag stellen ist eine übliche Verbindung für eine formelle Beantragung.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Eine Entscheidung treffen

Target: 40000000-0000-4000-8000-000000000026; category: work_vocabulary; review: pending.

Content hash: 08eb91007d0baf6397014ab05f36c870f8f28807e8cc5ac9d85ea7c7c11f40ab

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-26-0** — Der Vorstand ___ morgen eine Entscheidung. (treffen, Präsens)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"trifft"}]}]`

Hint: Treffen verändert im Singular seinen Stammvokal.

Explanation: Eine Entscheidung treffen ist eine feste Verbindung. Die Singularformen lauten trifft und triffst.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-26-1** — Du ___ die Entscheidung selbst. (treffen, Präsens)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"triffst"}]}]`

Hint: Treffen verändert im Singular seinen Stammvokal.

Explanation: Eine Entscheidung treffen ist eine feste Verbindung. Die Singularformen lauten trifft und triffst.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Verantwortung übernehmen

Target: 40000000-0000-4000-8000-000000000027; category: work_vocabulary; review: pending.

Content hash: 2e2031c9e2b54f43c5eb77d52cc32a2be6a1a8c5ccb1b2eecc2b72589e670a03

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-27-0** — Die Leiterin ___ die Verantwortung für das Projekt. (übernehmen, Präsens)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"übernimmt"}]}]`

Hint: Übernehmen verändert im Singular seinen Stammvokal.

Explanation: Verantwortung übernehmen bedeutet für etwas verantwortlich werden; die Formen lauten übernimmt / übernimmst.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-27-1** — Du ___ die Verantwortung für die Abrechnung. (übernehmen, Präsens)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"übernimmst"}]}]`

Hint: Übernehmen verändert im Singular seinen Stammvokal.

Explanation: Verantwortung übernehmen bedeutet für etwas verantwortlich werden; die Formen lauten übernimmt / übernimmst.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Eine Frist einhalten

Target: 40000000-0000-4000-8000-000000000028; category: work_vocabulary; review: pending.

Content hash: 616e5d022d205d23e3561a7b6ec4588864c05b1d852f9e4acaff7df76bcbd709

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-28-0** — Das Team ___ die Frist ein. (einhalten, Präsens; nur der konjugierte Teil)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"hält"}]}]`

Hint: Einhalten ist trennbar und verändert seinen Stammvokal.

Explanation: Eine Frist einhalten bedeutet rechtzeitig fertig werden. Im Singular stehen hält / hältst.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-28-1** — Du ___ die vereinbarte Frist ein. (einhalten, Präsens; nur der konjugierte Teil)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"hältst"}]}]`

Hint: Einhalten ist trennbar und verändert seinen Stammvokal.

Explanation: Eine Frist einhalten bedeutet rechtzeitig fertig werden. Im Singular stehen hält / hältst.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

### B2: Voraussetzungen erfüllen

Target: 40000000-0000-4000-8000-000000000029; category: work_vocabulary; review: pending.

Content hash: 8e23c9649547b35a2fa858073b1011ec15c5335e1a41631e85f28eb0a0667000

Original Codex-authored controlled examples, 7 October 2026; AI editorial assistance, not independent German review. No legacy sentences imported.

**b2-29-0** — Ich ___ alle Voraussetzungen für die Stelle. (erfüllen, Präsens)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"erfülle"}]}]`

Hint: Setze das angegebene Verb ins Präsens.

Explanation: Voraussetzungen erfüllen bedeutet den Anforderungen für etwas entsprechen.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.

**b2-29-1** — Die Bewerberin ___ die fachlichen Voraussetzungen. (erfüllen, Präsens)

Accepted: `[{"type":"cloze","values":[{"slotId":"form","text":"erfüllt"}]}]`

Hint: Setze das angegebene Verb ins Präsens.

Explanation: Voraussetzungen erfüllen bedeutet den Anforderungen für etwas entsprechen.

The parenthetical cue specifies the required lemma or construction. Evaluate that requested form, not arbitrary paraphrases. Independent level/naturalness/alternative-answer review pending.


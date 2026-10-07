# Basic practice candidate: editorial review

65 targets / 125 exercises: existing five B1 exercises, 60 prepared ChatGPT B1 exercises, 60 new Codex B2 exercises. This workbook describes source review drafts; independent German review remains pending. The separately owner-authorized live release is recorded in docs/operations/basic-content-release.md.

The B2 label selects a scaffolded practice pack for B2 learners; it is not a claim that every individual form is exclusive to B2 or that this assesses proficiency.

Check naturalness, requested form, alternatives, hint leakage, explanation, level suitability and context variation. Record independent sign-off in the source JSON with its matching content hash. AI editorial work is not human approval. The SQL installs only drafts in an isolated review database; do not use it to activate production.

## Additions

### B1: Plural: der Antrag

Target: 10000000-0000-4000-8000-000000000000; category: plural; review: pending.

Content hash: 92c842f99ba9140c68825f4e1a9d4aea5972aff5ce1c94d6b05320efbd21db47

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

Content hash: d74bf7813f2b1960467cf47981356f6e8b8c1db6210d7d19a04d50c0765488d7

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

Content hash: 290ba4da43757d85b5df351b01c45fe807f2cfaa7ea741fdbc7fe4ebcdd88ab1

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

Content hash: 8aa837b84188bbdf460dab3badcf196724a31c4976f0c6abebba07d31386b418

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

Content hash: ae9eb450ac908d2c90a1cf3a011834249a968b17c9f1e8b793c4f9de64188d96

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

Content hash: 7978f2366fbbc87810c12c45e09551a84f74b8f389a24e9cd579f0b5eff56d96

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

Content hash: c808e339cb2b20d8ea27db1e3b6bf4895e3170ff7e402a738deb8161323dd2aa

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

Content hash: 2fad98a9ec409d2fc0ee0024c3659b9a5261879c2348377c38c31097abf97531

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

Content hash: 852d4a43f5027d736d839183cf4ac4da46b8a750465aa1fb0ce763442976f327

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

Content hash: 23a94e6072ef5fc4dae8d51e7e81e04beee36efe1c8babc7ac9f58d2053479ae

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

Content hash: d3c18ccc496dbb4dddea2fb1a8e9dc8dd6abf89a1c9f19ea60d61158bc900ee5

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

Content hash: c751edccde68df60c5deb2c90359f807e2c9684c74d9ea90173d613d0b98bd73

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

Content hash: 6a867f0e7b0d7718cd484180e2a4989bfe2c48489277f7f268d187864a9dd5b9

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

Content hash: 9e82809c18ea93e006b26a4d45d380e3b2966517290230014cf720969d9d779e

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

Content hash: 54f52cd7c725d35435cc4381922153e76489140a099d5803f9323d99e765e439

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

Content hash: c20c8689427c94627f3d1cdc19460394693a0bbce35d19c7c99537b5389baa86

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-15-0** — Der neu___ Kollege beginnt heute.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"ending","optionId":"e"}]}]`

Hint: Der bestimmte Artikel markiert bereits Maskulinum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination.

Explanation: Nach „der“ im maskulinen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

**v-15-1** — Der freundlich___ Kunde wartet am Empfang.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"ending","optionId":"e"}]}]`

Hint: Der bestimmte Artikel markiert bereits Maskulinum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination.

Explanation: Nach „der“ im maskulinen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

### B1: Adjektivendung: Femininum im Nominativ nach „die“

Target: 10000000-0000-4000-8000-000000000016; category: adjective; review: pending.

Content hash: d5ced07c05c428df1edab88802ee5413a13b9e04915a4a7477a13228dba314da

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-16-0** — Die neu___ Kollegin arbeitet im Vertrieb.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"ending","optionId":"e"}]}]`

Hint: Der bestimmte Artikel markiert bereits Femininum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination.

Explanation: Nach „die“ im femininen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

**v-16-1** — Die wichtig___ Frage bleibt offen.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"ending","optionId":"e"}]}]`

Hint: Der bestimmte Artikel markiert bereits Femininum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination.

Explanation: Nach „die“ im femininen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

### B1: Adjektivendung: Neutrum im Nominativ nach „das“

Target: 10000000-0000-4000-8000-000000000017; category: adjective; review: pending.

Content hash: eab8205faa06d9dcb3b115b50c502901ccc016143ab416a01a9c0d00ac78e0df

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-17-0** — Das klein___ Büro ist frei.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"ending","optionId":"e"}]}]`

Hint: Der bestimmte Artikel markiert bereits Neutrum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination.

Explanation: Nach „das“ im neutralen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

**v-17-1** — Das neu___ Gerät funktioniert gut.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"ending","optionId":"e"}]}]`

Hint: Der bestimmte Artikel markiert bereits Neutrum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination.

Explanation: Nach „das“ im neutralen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

### B1: Adjektivendung: Maskulinum im Akkusativ nach „den“

Target: 10000000-0000-4000-8000-000000000018; category: adjective; review: pending.

Content hash: 358b7486d216002893d6f99c30dc19f2e76041adc3fe256b42fe576bcfbe95ae

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-18-0** — Ich sehe den neu___ Kollegen.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"ending","optionId":"en"}]}]`

Hint: Nach einem bestimmten Artikel im maskulinen Akkusativ folgt das Adjektiv der schwachen Deklination.

Explanation: Nach „den“ im maskulinen Akkusativ bekommt ein schwach dekliniertes Adjektiv die Endung -en.

This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

**v-18-1** — Wir begrüßen den wichtig___ Kunden.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"ending","optionId":"en"}]}]`

Hint: Nach einem bestimmten Artikel im maskulinen Akkusativ folgt das Adjektiv der schwachen Deklination.

Explanation: Nach „den“ im maskulinen Akkusativ bekommt ein schwach dekliniertes Adjektiv die Endung -en.

This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

### B1: Adjektivendung: Dativ nach bestimmtem Artikel

Target: 10000000-0000-4000-8000-000000000019; category: adjective; review: pending.

Content hash: a46529d476bcb94f3c32700af9c8d49bf86d570a4c337cb96d555d6e2bd3fc5a

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-19-0** — Ich spreche mit dem nett___ Kollegen.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"ending","optionId":"en"}]}]`

Hint: Nach einem bestimmten Artikel im Dativ haben schwach deklinierte Adjektive in diesen Genera dieselbe Endung.

Explanation: Nach einem bestimmten Artikel im Dativ bekommt ein schwach dekliniertes Adjektiv die Endung -en.

This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

**v-19-1** — Sie arbeitet mit der erfahren___ Ärztin.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"ending","optionId":"en"}]}]`

Hint: Nach einem bestimmten Artikel im Dativ haben schwach deklinierte Adjektive in diesen Genera dieselbe Endung.

Explanation: Nach einem bestimmten Artikel im Dativ bekommt ein schwach dekliniertes Adjektiv die Endung -en.

This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

### B1: Präsens: fahren (du/er)

Target: 10000000-0000-4000-8000-000000000020; category: verb; review: pending.

Content hash: dce83cf7b569f48f83da54c24176b6aa2d7bc68709ac2764e4011eb35b039513

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-20-0** — Du ___ jeden Morgen mit dem Bus. Er ___ heute mit dem Zug.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"du","optionId":"fährst"},{"slotId":"er","optionId":"fährt"}]}]`

Hint: Bei „fahren“ wird in den Formen mit „du“ und „er“ der Stammvokal a zu ä.

Explanation: Der Stammvokal wechselt a → ä: du fährst, er fährt.

Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

**v-20-1** — Du ___ morgen nach Leipzig. Er ___ am Wochenende zu seinen Eltern.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"du","optionId":"fährst"},{"slotId":"er","optionId":"fährt"}]}]`

Hint: Bei „fahren“ wird in den Formen mit „du“ und „er“ der Stammvokal a zu ä.

Explanation: Der Stammvokal wechselt a → ä: du fährst, er fährt.

Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

### B1: Präsens: lesen (du/er)

Target: 10000000-0000-4000-8000-000000000021; category: verb; review: pending.

Content hash: 21f69d106b31d2f45edafd53b34dfabda362b9939d558a11b514044b0e280685

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-21-0** — Du ___ gerade den Bericht. Er ___ jeden Morgen die Zeitung.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"du","optionId":"liest"},{"slotId":"er","optionId":"liest"}]}]`

Hint: Bei „lesen“ wird in den Formen mit „du“ und „er“ e zu ie.

Explanation: Der Stammvokal wechselt e → ie; beide Formen lauten „liest“: du liest, er liest.

Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

**v-21-1** — Du ___ die Nachricht noch einmal. Er ___ oft deutsche Romane.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"du","optionId":"liest"},{"slotId":"er","optionId":"liest"}]}]`

Hint: Bei „lesen“ wird in den Formen mit „du“ und „er“ e zu ie.

Explanation: Der Stammvokal wechselt e → ie; beide Formen lauten „liest“: du liest, er liest.

Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

### B1: Präsens: geben (du/er)

Target: 10000000-0000-4000-8000-000000000022; category: verb; review: pending.

Content hash: 780efb24a230d975f496303a75ccd71f9272c6b6d728e4d292b82ce935b3e59d

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-22-0** — Du ___ mir bitte den Schlüssel. Er ___ der Kollegin die Unterlagen.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"du","optionId":"gibst"},{"slotId":"er","optionId":"gibt"}]}]`

Hint: Bei „geben“ wird in den Formen mit „du“ und „er“ e zu i.

Explanation: Der Stammvokal wechselt e → i: du gibst, er gibt.

Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

**v-22-1** — Du ___ dem Kind Wasser. Er ___ uns eine klare Antwort.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"du","optionId":"gibst"},{"slotId":"er","optionId":"gibt"}]}]`

Hint: Bei „geben“ wird in den Formen mit „du“ und „er“ e zu i.

Explanation: Der Stammvokal wechselt e → i: du gibst, er gibt.

Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

### B1: Präsens: nehmen (du/er)

Target: 10000000-0000-4000-8000-000000000023; category: verb; review: pending.

Content hash: 91f6b05b5e1b5e6d8043c1cd53189e4ae451369fdc99c9f69bd40e0a86ca415c

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-23-0** — Du ___ morgens den Bus. Er ___ lieber das Fahrrad.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"du","optionId":"nimmst"},{"slotId":"er","optionId":"nimmt"}]}]`

Hint: Die Formen mit „du“ und „er“ verwenden den unregelmäßigen Stamm „nimm-“.

Explanation: Im Singular lautet der Stamm „nimm-“: du nimmst, er nimmt.

Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

**v-23-1** — Du ___ noch einen Kaffee. Er ___ die letzte Tablette.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"du","optionId":"nimmst"},{"slotId":"er","optionId":"nimmt"}]}]`

Hint: Die Formen mit „du“ und „er“ verwenden den unregelmäßigen Stamm „nimm-“.

Explanation: Im Singular lautet der Stamm „nimm-“: du nimmst, er nimmt.

Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

### B1: Präsens: sprechen (du/er)

Target: 10000000-0000-4000-8000-000000000024; category: verb; review: pending.

Content hash: cdee8598243fb3f7f13c8f64d1e64759b3e9bcf386c85837a14ea09bdf644f63

Original agent-authored examples for German Master 2.0; no imported dataset.

**v-24-0** — Du ___ sehr gut Deutsch. Er ___ heute mit der Ärztin.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"du","optionId":"sprichst"},{"slotId":"er","optionId":"spricht"}]}]`

Hint: Bei „sprechen“ wird in den Formen mit „du“ und „er“ e zu i.

Explanation: Der Stammvokal wechselt e → i: du sprichst, er spricht.

Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

**v-24-1** — Du ___ morgen mit deinem Chef. Er ___ oft über seine Arbeit.

Accepted: `[{"type":"gap_choice","selections":[{"slotId":"du","optionId":"sprichst"},{"slotId":"er","optionId":"spricht"}]}]`

Hint: Bei „sprechen“ wird in den Formen mit „du“ und „er“ e zu i.

Explanation: Der Stammvokal wechselt e → i: du sprichst, er spricht.

Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

### B1: Nebensatz mit „weil“

Target: 10000000-0000-4000-8000-000000000025; category: word-order; review: pending.

Content hash: 1834e08db5914e8c8fe246107f8df7b71d1242671dc6612aa464245bd4d0c5d9

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

Content hash: 0ddfd6e956d4fc0d92a181e9b45950f0a5d3b10b7ef216dc56ebd27400632d1c

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

Content hash: d8564ece61977e3997c40c9075aec3b4700136f8153b9894203fc8d291c91f12

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

Content hash: ad503af2dec670ce068c5cdd3eb8a49542485fb2b5b0a76391bbdd5d339ee835

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

Content hash: 0af6c33a7138c9747c77889520396a9592a1f49e43f4451ffe9609b78b3f2f65

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

Content hash: 00abff0ea81eeaf7323d0a2767f7bd1f594e4606f9dc98b8fdfbda9d184bec66

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

Content hash: 643449622a4e481bacaecbb9f5cb5502fff7f3798b0a757c48c6e99868223d23

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

Content hash: ab4fe599725642815d55180104b60cbaa8a1a388ced9c8a09c732cf0025cd450

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

Content hash: 7c610e3a3f294c76beda91ebeee3c596b20a38f26890fefa2172fbf8cb3b10d6

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

Content hash: 9581ae18f9604e4df873a907d70db0da3e417f9581364e6387725ef460a5f866

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

Content hash: 21ea51cd4353102b281a7601c53e9059ad7a16044fd9c3dca89fc11feb183d76

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

Content hash: 4af9293ce3be858da78556eb074227ae8eec37495da225f8d54ac80a3951f592

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

Content hash: 25066a38ec3e936f9935aeff64eca46c0effdd5ca9a9db25c2863cf251125147

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

Content hash: 836a447dcd5258e0ec5cddcca5e94dd705449578545668c7ad0dfa6cbf43a3ef

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

Content hash: aced03098e1ebc75719717afe2fb5bd128e8232da8cc760f7e29f2cef35537fd

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

Content hash: fef77f22b646b3ca6e3d739406a9c61d34d866e1f15159758ed6d0ccd2bce3ef

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

Content hash: 85ff74a1e57b941706373d2535f57477361da6b125e659e22238430da12f3202

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

Content hash: 02911632bc2fae0c9374954ebc7cb14a5f7f925461618022a1c6d4e49e9bada3

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

Content hash: f6a9f56865bd20710e1c85cf11fd153fe72677688aacf6eaad62d8bc455c2672

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

Content hash: 7bdb074cb987c47a6227333773f84a9d3f2fc33a5c2b2c3e4518499a48518fdd

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

Content hash: f458e66a96635a21ecb51ac63a68ce5c24031cd06280c02d10a759b36ff59fb1

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

Content hash: 2b8ec2e7ddaf9888af673f9b5934bf2eb08fe897b8ea5301fcf13b0f68af2750

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

Content hash: 66d95b6a2f045b70fe4fc23e5870e3f0ae863cb3e4cc422be3b7aa4e7e5df1b9

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

Content hash: 5277de98837a187c9fe57bd2ffad4acf65bc7c813ef6db429383951a2911748a

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

Content hash: a18c34a16d835894ab881421e47dca48e329e02981bffdc31f98efd6a8af5769

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

Content hash: 8678cde71f87a59fb973cf75d640ec3402c4618fce1a57b6a6dd21a2546a6afe

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

Content hash: 3670d40589dcf7daeab6cf968d210c3048c2cfbc25d31b591dd4eee01354f6f2

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

Content hash: 755119d6489d741875b410764aea1f056c98c0bacab40b1da0454e0e1792ff1d

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

Content hash: 83c3c49592c5ca95f696c715086fd1f97d562d24cd1b1da1d9c401519ea939e1

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

Content hash: 7d234513f00efb1a053d40b376025edb2a731bb9438b36f38df5cbf3112982c8

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

Content hash: aa7f029e0fb561265d9db8e8306db35a4693aa833ed861a654dd0b0615cab64c

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

Content hash: 1af5e2432e5dbf901ed43f905f66bc9fb3c9f0edf53084bd6acc42593c551bf0

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

Content hash: a9e85d566f2994eb8d9d4be61cc46fe18d2264e3e7257650dbbc519a981248aa

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

Content hash: 8630777759d8db9629f4842711999eabe1e8cfa2430b13d1172ea9b46b0f988b

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

Content hash: a3d3f8b7e40e4708052ea2cd5fc27ac465284afed16c2b7cc0eb891293e9c460

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


# Initial German-language review workbook

Generated from initial-30.json. Agent-authored, unpublished drafts. Validation is structural and grading conformance evidence, not German-language approval.

Targets: 30; variants: 60; independently approved: 0.

For each target, check objective, B1/B2 suitability, grammar, naturalness, ambiguity, all accepted alternatives, distractors, hint leakage, explanation, context variation and provenance. Record reviewer, date, approved/changes-requested status and checklist findings in the JSON. Publication remains separately authorized.

## Plural: der Antrag

Target: 10000000-0000-4000-8000-000000000000 · B1 · plural

Recall and produce the standard plural of der Antrag.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 92c842f99ba9140c68825f4e1a9d4aea5972aff5ce1c94d6b05320efbd21db47

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-0-0

Revision: 10000000-0000-4000-8000-000000001000@1; context: context-0-0; transfer: none

Schreibe den Plural von „der Antrag“ ohne Artikel.

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001000",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000000",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "short_answer",
  "prompt": "Schreibe den Plural von „der Antrag“ ohne Artikel."
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "short_answer",
    "text": "Anträge"
  }
]
```

Anträge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

Ambiguity: The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

### v-0-1

Revision: 10000000-0000-4000-8000-000000001001@1; context: context-0-1; transfer: transfer-0

Für die Förderung liegen mehrere ___ vor. (Plural von „der Antrag“)

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001001",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000000",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "cloze",
  "prompt": "Für die Förderung liegen mehrere ___ vor. (Plural von „der Antrag“)",
  "slots": [
    {
      "id": "plural",
      "label": "Plural"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "cloze",
    "values": [
      {
        "slotId": "plural",
        "text": "Anträge"
      }
    ]
  }
]
```

Anträge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

Ambiguity: The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

## Plural: die Rechnung

Target: 10000000-0000-4000-8000-000000000001 · B1 · plural

Recall and produce the standard plural of die Rechnung.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: d74bf7813f2b1960467cf47981356f6e8b8c1db6210d7d19a04d50c0765488d7

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-1-0

Revision: 10000000-0000-4000-8000-000000001002@1; context: context-1-0; transfer: none

Schreibe den Plural von „die Rechnung“ ohne Artikel.

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001002",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000001",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "short_answer",
  "prompt": "Schreibe den Plural von „die Rechnung“ ohne Artikel."
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "short_answer",
    "text": "Rechnungen"
  }
]
```

Rechnungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

### v-1-1

Revision: 10000000-0000-4000-8000-000000001003@1; context: context-1-1; transfer: transfer-1

Im Ordner liegen noch drei offene ___. (Plural von „die Rechnung“)

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001003",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000001",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "cloze",
  "prompt": "Im Ordner liegen noch drei offene ___. (Plural von „die Rechnung“)",
  "slots": [
    {
      "id": "plural",
      "label": "Plural"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "cloze",
    "values": [
      {
        "slotId": "plural",
        "text": "Rechnungen"
      }
    ]
  }
]
```

Rechnungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

## Plural: der Termin

Target: 10000000-0000-4000-8000-000000000002 · B1 · plural

Recall and produce the standard plural of der Termin.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 290ba4da43757d85b5df351b01c45fe807f2cfaa7ea741fdbc7fe4ebcdd88ab1

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-2-0

Revision: 10000000-0000-4000-8000-000000001004@1; context: context-2-0; transfer: none

Schreibe den Plural von „der Termin“ ohne Artikel.

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001004",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000002",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "short_answer",
  "prompt": "Schreibe den Plural von „der Termin“ ohne Artikel."
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "short_answer",
    "text": "Termine"
  }
]
```

Termine ist der Standardplural. Der Plural bekommt die Endung -e.

Ambiguity: The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

### v-2-1

Revision: 10000000-0000-4000-8000-000000001005@1; context: context-2-1; transfer: transfer-2

Nächste Woche habe ich zwei wichtige ___. (Plural von „der Termin“)

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001005",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000002",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "cloze",
  "prompt": "Nächste Woche habe ich zwei wichtige ___. (Plural von „der Termin“)",
  "slots": [
    {
      "id": "plural",
      "label": "Plural"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "cloze",
    "values": [
      {
        "slotId": "plural",
        "text": "Termine"
      }
    ]
  }
]
```

Termine ist der Standardplural. Der Plural bekommt die Endung -e.

Ambiguity: The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

## Plural: das Gespräch

Target: 10000000-0000-4000-8000-000000000003 · B1 · plural

Recall and produce the standard plural of das Gespräch.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 8aa837b84188bbdf460dab3badcf196724a31c4976f0c6abebba07d31386b418

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-3-0

Revision: 10000000-0000-4000-8000-000000001006@1; context: context-3-0; transfer: none

Schreibe den Plural von „das Gespräch“ ohne Artikel.

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001006",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000003",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "short_answer",
  "prompt": "Schreibe den Plural von „das Gespräch“ ohne Artikel."
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "short_answer",
    "text": "Gespräche"
  }
]
```

Gespräche ist der Standardplural. Der Plural bekommt -e; der Stammvokal bleibt ä.

Ambiguity: The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

### v-3-1

Revision: 10000000-0000-4000-8000-000000001007@1; context: context-3-1; transfer: transfer-3

Heute stehen noch zwei schwierige ___ an. (Plural von „das Gespräch“)

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001007",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000003",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "cloze",
  "prompt": "Heute stehen noch zwei schwierige ___ an. (Plural von „das Gespräch“)",
  "slots": [
    {
      "id": "plural",
      "label": "Plural"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "cloze",
    "values": [
      {
        "slotId": "plural",
        "text": "Gespräche"
      }
    ]
  }
]
```

Gespräche ist der Standardplural. Der Plural bekommt -e; der Stammvokal bleibt ä.

Ambiguity: The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

## Plural: der Vertrag

Target: 10000000-0000-4000-8000-000000000004 · B1 · plural

Recall and produce the standard plural of der Vertrag.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: ae9eb450ac908d2c90a1cf3a011834249a968b17c9f1e8b793c4f9de64188d96

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-4-0

Revision: 10000000-0000-4000-8000-000000001008@1; context: context-4-0; transfer: none

Schreibe den Plural von „der Vertrag“ ohne Artikel.

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001008",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000004",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "short_answer",
  "prompt": "Schreibe den Plural von „der Vertrag“ ohne Artikel."
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "short_answer",
    "text": "Verträge"
  }
]
```

Verträge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

Ambiguity: The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

### v-4-1

Revision: 10000000-0000-4000-8000-000000001009@1; context: context-4-1; transfer: transfer-4

Die Firma hat mehrere neue ___ abgeschlossen. (Plural von „der Vertrag“)

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001009",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000004",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "cloze",
  "prompt": "Die Firma hat mehrere neue ___ abgeschlossen. (Plural von „der Vertrag“)",
  "slots": [
    {
      "id": "plural",
      "label": "Plural"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "cloze",
    "values": [
      {
        "slotId": "plural",
        "text": "Verträge"
      }
    ]
  }
]
```

Verträge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

Ambiguity: The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

## Plural: die Erfahrung

Target: 10000000-0000-4000-8000-000000000005 · B1 · plural

Recall and produce the standard plural of die Erfahrung.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 7978f2366fbbc87810c12c45e09551a84f74b8f389a24e9cd579f0b5eff56d96

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-5-0

Revision: 10000000-0000-4000-8000-000000001010@1; context: context-5-0; transfer: none

Schreibe den Plural von „die Erfahrung“ ohne Artikel.

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001010",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000005",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "short_answer",
  "prompt": "Schreibe den Plural von „die Erfahrung“ ohne Artikel."
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "short_answer",
    "text": "Erfahrungen"
  }
]
```

Erfahrungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

### v-5-1

Revision: 10000000-0000-4000-8000-000000001011@1; context: context-5-1; transfer: transfer-5

Im Lebenslauf beschreibt sie ihre beruflichen ___. (Plural von „die Erfahrung“)

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001011",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000005",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "cloze",
  "prompt": "Im Lebenslauf beschreibt sie ihre beruflichen ___. (Plural von „die Erfahrung“)",
  "slots": [
    {
      "id": "plural",
      "label": "Plural"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "cloze",
    "values": [
      {
        "slotId": "plural",
        "text": "Erfahrungen"
      }
    ]
  }
]
```

Erfahrungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

## Plural: das Angebot

Target: 10000000-0000-4000-8000-000000000006 · B1 · plural

Recall and produce the standard plural of das Angebot.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: c808e339cb2b20d8ea27db1e3b6bf4895e3170ff7e402a738deb8161323dd2aa

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-6-0

Revision: 10000000-0000-4000-8000-000000001012@1; context: context-6-0; transfer: none

Schreibe den Plural von „das Angebot“ ohne Artikel.

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001012",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000006",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "short_answer",
  "prompt": "Schreibe den Plural von „das Angebot“ ohne Artikel."
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "short_answer",
    "text": "Angebote"
  }
]
```

Angebote ist der Standardplural. Der Plural bekommt die Endung -e.

Ambiguity: The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

### v-6-1

Revision: 10000000-0000-4000-8000-000000001013@1; context: context-6-1; transfer: transfer-6

Wir vergleichen drei verschiedene ___. (Plural von „das Angebot“)

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001013",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000006",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "cloze",
  "prompt": "Wir vergleichen drei verschiedene ___. (Plural von „das Angebot“)",
  "slots": [
    {
      "id": "plural",
      "label": "Plural"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "cloze",
    "values": [
      {
        "slotId": "plural",
        "text": "Angebote"
      }
    ]
  }
]
```

Angebote ist der Standardplural. Der Plural bekommt die Endung -e.

Ambiguity: The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

## Plural: die Entscheidung

Target: 10000000-0000-4000-8000-000000000007 · B1 · plural

Recall and produce the standard plural of die Entscheidung.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 2fad98a9ec409d2fc0ee0024c3659b9a5261879c2348377c38c31097abf97531

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-7-0

Revision: 10000000-0000-4000-8000-000000001014@1; context: context-7-0; transfer: none

Schreibe den Plural von „die Entscheidung“ ohne Artikel.

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001014",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000007",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "short_answer",
  "prompt": "Schreibe den Plural von „die Entscheidung“ ohne Artikel."
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "short_answer",
    "text": "Entscheidungen"
  }
]
```

Entscheidungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

### v-7-1

Revision: 10000000-0000-4000-8000-000000001015@1; context: context-7-1; transfer: transfer-7

Das Team muss heute mehrere wichtige ___ treffen. (Plural von „die Entscheidung“)

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001015",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000007",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "cloze",
  "prompt": "Das Team muss heute mehrere wichtige ___ treffen. (Plural von „die Entscheidung“)",
  "slots": [
    {
      "id": "plural",
      "label": "Plural"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "cloze",
    "values": [
      {
        "slotId": "plural",
        "text": "Entscheidungen"
      }
    ]
  }
]
```

Entscheidungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

## Plural: die Voraussetzung

Target: 10000000-0000-4000-8000-000000000008 · B1 · plural

Recall and produce the standard plural of die Voraussetzung.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 852d4a43f5027d736d839183cf4ac4da46b8a750465aa1fb0ce763442976f327

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-8-0

Revision: 10000000-0000-4000-8000-000000001016@1; context: context-8-0; transfer: none

Schreibe den Plural von „die Voraussetzung“ ohne Artikel.

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001016",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000008",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "short_answer",
  "prompt": "Schreibe den Plural von „die Voraussetzung“ ohne Artikel."
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "short_answer",
    "text": "Voraussetzungen"
  }
]
```

Voraussetzungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

### v-8-1

Revision: 10000000-0000-4000-8000-000000001017@1; context: context-8-1; transfer: transfer-8

Für die Stelle musst du mehrere ___ erfüllen. (Plural von „die Voraussetzung“)

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001017",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000008",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "cloze",
  "prompt": "Für die Stelle musst du mehrere ___ erfüllen. (Plural von „die Voraussetzung“)",
  "slots": [
    {
      "id": "plural",
      "label": "Plural"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "cloze",
    "values": [
      {
        "slotId": "plural",
        "text": "Voraussetzungen"
      }
    ]
  }
]
```

Voraussetzungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

## Plural: der Vorschlag

Target: 10000000-0000-4000-8000-000000000009 · B1 · plural

Recall and produce the standard plural of der Vorschlag.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 23a94e6072ef5fc4dae8d51e7e81e04beee36efe1c8babc7ac9f58d2053479ae

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-9-0

Revision: 10000000-0000-4000-8000-000000001018@1; context: context-9-0; transfer: none

Schreibe den Plural von „der Vorschlag“ ohne Artikel.

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001018",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000009",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "short_answer",
  "prompt": "Schreibe den Plural von „der Vorschlag“ ohne Artikel."
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "short_answer",
    "text": "Vorschläge"
  }
]
```

Vorschläge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

Ambiguity: The lemma and requested number are explicit. Accept only the standard plural noun form without an article.

### v-9-1

Revision: 10000000-0000-4000-8000-000000001019@1; context: context-9-1; transfer: transfer-9

Im Meeting wurden drei konkrete ___ gemacht. (Plural von „der Vorschlag“)

Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001019",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000009",
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "instruction": {
    "en": "Write only the requested plural form. Keep German noun capitalization.",
    "de": "Schreibe nur die verlangte Pluralform. Achte auf die Großschreibung des Nomens."
  },
  "type": "cloze",
  "prompt": "Im Meeting wurden drei konkrete ___ gemacht. (Plural von „der Vorschlag“)",
  "slots": [
    {
      "id": "plural",
      "label": "Plural"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "cloze",
    "values": [
      {
        "slotId": "plural",
        "text": "Vorschläge"
      }
    ]
  }
]
```

Vorschläge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

Ambiguity: The lemma is stated in the prompt and the sentence requires its plural form; no article belongs in the blank.

## Dativartikel nach „mit“

Target: 10000000-0000-4000-8000-000000000010 · B1 · preposition

Choose the definite singular article after mit, using the dative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: d3c18ccc496dbb4dddea2fb1a8e9dc8dd6abf89a1c9f19ea60d61158bc900ee5

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-10-0

Revision: 10000000-0000-4000-8000-000000001020@1; context: context-10-0; transfer: none

Ich bespreche die Ergebnisse mit ___ Arzt. (der Arzt)

Wähle nur den bestimmten Artikel im Singular, der nach „mit“ zum Nomen passt.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001020",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000010",
  "hint": {
    "en": "mit always takes the dative. Match the article to the noun’s gender.",
    "de": "„mit“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an."
  },
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “mit”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „mit“ zum Nomen passt."
  },
  "type": "choice",
  "prompt": "Ich bespreche die Ergebnisse mit ___ Arzt. (der Arzt)",
  "options": [
    {
      "id": "der",
      "text": "der"
    },
    {
      "id": "die",
      "text": "die"
    },
    {
      "id": "das",
      "text": "das"
    },
    {
      "id": "den",
      "text": "den"
    },
    {
      "id": "dem",
      "text": "dem"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "dem"
  }
]
```

„mit“ verlangt den Dativ. „der Arzt“ ist maskulin; im Dativ Singular lautet der bestimmte Artikel „dem“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### v-10-1

Revision: 10000000-0000-4000-8000-000000001021@1; context: context-10-1; transfer: transfer-10

Morgen fahre ich mit ___ Zug nach Hamburg. (der Zug)

Wähle nur den bestimmten Artikel im Singular, der nach „mit“ zum Nomen passt.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001021",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000010",
  "hint": {
    "en": "mit always takes the dative. Match the article to the noun’s gender.",
    "de": "„mit“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an."
  },
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “mit”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „mit“ zum Nomen passt."
  },
  "type": "choice",
  "prompt": "Morgen fahre ich mit ___ Zug nach Hamburg. (der Zug)",
  "options": [
    {
      "id": "der",
      "text": "der"
    },
    {
      "id": "die",
      "text": "die"
    },
    {
      "id": "das",
      "text": "das"
    },
    {
      "id": "den",
      "text": "den"
    },
    {
      "id": "dem",
      "text": "dem"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "dem"
  }
]
```

„mit“ verlangt den Dativ. „der Zug“ ist maskulin; im Dativ Singular lautet der bestimmte Artikel „dem“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

## Dativartikel nach „bei“

Target: 10000000-0000-4000-8000-000000000011 · B1 · preposition

Choose the definite singular article after bei, using the dative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: c751edccde68df60c5deb2c90359f807e2c9684c74d9ea90173d613d0b98bd73

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-11-0

Revision: 10000000-0000-4000-8000-000000001022@1; context: context-11-0; transfer: none

Wegen der Schmerzen bin ich heute bei ___ Ärztin. (die Ärztin)

Wähle nur den bestimmten Artikel im Singular, der nach „bei“ zum Nomen passt.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001022",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000011",
  "hint": {
    "en": "bei always takes the dative. Match the article to the noun’s gender.",
    "de": "„bei“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an."
  },
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “bei”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „bei“ zum Nomen passt."
  },
  "type": "choice",
  "prompt": "Wegen der Schmerzen bin ich heute bei ___ Ärztin. (die Ärztin)",
  "options": [
    {
      "id": "der",
      "text": "der"
    },
    {
      "id": "die",
      "text": "die"
    },
    {
      "id": "das",
      "text": "das"
    },
    {
      "id": "den",
      "text": "den"
    },
    {
      "id": "dem",
      "text": "dem"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "der"
  }
]
```

„bei“ verlangt den Dativ. „die Ärztin“ ist feminin; im Dativ Singular lautet der bestimmte Artikel „der“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### v-11-1

Revision: 10000000-0000-4000-8000-000000001023@1; context: context-11-1; transfer: transfer-11

Wir brauchen Hilfe bei ___ Vorbereitung der Präsentation. (die Vorbereitung)

Wähle nur den bestimmten Artikel im Singular, der nach „bei“ zum Nomen passt.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001023",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000011",
  "hint": {
    "en": "bei always takes the dative. Match the article to the noun’s gender.",
    "de": "„bei“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an."
  },
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “bei”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „bei“ zum Nomen passt."
  },
  "type": "choice",
  "prompt": "Wir brauchen Hilfe bei ___ Vorbereitung der Präsentation. (die Vorbereitung)",
  "options": [
    {
      "id": "der",
      "text": "der"
    },
    {
      "id": "die",
      "text": "die"
    },
    {
      "id": "das",
      "text": "das"
    },
    {
      "id": "den",
      "text": "den"
    },
    {
      "id": "dem",
      "text": "dem"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "der"
  }
]
```

„bei“ verlangt den Dativ. „die Vorbereitung“ ist feminin; im Dativ Singular lautet der bestimmte Artikel „der“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

## Akkusativartikel nach „für“

Target: 10000000-0000-4000-8000-000000000012 · B1 · preposition

Choose the definite singular article after für, using the accusative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 6a867f0e7b0d7718cd484180e2a4989bfe2c48489277f7f268d187864a9dd5b9

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-12-0

Revision: 10000000-0000-4000-8000-000000001024@1; context: context-12-0; transfer: none

Dieses Formular ist für ___ Kunden am Schalter. (der Kunde, Singular)

Wähle nur den bestimmten Artikel im Singular, der nach „für“ zum Nomen passt.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001024",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000012",
  "hint": {
    "en": "für always takes the accusative. Match the article to the noun’s gender.",
    "de": "„für“ verlangt immer den Akkusativ. Passe den Artikel an das Genus des Nomens an."
  },
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “für”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „für“ zum Nomen passt."
  },
  "type": "choice",
  "prompt": "Dieses Formular ist für ___ Kunden am Schalter. (der Kunde, Singular)",
  "options": [
    {
      "id": "der",
      "text": "der"
    },
    {
      "id": "die",
      "text": "die"
    },
    {
      "id": "das",
      "text": "das"
    },
    {
      "id": "den",
      "text": "den"
    },
    {
      "id": "dem",
      "text": "dem"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "den"
  }
]
```

„für“ verlangt den Akkusativ. „der Kunde“ ist maskulin; im Akkusativ Singular lautet der bestimmte Artikel „den“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### v-12-1

Revision: 10000000-0000-4000-8000-000000001025@1; context: context-12-1; transfer: transfer-12

Ich buche den Raum für ___ Kurs am Abend. (der Kurs)

Wähle nur den bestimmten Artikel im Singular, der nach „für“ zum Nomen passt.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001025",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000012",
  "hint": {
    "en": "für always takes the accusative. Match the article to the noun’s gender.",
    "de": "„für“ verlangt immer den Akkusativ. Passe den Artikel an das Genus des Nomens an."
  },
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “für”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „für“ zum Nomen passt."
  },
  "type": "choice",
  "prompt": "Ich buche den Raum für ___ Kurs am Abend. (der Kurs)",
  "options": [
    {
      "id": "der",
      "text": "der"
    },
    {
      "id": "die",
      "text": "die"
    },
    {
      "id": "das",
      "text": "das"
    },
    {
      "id": "den",
      "text": "den"
    },
    {
      "id": "dem",
      "text": "dem"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "den"
  }
]
```

„für“ verlangt den Akkusativ. „der Kurs“ ist maskulin; im Akkusativ Singular lautet der bestimmte Artikel „den“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

## Akkusativartikel nach „ohne“

Target: 10000000-0000-4000-8000-000000000013 · B1 · preposition

Choose the definite singular article after ohne, using the accusative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 9e82809c18ea93e006b26a4d45d380e3b2966517290230014cf720969d9d779e

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-13-0

Revision: 10000000-0000-4000-8000-000000001026@1; context: context-13-0; transfer: none

Sie geht nie ohne ___ Tasche aus dem Haus. (die Tasche)

Wähle nur den bestimmten Artikel im Singular, der nach „ohne“ zum Nomen passt.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001026",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000013",
  "hint": {
    "en": "ohne always takes the accusative. Match the article to the noun’s gender.",
    "de": "„ohne“ verlangt immer den Akkusativ. Passe den Artikel an das Genus des Nomens an."
  },
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “ohne”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „ohne“ zum Nomen passt."
  },
  "type": "choice",
  "prompt": "Sie geht nie ohne ___ Tasche aus dem Haus. (die Tasche)",
  "options": [
    {
      "id": "der",
      "text": "der"
    },
    {
      "id": "die",
      "text": "die"
    },
    {
      "id": "das",
      "text": "das"
    },
    {
      "id": "den",
      "text": "den"
    },
    {
      "id": "dem",
      "text": "dem"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "die"
  }
]
```

„ohne“ verlangt den Akkusativ. „die Tasche“ ist feminin; im Akkusativ Singular bleibt der bestimmte Artikel „die“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### v-13-1

Revision: 10000000-0000-4000-8000-000000001027@1; context: context-13-1; transfer: transfer-13

Wir dürfen das nicht ohne ___ Zustimmung veröffentlichen. (die Zustimmung)

Wähle nur den bestimmten Artikel im Singular, der nach „ohne“ zum Nomen passt.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001027",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000013",
  "hint": {
    "en": "ohne always takes the accusative. Match the article to the noun’s gender.",
    "de": "„ohne“ verlangt immer den Akkusativ. Passe den Artikel an das Genus des Nomens an."
  },
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “ohne”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „ohne“ zum Nomen passt."
  },
  "type": "choice",
  "prompt": "Wir dürfen das nicht ohne ___ Zustimmung veröffentlichen. (die Zustimmung)",
  "options": [
    {
      "id": "der",
      "text": "der"
    },
    {
      "id": "die",
      "text": "die"
    },
    {
      "id": "das",
      "text": "das"
    },
    {
      "id": "den",
      "text": "den"
    },
    {
      "id": "dem",
      "text": "dem"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "die"
  }
]
```

„ohne“ verlangt den Akkusativ. „die Zustimmung“ ist feminin; im Akkusativ Singular bleibt der bestimmte Artikel „die“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

## Dativartikel nach „aus“

Target: 10000000-0000-4000-8000-000000000014 · B1 · preposition

Choose the definite singular article after aus, using the dative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 54f52cd7c725d35435cc4381922153e76489140a099d5803f9323d99e765e439

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-14-0

Revision: 10000000-0000-4000-8000-000000001028@1; context: context-14-0; transfer: none

Die Unterlagen kommen aus ___ Büro im Erdgeschoss. (das Büro)

Wähle nur den bestimmten Artikel im Singular, der nach „aus“ zum Nomen passt.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001028",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000014",
  "hint": {
    "en": "aus always takes the dative. Match the article to the noun’s gender.",
    "de": "„aus“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an."
  },
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “aus”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „aus“ zum Nomen passt."
  },
  "type": "choice",
  "prompt": "Die Unterlagen kommen aus ___ Büro im Erdgeschoss. (das Büro)",
  "options": [
    {
      "id": "der",
      "text": "der"
    },
    {
      "id": "die",
      "text": "die"
    },
    {
      "id": "das",
      "text": "das"
    },
    {
      "id": "den",
      "text": "den"
    },
    {
      "id": "dem",
      "text": "dem"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "dem"
  }
]
```

„aus“ verlangt den Dativ. „das Büro“ ist neutral; im Dativ Singular lautet der bestimmte Artikel „dem“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### v-14-1

Revision: 10000000-0000-4000-8000-000000001029@1; context: context-14-1; transfer: transfer-14

Wir hören Musik aus ___ Haus nebenan. (das Haus)

Wähle nur den bestimmten Artikel im Singular, der nach „aus“ zum Nomen passt.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001029",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000014",
  "hint": {
    "en": "aus always takes the dative. Match the article to the noun’s gender.",
    "de": "„aus“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an."
  },
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “aus”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „aus“ zum Nomen passt."
  },
  "type": "choice",
  "prompt": "Wir hören Musik aus ___ Haus nebenan. (das Haus)",
  "options": [
    {
      "id": "der",
      "text": "der"
    },
    {
      "id": "die",
      "text": "die"
    },
    {
      "id": "das",
      "text": "das"
    },
    {
      "id": "den",
      "text": "den"
    },
    {
      "id": "dem",
      "text": "dem"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "dem"
  }
]
```

„aus“ verlangt den Dativ. „das Haus“ ist neutral; im Dativ Singular lautet der bestimmte Artikel „dem“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

## Adjektivendung: Maskulinum im Nominativ nach „der“

Target: 10000000-0000-4000-8000-000000000015 · B1 · adjective

Choose the weak adjective ending after der in masculine nominative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: c20c8689427c94627f3d1cdc19460394693a0bbce35d19c7c99537b5389baa86

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-15-0

Revision: 10000000-0000-4000-8000-000000001030@2; context: context-15-0; transfer: none

Der neu___ Kollege beginnt heute.

Wähle die fehlende Adjektivendung.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001030",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000015",
  "hint": {
    "en": "The definite article already marks masculine nominative, so the adjective uses the weak pattern.",
    "de": "Der bestimmte Artikel markiert bereits Maskulinum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination."
  },
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "type": "gap_choice",
  "prompt": "Der neu___ Kollege beginnt heute.",
  "slots": [
    {
      "id": "ending",
      "label": "Adjektivendung",
      "options": [
        {
          "id": "er",
          "text": "-er"
        },
        {
          "id": "e",
          "text": "-e"
        },
        {
          "id": "en",
          "text": "-en"
        },
        {
          "id": "es",
          "text": "-es"
        },
        {
          "id": "em",
          "text": "-em"
        }
      ]
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "gap_choice",
    "selections": [
      {
        "slotId": "ending",
        "optionId": "e"
      }
    ]
  }
]
```

Nach „der“ im maskulinen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

Ambiguity: This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

### v-15-1

Revision: 10000000-0000-4000-8000-000000001031@2; context: context-15-1; transfer: transfer-15

Der freundlich___ Kunde wartet am Empfang.

Wähle die fehlende Adjektivendung.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001031",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000015",
  "hint": {
    "en": "The definite article already marks masculine nominative, so the adjective uses the weak pattern.",
    "de": "Der bestimmte Artikel markiert bereits Maskulinum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination."
  },
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "type": "gap_choice",
  "prompt": "Der freundlich___ Kunde wartet am Empfang.",
  "slots": [
    {
      "id": "ending",
      "label": "Adjektivendung",
      "options": [
        {
          "id": "en",
          "text": "-en"
        },
        {
          "id": "es",
          "text": "-es"
        },
        {
          "id": "em",
          "text": "-em"
        },
        {
          "id": "e",
          "text": "-e"
        },
        {
          "id": "er",
          "text": "-er"
        }
      ]
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "gap_choice",
    "selections": [
      {
        "slotId": "ending",
        "optionId": "e"
      }
    ]
  }
]
```

Nach „der“ im maskulinen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

Ambiguity: This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

## Adjektivendung: Femininum im Nominativ nach „die“

Target: 10000000-0000-4000-8000-000000000016 · B1 · adjective

Choose the weak adjective ending after die in feminine nominative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: d5ced07c05c428df1edab88802ee5413a13b9e04915a4a7477a13228dba314da

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-16-0

Revision: 10000000-0000-4000-8000-000000001032@2; context: context-16-0; transfer: none

Die neu___ Kollegin arbeitet im Vertrieb.

Wähle die fehlende Adjektivendung.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001032",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000016",
  "hint": {
    "en": "The definite article already marks feminine nominative, so the adjective uses the weak pattern.",
    "de": "Der bestimmte Artikel markiert bereits Femininum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination."
  },
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "type": "gap_choice",
  "prompt": "Die neu___ Kollegin arbeitet im Vertrieb.",
  "slots": [
    {
      "id": "ending",
      "label": "Adjektivendung",
      "options": [
        {
          "id": "e",
          "text": "-e"
        },
        {
          "id": "em",
          "text": "-em"
        },
        {
          "id": "er",
          "text": "-er"
        },
        {
          "id": "en",
          "text": "-en"
        },
        {
          "id": "es",
          "text": "-es"
        }
      ]
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "gap_choice",
    "selections": [
      {
        "slotId": "ending",
        "optionId": "e"
      }
    ]
  }
]
```

Nach „die“ im femininen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

Ambiguity: This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

### v-16-1

Revision: 10000000-0000-4000-8000-000000001033@2; context: context-16-1; transfer: transfer-16

Die wichtig___ Frage bleibt offen.

Wähle die fehlende Adjektivendung.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001033",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000016",
  "hint": {
    "en": "The definite article already marks feminine nominative, so the adjective uses the weak pattern.",
    "de": "Der bestimmte Artikel markiert bereits Femininum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination."
  },
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "type": "gap_choice",
  "prompt": "Die wichtig___ Frage bleibt offen.",
  "slots": [
    {
      "id": "ending",
      "label": "Adjektivendung",
      "options": [
        {
          "id": "es",
          "text": "-es"
        },
        {
          "id": "en",
          "text": "-en"
        },
        {
          "id": "e",
          "text": "-e"
        },
        {
          "id": "er",
          "text": "-er"
        },
        {
          "id": "em",
          "text": "-em"
        }
      ]
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "gap_choice",
    "selections": [
      {
        "slotId": "ending",
        "optionId": "e"
      }
    ]
  }
]
```

Nach „die“ im femininen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

Ambiguity: This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

## Adjektivendung: Neutrum im Nominativ nach „das“

Target: 10000000-0000-4000-8000-000000000017 · B1 · adjective

Choose the weak adjective ending after das in neuter nominative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: eab8205faa06d9dcb3b115b50c502901ccc016143ab416a01a9c0d00ac78e0df

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-17-0

Revision: 10000000-0000-4000-8000-000000001034@2; context: context-17-0; transfer: none

Das klein___ Büro ist frei.

Wähle die fehlende Adjektivendung.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001034",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000017",
  "hint": {
    "en": "The definite article already marks neuter nominative, so the adjective uses the weak pattern.",
    "de": "Der bestimmte Artikel markiert bereits Neutrum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination."
  },
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "type": "gap_choice",
  "prompt": "Das klein___ Büro ist frei.",
  "slots": [
    {
      "id": "ending",
      "label": "Adjektivendung",
      "options": [
        {
          "id": "em",
          "text": "-em"
        },
        {
          "id": "er",
          "text": "-er"
        },
        {
          "id": "es",
          "text": "-es"
        },
        {
          "id": "en",
          "text": "-en"
        },
        {
          "id": "e",
          "text": "-e"
        }
      ]
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "gap_choice",
    "selections": [
      {
        "slotId": "ending",
        "optionId": "e"
      }
    ]
  }
]
```

Nach „das“ im neutralen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

Ambiguity: This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

### v-17-1

Revision: 10000000-0000-4000-8000-000000001035@2; context: context-17-1; transfer: transfer-17

Das neu___ Gerät funktioniert gut.

Wähle die fehlende Adjektivendung.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001035",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000017",
  "hint": {
    "en": "The definite article already marks neuter nominative, so the adjective uses the weak pattern.",
    "de": "Der bestimmte Artikel markiert bereits Neutrum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination."
  },
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "type": "gap_choice",
  "prompt": "Das neu___ Gerät funktioniert gut.",
  "slots": [
    {
      "id": "ending",
      "label": "Adjektivendung",
      "options": [
        {
          "id": "en",
          "text": "-en"
        },
        {
          "id": "e",
          "text": "-e"
        },
        {
          "id": "em",
          "text": "-em"
        },
        {
          "id": "es",
          "text": "-es"
        },
        {
          "id": "er",
          "text": "-er"
        }
      ]
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "gap_choice",
    "selections": [
      {
        "slotId": "ending",
        "optionId": "e"
      }
    ]
  }
]
```

Nach „das“ im neutralen Nominativ bekommt ein schwach dekliniertes Adjektiv die Endung -e.

Ambiguity: This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

## Adjektivendung: Maskulinum im Akkusativ nach „den“

Target: 10000000-0000-4000-8000-000000000018 · B1 · adjective

Choose the weak adjective ending after den in masculine accusative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 358b7486d216002893d6f99c30dc19f2e76041adc3fe256b42fe576bcfbe95ae

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-18-0

Revision: 10000000-0000-4000-8000-000000001036@2; context: context-18-0; transfer: none

Ich sehe den neu___ Kollegen.

Wähle die fehlende Adjektivendung.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001036",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000018",
  "hint": {
    "en": "After a definite article in masculine accusative, use the weak adjective pattern.",
    "de": "Nach einem bestimmten Artikel im maskulinen Akkusativ folgt das Adjektiv der schwachen Deklination."
  },
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "type": "gap_choice",
  "prompt": "Ich sehe den neu___ Kollegen.",
  "slots": [
    {
      "id": "ending",
      "label": "Adjektivendung",
      "options": [
        {
          "id": "es",
          "text": "-es"
        },
        {
          "id": "en",
          "text": "-en"
        },
        {
          "id": "er",
          "text": "-er"
        },
        {
          "id": "e",
          "text": "-e"
        },
        {
          "id": "em",
          "text": "-em"
        }
      ]
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "gap_choice",
    "selections": [
      {
        "slotId": "ending",
        "optionId": "en"
      }
    ]
  }
]
```

Nach „den“ im maskulinen Akkusativ bekommt ein schwach dekliniertes Adjektiv die Endung -en.

Ambiguity: This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

### v-18-1

Revision: 10000000-0000-4000-8000-000000001037@2; context: context-18-1; transfer: transfer-18

Wir begrüßen den wichtig___ Kunden.

Wähle die fehlende Adjektivendung.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001037",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000018",
  "hint": {
    "en": "After a definite article in masculine accusative, use the weak adjective pattern.",
    "de": "Nach einem bestimmten Artikel im maskulinen Akkusativ folgt das Adjektiv der schwachen Deklination."
  },
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "type": "gap_choice",
  "prompt": "Wir begrüßen den wichtig___ Kunden.",
  "slots": [
    {
      "id": "ending",
      "label": "Adjektivendung",
      "options": [
        {
          "id": "em",
          "text": "-em"
        },
        {
          "id": "er",
          "text": "-er"
        },
        {
          "id": "e",
          "text": "-e"
        },
        {
          "id": "es",
          "text": "-es"
        },
        {
          "id": "en",
          "text": "-en"
        }
      ]
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "gap_choice",
    "selections": [
      {
        "slotId": "ending",
        "optionId": "en"
      }
    ]
  }
]
```

Nach „den“ im maskulinen Akkusativ bekommt ein schwach dekliniertes Adjektiv die Endung -en.

Ambiguity: This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

## Adjektivendung: Dativ nach bestimmtem Artikel

Target: 10000000-0000-4000-8000-000000000019 · B1 · adjective

Choose the weak adjective ending after a definite article in the dative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: a46529d476bcb94f3c32700af9c8d49bf86d570a4c337cb96d555d6e2bd3fc5a

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-19-0

Revision: 10000000-0000-4000-8000-000000001038@2; context: context-19-0; transfer: none

Ich spreche mit dem nett___ Kollegen.

Wähle die fehlende Adjektivendung.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001038",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000019",
  "hint": {
    "en": "After a definite article in the dative, weak adjectives use the same ending across these genders.",
    "de": "Nach einem bestimmten Artikel im Dativ haben schwach deklinierte Adjektive in diesen Genera dieselbe Endung."
  },
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "type": "gap_choice",
  "prompt": "Ich spreche mit dem nett___ Kollegen.",
  "slots": [
    {
      "id": "ending",
      "label": "Adjektivendung",
      "options": [
        {
          "id": "en",
          "text": "-en"
        },
        {
          "id": "es",
          "text": "-es"
        },
        {
          "id": "em",
          "text": "-em"
        },
        {
          "id": "er",
          "text": "-er"
        },
        {
          "id": "e",
          "text": "-e"
        }
      ]
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "gap_choice",
    "selections": [
      {
        "slotId": "ending",
        "optionId": "en"
      }
    ]
  }
]
```

Nach einem bestimmten Artikel im Dativ bekommt ein schwach dekliniertes Adjektiv die Endung -en.

Ambiguity: This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

### v-19-1

Revision: 10000000-0000-4000-8000-000000001039@2; context: context-19-1; transfer: transfer-19

Sie arbeitet mit der erfahren___ Ärztin.

Wähle die fehlende Adjektivendung.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001039",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000019",
  "hint": {
    "en": "After a definite article in the dative, weak adjectives use the same ending across these genders.",
    "de": "Nach einem bestimmten Artikel im Dativ haben schwach deklinierte Adjektive in diesen Genera dieselbe Endung."
  },
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "type": "gap_choice",
  "prompt": "Sie arbeitet mit der erfahren___ Ärztin.",
  "slots": [
    {
      "id": "ending",
      "label": "Adjektivendung",
      "options": [
        {
          "id": "e",
          "text": "-e"
        },
        {
          "id": "er",
          "text": "-er"
        },
        {
          "id": "en",
          "text": "-en"
        },
        {
          "id": "em",
          "text": "-em"
        },
        {
          "id": "es",
          "text": "-es"
        }
      ]
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "gap_choice",
    "selections": [
      {
        "slotId": "ending",
        "optionId": "en"
      }
    ]
  }
]
```

Nach einem bestimmten Artikel im Dativ bekommt ein schwach dekliniertes Adjektiv die Endung -en.

Ambiguity: This revision measures recognition of the weak adjective ending in an explicit case/gender context. The distractors are authored German adjective endings; it does not claim unrestricted written production.

## Präsens: fahren (du/er)

Target: 10000000-0000-4000-8000-000000000020 · B1 · verb

Conjugate fahren in the second- and third-person singular present.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 92ede20c10b4a693bcffc92dbce93fafc25e7a42130ccaf75716c67ee3f696aa

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-20-0

Revision: 10000000-0000-4000-8000-000000001040@1; context: context-20-0; transfer: none

Du ___ jeden Morgen mit dem Bus. Er ___ heute mit dem Zug.

Ergänze beide Formen von „fahren“ im Präsens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001040",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000020",
  "hint": {
    "en": "In the du and er forms of fahren, the stem vowel a changes to ä.",
    "de": "Bei „fahren“ wird in den Formen mit „du“ und „er“ der Stammvokal a zu ä."
  },
  "instruction": {
    "en": "Complete both present-tense forms of fahren.",
    "de": "Ergänze beide Formen von „fahren“ im Präsens."
  },
  "type": "multi_slot",
  "prompt": "Du ___ jeden Morgen mit dem Bus. Er ___ heute mit dem Zug.",
  "slots": [
    {
      "id": "du",
      "label": "du"
    },
    {
      "id": "er",
      "label": "er"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "multi_slot",
    "values": [
      {
        "slotId": "du",
        "text": "fährst"
      },
      {
        "slotId": "er",
        "text": "fährt"
      }
    ]
  }
]
```

Der Stammvokal wechselt a → ä: du fährst, er fährt.

Ambiguity: Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

### v-20-1

Revision: 10000000-0000-4000-8000-000000001041@1; context: context-20-1; transfer: transfer-20

Du ___ morgen nach Leipzig. Er ___ am Wochenende zu seinen Eltern.

Ergänze beide Formen von „fahren“ im Präsens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001041",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000020",
  "hint": {
    "en": "In the du and er forms of fahren, the stem vowel a changes to ä.",
    "de": "Bei „fahren“ wird in den Formen mit „du“ und „er“ der Stammvokal a zu ä."
  },
  "instruction": {
    "en": "Complete both present-tense forms of fahren.",
    "de": "Ergänze beide Formen von „fahren“ im Präsens."
  },
  "type": "multi_slot",
  "prompt": "Du ___ morgen nach Leipzig. Er ___ am Wochenende zu seinen Eltern.",
  "slots": [
    {
      "id": "du",
      "label": "du"
    },
    {
      "id": "er",
      "label": "er"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "multi_slot",
    "values": [
      {
        "slotId": "du",
        "text": "fährst"
      },
      {
        "slotId": "er",
        "text": "fährt"
      }
    ]
  }
]
```

Der Stammvokal wechselt a → ä: du fährst, er fährt.

Ambiguity: Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

## Präsens: lesen (du/er)

Target: 10000000-0000-4000-8000-000000000021 · B1 · verb

Conjugate lesen in the second- and third-person singular present.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: fb350e3f8ee5ada81969042c60d379e565d2e42cf718d9f9524d58604e1c3858

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-21-0

Revision: 10000000-0000-4000-8000-000000001042@1; context: context-21-0; transfer: none

Du ___ gerade den Bericht. Er ___ jeden Morgen die Zeitung.

Ergänze beide Formen von „lesen“ im Präsens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001042",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000021",
  "hint": {
    "en": "In the du and er forms of lesen, e changes to ie.",
    "de": "Bei „lesen“ wird in den Formen mit „du“ und „er“ e zu ie."
  },
  "instruction": {
    "en": "Complete both present-tense forms of lesen.",
    "de": "Ergänze beide Formen von „lesen“ im Präsens."
  },
  "type": "multi_slot",
  "prompt": "Du ___ gerade den Bericht. Er ___ jeden Morgen die Zeitung.",
  "slots": [
    {
      "id": "du",
      "label": "du"
    },
    {
      "id": "er",
      "label": "er"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "multi_slot",
    "values": [
      {
        "slotId": "du",
        "text": "liest"
      },
      {
        "slotId": "er",
        "text": "liest"
      }
    ]
  }
]
```

Der Stammvokal wechselt e → ie; beide Formen lauten „liest“: du liest, er liest.

Ambiguity: Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

### v-21-1

Revision: 10000000-0000-4000-8000-000000001043@1; context: context-21-1; transfer: transfer-21

Du ___ die Nachricht noch einmal. Er ___ oft deutsche Romane.

Ergänze beide Formen von „lesen“ im Präsens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001043",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000021",
  "hint": {
    "en": "In the du and er forms of lesen, e changes to ie.",
    "de": "Bei „lesen“ wird in den Formen mit „du“ und „er“ e zu ie."
  },
  "instruction": {
    "en": "Complete both present-tense forms of lesen.",
    "de": "Ergänze beide Formen von „lesen“ im Präsens."
  },
  "type": "multi_slot",
  "prompt": "Du ___ die Nachricht noch einmal. Er ___ oft deutsche Romane.",
  "slots": [
    {
      "id": "du",
      "label": "du"
    },
    {
      "id": "er",
      "label": "er"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "multi_slot",
    "values": [
      {
        "slotId": "du",
        "text": "liest"
      },
      {
        "slotId": "er",
        "text": "liest"
      }
    ]
  }
]
```

Der Stammvokal wechselt e → ie; beide Formen lauten „liest“: du liest, er liest.

Ambiguity: Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

## Präsens: geben (du/er)

Target: 10000000-0000-4000-8000-000000000022 · B1 · verb

Conjugate geben in the second- and third-person singular present.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: b6707349a80f62b8abeabc4031aedf549aa13166642423c8b138cd09f53bde3b

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-22-0

Revision: 10000000-0000-4000-8000-000000001044@1; context: context-22-0; transfer: none

Du ___ mir bitte den Schlüssel. Er ___ der Kollegin die Unterlagen.

Ergänze beide Formen von „geben“ im Präsens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001044",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000022",
  "hint": {
    "en": "In the du and er forms of geben, e changes to i.",
    "de": "Bei „geben“ wird in den Formen mit „du“ und „er“ e zu i."
  },
  "instruction": {
    "en": "Complete both present-tense forms of geben.",
    "de": "Ergänze beide Formen von „geben“ im Präsens."
  },
  "type": "multi_slot",
  "prompt": "Du ___ mir bitte den Schlüssel. Er ___ der Kollegin die Unterlagen.",
  "slots": [
    {
      "id": "du",
      "label": "du"
    },
    {
      "id": "er",
      "label": "er"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "multi_slot",
    "values": [
      {
        "slotId": "du",
        "text": "gibst"
      },
      {
        "slotId": "er",
        "text": "gibt"
      }
    ]
  }
]
```

Der Stammvokal wechselt e → i: du gibst, er gibt.

Ambiguity: Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

### v-22-1

Revision: 10000000-0000-4000-8000-000000001045@1; context: context-22-1; transfer: transfer-22

Du ___ dem Kind Wasser. Er ___ uns eine klare Antwort.

Ergänze beide Formen von „geben“ im Präsens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001045",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000022",
  "hint": {
    "en": "In the du and er forms of geben, e changes to i.",
    "de": "Bei „geben“ wird in den Formen mit „du“ und „er“ e zu i."
  },
  "instruction": {
    "en": "Complete both present-tense forms of geben.",
    "de": "Ergänze beide Formen von „geben“ im Präsens."
  },
  "type": "multi_slot",
  "prompt": "Du ___ dem Kind Wasser. Er ___ uns eine klare Antwort.",
  "slots": [
    {
      "id": "du",
      "label": "du"
    },
    {
      "id": "er",
      "label": "er"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "multi_slot",
    "values": [
      {
        "slotId": "du",
        "text": "gibst"
      },
      {
        "slotId": "er",
        "text": "gibt"
      }
    ]
  }
]
```

Der Stammvokal wechselt e → i: du gibst, er gibt.

Ambiguity: Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

## Präsens: nehmen (du/er)

Target: 10000000-0000-4000-8000-000000000023 · B1 · verb

Conjugate nehmen in the second- and third-person singular present.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 865d2d3b4ca7594a001ece0a645878d98d0b6f0d174786acc51cf77d9d213de8

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-23-0

Revision: 10000000-0000-4000-8000-000000001046@1; context: context-23-0; transfer: none

Du ___ morgens den Bus. Er ___ lieber das Fahrrad.

Ergänze beide Formen von „nehmen“ im Präsens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001046",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000023",
  "hint": {
    "en": "The du and er forms use the irregular stem nimm-.",
    "de": "Die Formen mit „du“ und „er“ verwenden den unregelmäßigen Stamm „nimm-“."
  },
  "instruction": {
    "en": "Complete both present-tense forms of nehmen.",
    "de": "Ergänze beide Formen von „nehmen“ im Präsens."
  },
  "type": "multi_slot",
  "prompt": "Du ___ morgens den Bus. Er ___ lieber das Fahrrad.",
  "slots": [
    {
      "id": "du",
      "label": "du"
    },
    {
      "id": "er",
      "label": "er"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "multi_slot",
    "values": [
      {
        "slotId": "du",
        "text": "nimmst"
      },
      {
        "slotId": "er",
        "text": "nimmt"
      }
    ]
  }
]
```

Im Singular lautet der Stamm „nimm-“: du nimmst, er nimmt.

Ambiguity: Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

### v-23-1

Revision: 10000000-0000-4000-8000-000000001047@1; context: context-23-1; transfer: transfer-23

Du ___ noch einen Kaffee. Er ___ die letzte Tablette.

Ergänze beide Formen von „nehmen“ im Präsens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001047",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000023",
  "hint": {
    "en": "The du and er forms use the irregular stem nimm-.",
    "de": "Die Formen mit „du“ und „er“ verwenden den unregelmäßigen Stamm „nimm-“."
  },
  "instruction": {
    "en": "Complete both present-tense forms of nehmen.",
    "de": "Ergänze beide Formen von „nehmen“ im Präsens."
  },
  "type": "multi_slot",
  "prompt": "Du ___ noch einen Kaffee. Er ___ die letzte Tablette.",
  "slots": [
    {
      "id": "du",
      "label": "du"
    },
    {
      "id": "er",
      "label": "er"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "multi_slot",
    "values": [
      {
        "slotId": "du",
        "text": "nimmst"
      },
      {
        "slotId": "er",
        "text": "nimmt"
      }
    ]
  }
]
```

Im Singular lautet der Stamm „nimm-“: du nimmst, er nimmt.

Ambiguity: Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

## Präsens: sprechen (du/er)

Target: 10000000-0000-4000-8000-000000000024 · B1 · verb

Conjugate sprechen in the second- and third-person singular present.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 68dd8cd1dc3009ab356b1024bed17e7f5a62c16745a0e70e11a6e587e441074b

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-24-0

Revision: 10000000-0000-4000-8000-000000001048@1; context: context-24-0; transfer: none

Du ___ sehr gut Deutsch. Er ___ heute mit der Ärztin.

Ergänze beide Formen von „sprechen“ im Präsens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001048",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000024",
  "hint": {
    "en": "In the du and er forms of sprechen, e changes to i.",
    "de": "Bei „sprechen“ wird in den Formen mit „du“ und „er“ e zu i."
  },
  "instruction": {
    "en": "Complete both present-tense forms of sprechen.",
    "de": "Ergänze beide Formen von „sprechen“ im Präsens."
  },
  "type": "multi_slot",
  "prompt": "Du ___ sehr gut Deutsch. Er ___ heute mit der Ärztin.",
  "slots": [
    {
      "id": "du",
      "label": "du"
    },
    {
      "id": "er",
      "label": "er"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "multi_slot",
    "values": [
      {
        "slotId": "du",
        "text": "sprichst"
      },
      {
        "slotId": "er",
        "text": "spricht"
      }
    ]
  }
]
```

Der Stammvokal wechselt e → i: du sprichst, er spricht.

Ambiguity: Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

### v-24-1

Revision: 10000000-0000-4000-8000-000000001049@1; context: context-24-1; transfer: transfer-24

Du ___ morgen mit deinem Chef. Er ___ oft über seine Arbeit.

Ergänze beide Formen von „sprechen“ im Präsens.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001049",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000024",
  "hint": {
    "en": "In the du and er forms of sprechen, e changes to i.",
    "de": "Bei „sprechen“ wird in den Formen mit „du“ und „er“ e zu i."
  },
  "instruction": {
    "en": "Complete both present-tense forms of sprechen.",
    "de": "Ergänze beide Formen von „sprechen“ im Präsens."
  },
  "type": "multi_slot",
  "prompt": "Du ___ morgen mit deinem Chef. Er ___ oft über seine Arbeit.",
  "slots": [
    {
      "id": "du",
      "label": "du"
    },
    {
      "id": "er",
      "label": "er"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "multi_slot",
    "values": [
      {
        "slotId": "du",
        "text": "sprichst"
      },
      {
        "slotId": "er",
        "text": "spricht"
      }
    ]
  }
]
```

Der Stammvokal wechselt e → i: du sprichst, er spricht.

Ambiguity: Both subjects are explicit, and each slot asks only for the finite present-tense form of the named infinitive.

## Nebensatz mit „weil“

Target: 10000000-0000-4000-8000-000000000025 · B1 · word-order

Place the finite verb at the end of a subordinate clause introduced by weil.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 1834e08db5914e8c8fe246107f8df7b71d1242671dc6612aa464245bd4d0c5d9

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-25-0

Revision: 10000000-0000-4000-8000-000000001050@1; context: context-25-0; transfer: none

arbeite / weil / heute / ich

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001050",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000025",
  "hint": {
    "en": "After weil, the finite verb belongs at the end of the clause.",
    "de": "Nach „weil“ steht das finite Verb am Ende des Satzes."
  },
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "type": "word_order",
  "prompt": "arbeite / weil / heute / ich",
  "tokens": [
    {
      "id": "0",
      "text": "arbeite"
    },
    {
      "id": "1",
      "text": "weil"
    },
    {
      "id": "2",
      "text": "heute"
    },
    {
      "id": "3",
      "text": "ich"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "word_order",
    "tokenIds": [
      "1",
      "3",
      "2",
      "0"
    ]
  }
]
```

Das finite Verb steht am Ende: weil ich heute arbeite.

Ambiguity: German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

### v-25-1

Revision: 10000000-0000-4000-8000-000000001051@1; context: context-25-1; transfer: transfer-25

braucht / weil / Hilfe / sie

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001051",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000025",
  "hint": {
    "en": "After weil, the finite verb belongs at the end of the clause.",
    "de": "Nach „weil“ steht das finite Verb am Ende des Satzes."
  },
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "type": "word_order",
  "prompt": "braucht / weil / Hilfe / sie",
  "tokens": [
    {
      "id": "0",
      "text": "braucht"
    },
    {
      "id": "1",
      "text": "weil"
    },
    {
      "id": "2",
      "text": "Hilfe"
    },
    {
      "id": "3",
      "text": "sie"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "word_order",
    "tokenIds": [
      "1",
      "3",
      "2",
      "0"
    ]
  }
]
```

Das finite Verb steht am Ende: weil sie Hilfe braucht.

Ambiguity: German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

## Nebensatz mit „dass“

Target: 10000000-0000-4000-8000-000000000026 · B1 · word-order

Place the finite verb at the end of a subordinate clause introduced by dass.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 0ddfd6e956d4fc0d92a181e9b45950f0a5d3b10b7ef216dc56ebd27400632d1c

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-26-0

Revision: 10000000-0000-4000-8000-000000001052@1; context: context-26-0; transfer: none

starten / dass / morgen / wir

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001052",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000026",
  "hint": {
    "en": "After dass, the finite verb belongs at the end of the clause.",
    "de": "Nach „dass“ steht das finite Verb am Ende des Satzes."
  },
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "type": "word_order",
  "prompt": "starten / dass / morgen / wir",
  "tokens": [
    {
      "id": "0",
      "text": "starten"
    },
    {
      "id": "1",
      "text": "dass"
    },
    {
      "id": "2",
      "text": "morgen"
    },
    {
      "id": "3",
      "text": "wir"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "word_order",
    "tokenIds": [
      "1",
      "3",
      "2",
      "0"
    ]
  }
]
```

Das finite Verb steht am Ende: dass wir morgen starten.

Ambiguity: German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

### v-26-1

Revision: 10000000-0000-4000-8000-000000001053@1; context: context-26-1; transfer: transfer-26

bestätigt / dass / den Termin / er

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001053",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000026",
  "hint": {
    "en": "After dass, the finite verb belongs at the end of the clause.",
    "de": "Nach „dass“ steht das finite Verb am Ende des Satzes."
  },
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "type": "word_order",
  "prompt": "bestätigt / dass / den Termin / er",
  "tokens": [
    {
      "id": "0",
      "text": "bestätigt"
    },
    {
      "id": "1",
      "text": "dass"
    },
    {
      "id": "2",
      "text": "den Termin"
    },
    {
      "id": "3",
      "text": "er"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "word_order",
    "tokenIds": [
      "1",
      "3",
      "2",
      "0"
    ]
  }
]
```

Das finite Verb steht am Ende: dass er den Termin bestätigt.

Ambiguity: German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

## Nebensatz mit „obwohl“

Target: 10000000-0000-4000-8000-000000000027 · B1 · word-order

Place the finite verb at the end of a subordinate clause introduced by obwohl.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: d8564ece61977e3997c40c9075aec3b4700136f8153b9894203fc8d291c91f12

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-27-0

Revision: 10000000-0000-4000-8000-000000001054@1; context: context-27-0; transfer: none

bin / obwohl / müde / ich

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001054",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000027",
  "hint": {
    "en": "After obwohl, the finite verb belongs at the end of the clause.",
    "de": "Nach „obwohl“ steht das finite Verb am Ende des Satzes."
  },
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "type": "word_order",
  "prompt": "bin / obwohl / müde / ich",
  "tokens": [
    {
      "id": "0",
      "text": "bin"
    },
    {
      "id": "1",
      "text": "obwohl"
    },
    {
      "id": "2",
      "text": "müde"
    },
    {
      "id": "3",
      "text": "ich"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "word_order",
    "tokenIds": [
      "1",
      "3",
      "2",
      "0"
    ]
  }
]
```

Das finite Verb steht am Ende: obwohl ich müde bin.

Ambiguity: German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

### v-27-1

Revision: 10000000-0000-4000-8000-000000001055@1; context: context-27-1; transfer: transfer-27

hat / obwohl / wenig Zeit / sie

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001055",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000027",
  "hint": {
    "en": "After obwohl, the finite verb belongs at the end of the clause.",
    "de": "Nach „obwohl“ steht das finite Verb am Ende des Satzes."
  },
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "type": "word_order",
  "prompt": "hat / obwohl / wenig Zeit / sie",
  "tokens": [
    {
      "id": "0",
      "text": "hat"
    },
    {
      "id": "1",
      "text": "obwohl"
    },
    {
      "id": "2",
      "text": "wenig Zeit"
    },
    {
      "id": "3",
      "text": "sie"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "word_order",
    "tokenIds": [
      "1",
      "3",
      "2",
      "0"
    ]
  }
]
```

Das finite Verb steht am Ende: obwohl sie wenig Zeit hat.

Ambiguity: German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

## Nebensatz mit „wenn“

Target: 10000000-0000-4000-8000-000000000028 · B1 · word-order

Place the finite verb at the end of a subordinate clause introduced by wenn.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: ad503af2dec670ce068c5cdd3eb8a49542485fb2b5b0a76391bbdd5d339ee835

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-28-0

Revision: 10000000-0000-4000-8000-000000001056@1; context: context-28-0; transfer: none

habe / wenn / Zeit / ich

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001056",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000028",
  "hint": {
    "en": "After wenn, the finite verb belongs at the end of the clause.",
    "de": "Nach „wenn“ steht das finite Verb am Ende des Satzes."
  },
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "type": "word_order",
  "prompt": "habe / wenn / Zeit / ich",
  "tokens": [
    {
      "id": "0",
      "text": "habe"
    },
    {
      "id": "1",
      "text": "wenn"
    },
    {
      "id": "2",
      "text": "Zeit"
    },
    {
      "id": "3",
      "text": "ich"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "word_order",
    "tokenIds": [
      "1",
      "3",
      "2",
      "0"
    ]
  }
]
```

Das finite Verb steht am Ende: wenn ich Zeit habe.

Ambiguity: German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

### v-28-1

Revision: 10000000-0000-4000-8000-000000001057@1; context: context-28-1; transfer: transfer-28

kommt / wenn / pünktlich / der Zug

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001057",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000028",
  "hint": {
    "en": "After wenn, the finite verb belongs at the end of the clause.",
    "de": "Nach „wenn“ steht das finite Verb am Ende des Satzes."
  },
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "type": "word_order",
  "prompt": "kommt / wenn / pünktlich / der Zug",
  "tokens": [
    {
      "id": "0",
      "text": "kommt"
    },
    {
      "id": "1",
      "text": "wenn"
    },
    {
      "id": "2",
      "text": "pünktlich"
    },
    {
      "id": "3",
      "text": "der Zug"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "word_order",
    "tokenIds": [
      "1",
      "3",
      "2",
      "0"
    ]
  }
]
```

Das finite Verb steht am Ende: wenn der Zug pünktlich kommt.

Ambiguity: German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

## Indirekte Frage mit „ob“

Target: 10000000-0000-4000-8000-000000000029 · B1 · word-order

Place the finite verb at the end of an indirect yes/no question introduced by ob.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 0af6c33a7138c9747c77889520396a9592a1f49e43f4451ffe9609b78b3f2f65

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-29-0

Revision: 10000000-0000-4000-8000-000000001058@1; context: context-29-0; transfer: none

kommst / ob / morgen / du

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001058",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000029",
  "hint": {
    "en": "After ob, the finite verb belongs at the end of the clause.",
    "de": "Nach „ob“ steht das finite Verb am Ende des Satzes."
  },
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "type": "word_order",
  "prompt": "kommst / ob / morgen / du",
  "tokens": [
    {
      "id": "0",
      "text": "kommst"
    },
    {
      "id": "1",
      "text": "ob"
    },
    {
      "id": "2",
      "text": "morgen"
    },
    {
      "id": "3",
      "text": "du"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "word_order",
    "tokenIds": [
      "1",
      "3",
      "2",
      "0"
    ]
  }
]
```

Das finite Verb steht am Ende: ob du morgen kommst.

Ambiguity: German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

### v-29-1

Revision: 10000000-0000-4000-8000-000000001059@1; context: context-29-1; transfer: transfer-29

hat / ob / genug Zeit / sie

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001059",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000029",
  "hint": {
    "en": "After ob, the finite verb belongs at the end of the clause.",
    "de": "Nach „ob“ steht das finite Verb am Ende des Satzes."
  },
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "type": "word_order",
  "prompt": "hat / ob / genug Zeit / sie",
  "tokens": [
    {
      "id": "0",
      "text": "hat"
    },
    {
      "id": "1",
      "text": "ob"
    },
    {
      "id": "2",
      "text": "genug Zeit"
    },
    {
      "id": "3",
      "text": "sie"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "word_order",
    "tokenIds": [
      "1",
      "3",
      "2",
      "0"
    ]
  }
]
```

Das finite Verb steht am Ende: ob sie genug Zeit hat.

Ambiguity: German can allow marked constituent orders, but this exercise explicitly fixes conjunction → subject → remaining phrase → finite verb to isolate verb-final placement.

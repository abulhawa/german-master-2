# Initial German-language review workbook

Generated from initial-30.json. Agent-authored, unpublished drafts. Validation is structural and grading conformance evidence, not German-language approval.

Targets: 30; variants: 60; independently approved: 0.

For each target, check objective, B1/B2 suitability, grammar, naturalness, ambiguity, all accepted alternatives, distractors, hint leakage, explanation, context variation and provenance. Record reviewer, date, approved/changes-requested status and checklist findings in the JSON. Publication remains separately authorized.

## Plural: der Antrag

Target: 10000000-0000-4000-8000-000000000000 · B1 · plural

Recall the plural of der Antrag.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 6c8228569b9f1e2924669fd621f0de7bc30fc23b1db2c027152724dd2c832aee

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-0-0

Revision: 10000000-0000-4000-8000-000000001000@1; context: context-0-0; transfer: none

Schreiben Sie den Plural von „der Antrag“ ohne Artikel.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "short_answer",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001000",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000000",
  "prompt": "Schreiben Sie den Plural von „der Antrag“ ohne Artikel.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  }
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

Der Plural lautet Anträge. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-0-1

Revision: 10000000-0000-4000-8000-000000001001@1; context: context-0-1; transfer: transfer-0

Ergänzen Sie die Pluralform von „der Antrag“: mehrere ___.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001001",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000000",
  "prompt": "Ergänzen Sie die Pluralform von „der Antrag“: mehrere ___.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Der Plural lautet Anträge. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Plural: die Rechnung

Target: 10000000-0000-4000-8000-000000000001 · B1 · plural

Recall the plural of die Rechnung.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 95a911a32b6a79eb9fd0b61f25ae1d480776a4b7cde2a84a3f02ff518115aaa2

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-1-0

Revision: 10000000-0000-4000-8000-000000001002@1; context: context-1-0; transfer: none

Schreiben Sie den Plural von „die Rechnung“ ohne Artikel.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "short_answer",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001002",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000001",
  "prompt": "Schreiben Sie den Plural von „die Rechnung“ ohne Artikel.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  }
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

Der Plural lautet Rechnungen. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-1-1

Revision: 10000000-0000-4000-8000-000000001003@1; context: context-1-1; transfer: transfer-1

Ergänzen Sie die Pluralform von „die Rechnung“: mehrere ___.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001003",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000001",
  "prompt": "Ergänzen Sie die Pluralform von „die Rechnung“: mehrere ___.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Der Plural lautet Rechnungen. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Plural: der Termin

Target: 10000000-0000-4000-8000-000000000002 · B1 · plural

Recall the plural of der Termin.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: ed84f16a872a8305012914fb992b7a1c4f4bf1758d9b2038ad961a316a72437f

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-2-0

Revision: 10000000-0000-4000-8000-000000001004@1; context: context-2-0; transfer: none

Schreiben Sie den Plural von „der Termin“ ohne Artikel.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "short_answer",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001004",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000002",
  "prompt": "Schreiben Sie den Plural von „der Termin“ ohne Artikel.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  }
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

Der Plural lautet Termine. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-2-1

Revision: 10000000-0000-4000-8000-000000001005@1; context: context-2-1; transfer: transfer-2

Ergänzen Sie die Pluralform von „der Termin“: mehrere ___.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001005",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000002",
  "prompt": "Ergänzen Sie die Pluralform von „der Termin“: mehrere ___.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Der Plural lautet Termine. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Plural: das Gespräch

Target: 10000000-0000-4000-8000-000000000003 · B1 · plural

Recall the plural of das Gespräch.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: cab27ff79d2fe38d010bd19d966fed28bddc34f814b26a1bcbb9d4933f7ade39

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-3-0

Revision: 10000000-0000-4000-8000-000000001006@1; context: context-3-0; transfer: none

Schreiben Sie den Plural von „das Gespräch“ ohne Artikel.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "short_answer",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001006",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000003",
  "prompt": "Schreiben Sie den Plural von „das Gespräch“ ohne Artikel.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  }
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

Der Plural lautet Gespräche. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-3-1

Revision: 10000000-0000-4000-8000-000000001007@1; context: context-3-1; transfer: transfer-3

Ergänzen Sie die Pluralform von „das Gespräch“: mehrere ___.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001007",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000003",
  "prompt": "Ergänzen Sie die Pluralform von „das Gespräch“: mehrere ___.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Der Plural lautet Gespräche. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Plural: der Vertrag

Target: 10000000-0000-4000-8000-000000000004 · B1 · plural

Recall the plural of der Vertrag.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 5d3b8245471ce87e455aec5c3a4ee91bd7d9abb08719865ef22a5cc1a6e29f8f

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-4-0

Revision: 10000000-0000-4000-8000-000000001008@1; context: context-4-0; transfer: none

Schreiben Sie den Plural von „der Vertrag“ ohne Artikel.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "short_answer",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001008",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000004",
  "prompt": "Schreiben Sie den Plural von „der Vertrag“ ohne Artikel.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  }
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

Der Plural lautet Verträge. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-4-1

Revision: 10000000-0000-4000-8000-000000001009@1; context: context-4-1; transfer: transfer-4

Ergänzen Sie die Pluralform von „der Vertrag“: mehrere ___.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001009",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000004",
  "prompt": "Ergänzen Sie die Pluralform von „der Vertrag“: mehrere ___.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Der Plural lautet Verträge. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Plural: die Erfahrung

Target: 10000000-0000-4000-8000-000000000005 · B1 · plural

Recall the plural of die Erfahrung.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 43a90b59e8acd2327636b001d68558478dde94cccc3cc73612231fdb99fd84ee

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-5-0

Revision: 10000000-0000-4000-8000-000000001010@1; context: context-5-0; transfer: none

Schreiben Sie den Plural von „die Erfahrung“ ohne Artikel.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "short_answer",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001010",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000005",
  "prompt": "Schreiben Sie den Plural von „die Erfahrung“ ohne Artikel.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  }
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

Der Plural lautet Erfahrungen. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-5-1

Revision: 10000000-0000-4000-8000-000000001011@1; context: context-5-1; transfer: transfer-5

Ergänzen Sie die Pluralform von „die Erfahrung“: mehrere ___.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001011",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000005",
  "prompt": "Ergänzen Sie die Pluralform von „die Erfahrung“: mehrere ___.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Der Plural lautet Erfahrungen. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Plural: das Angebot

Target: 10000000-0000-4000-8000-000000000006 · B1 · plural

Recall the plural of das Angebot.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: c1302c451b54ef9baf4657d34be9fd16f76df53b323321c153817e881e512c57

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-6-0

Revision: 10000000-0000-4000-8000-000000001012@1; context: context-6-0; transfer: none

Schreiben Sie den Plural von „das Angebot“ ohne Artikel.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "short_answer",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001012",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000006",
  "prompt": "Schreiben Sie den Plural von „das Angebot“ ohne Artikel.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  }
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

Der Plural lautet Angebote. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-6-1

Revision: 10000000-0000-4000-8000-000000001013@1; context: context-6-1; transfer: transfer-6

Ergänzen Sie die Pluralform von „das Angebot“: mehrere ___.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001013",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000006",
  "prompt": "Ergänzen Sie die Pluralform von „das Angebot“: mehrere ___.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Der Plural lautet Angebote. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Plural: die Entscheidung

Target: 10000000-0000-4000-8000-000000000007 · B1 · plural

Recall the plural of die Entscheidung.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: ac4aeb5ff821c1b36d3c146ef61ef0a5580f921a35a8f7318bf589ab8f4c8b96

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-7-0

Revision: 10000000-0000-4000-8000-000000001014@1; context: context-7-0; transfer: none

Schreiben Sie den Plural von „die Entscheidung“ ohne Artikel.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "short_answer",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001014",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000007",
  "prompt": "Schreiben Sie den Plural von „die Entscheidung“ ohne Artikel.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  }
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

Der Plural lautet Entscheidungen. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-7-1

Revision: 10000000-0000-4000-8000-000000001015@1; context: context-7-1; transfer: transfer-7

Ergänzen Sie die Pluralform von „die Entscheidung“: mehrere ___.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001015",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000007",
  "prompt": "Ergänzen Sie die Pluralform von „die Entscheidung“: mehrere ___.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Der Plural lautet Entscheidungen. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Plural: die Voraussetzung

Target: 10000000-0000-4000-8000-000000000008 · B1 · plural

Recall the plural of die Voraussetzung.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: b41c6031123c342564ab4dd6db42ca9751a0673251efcd5e02a86a179e524606

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-8-0

Revision: 10000000-0000-4000-8000-000000001016@1; context: context-8-0; transfer: none

Schreiben Sie den Plural von „die Voraussetzung“ ohne Artikel.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "short_answer",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001016",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000008",
  "prompt": "Schreiben Sie den Plural von „die Voraussetzung“ ohne Artikel.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  }
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

Der Plural lautet Voraussetzungen. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-8-1

Revision: 10000000-0000-4000-8000-000000001017@1; context: context-8-1; transfer: transfer-8

Ergänzen Sie die Pluralform von „die Voraussetzung“: mehrere ___.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001017",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000008",
  "prompt": "Ergänzen Sie die Pluralform von „die Voraussetzung“: mehrere ___.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Der Plural lautet Voraussetzungen. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Plural: der Vorschlag

Target: 10000000-0000-4000-8000-000000000009 · B1 · plural

Recall the plural of der Vorschlag.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 18990bb2de213b91880cb0cfb84746d316baefd11ee7b71088309c3c7b559d31

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-9-0

Revision: 10000000-0000-4000-8000-000000001018@1; context: context-9-0; transfer: none

Schreiben Sie den Plural von „der Vorschlag“ ohne Artikel.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "short_answer",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001018",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000009",
  "prompt": "Schreiben Sie den Plural von „der Vorschlag“ ohne Artikel.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  }
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

Der Plural lautet Vorschläge. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-9-1

Revision: 10000000-0000-4000-8000-000000001019@1; context: context-9-1; transfer: transfer-9

Ergänzen Sie die Pluralform von „der Vorschlag“: mehrere ___.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001019",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000009",
  "prompt": "Ergänzen Sie die Pluralform von „der Vorschlag“: mehrere ___.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Der Plural lautet Vorschläge. Nomen werden großgeschrieben.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Artikel nach mit

Target: 10000000-0000-4000-8000-000000000010 · B1 · preposition

Select the definite singular article after mit.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 859e72c8e8a7c9850ba704ba15f42fccfbffb6dbac472026c1b85d60d728b216

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-10-0

Revision: 10000000-0000-4000-8000-000000001020@1; context: context-10-0; transfer: none

Ich spreche mit ___ Arzt. (der Arzt)

Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001020",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000010",
  "prompt": "Ich spreche mit ___ Arzt. (der Arzt)",
  "instruction": {
    "en": "Choose the definite singular article; do not contract the preposition.",
    "de": "Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

„mit“ verlangt den Dativ; der Artikel im Singular lautet hier „dem“.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-10-1

Revision: 10000000-0000-4000-8000-000000001021@1; context: context-10-1; transfer: transfer-10

Ich fahre mit ___ Zug. (der Zug)

Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001021",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000010",
  "prompt": "Ich fahre mit ___ Zug. (der Zug)",
  "instruction": {
    "en": "Choose the definite singular article; do not contract the preposition.",
    "de": "Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

„mit“ verlangt den Dativ; der Artikel im Singular lautet hier „dem“.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Artikel nach bei

Target: 10000000-0000-4000-8000-000000000011 · B1 · preposition

Select the definite singular article after bei.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: c5d6e792331c45e8fb2bc2dfea11a69718380bbc87e4af13c113bb2635770386

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-11-0

Revision: 10000000-0000-4000-8000-000000001022@1; context: context-11-0; transfer: none

Ich bin bei ___ Ärztin. (die Ärztin)

Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001022",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000011",
  "prompt": "Ich bin bei ___ Ärztin. (die Ärztin)",
  "instruction": {
    "en": "Choose the definite singular article; do not contract the preposition.",
    "de": "Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

„bei“ verlangt den Dativ; der Artikel im Singular lautet hier „der“.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-11-1

Revision: 10000000-0000-4000-8000-000000001023@1; context: context-11-1; transfer: transfer-11

Ich helfe bei ___ Vorbereitung. (die Vorbereitung)

Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001023",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000011",
  "prompt": "Ich helfe bei ___ Vorbereitung. (die Vorbereitung)",
  "instruction": {
    "en": "Choose the definite singular article; do not contract the preposition.",
    "de": "Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

„bei“ verlangt den Dativ; der Artikel im Singular lautet hier „der“.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Artikel nach für

Target: 10000000-0000-4000-8000-000000000012 · B1 · preposition

Select the definite singular article after für.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: b7ca2218d5a6bf6c6a89f1a2c5f7fe9860b0b4f38bb364134e6ebe72bbc87bb8

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-12-0

Revision: 10000000-0000-4000-8000-000000001024@1; context: context-12-0; transfer: none

Das ist für ___ Kunden. (der Kunde, Singular)

Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001024",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000012",
  "prompt": "Das ist für ___ Kunden. (der Kunde, Singular)",
  "instruction": {
    "en": "Choose the definite singular article; do not contract the preposition.",
    "de": "Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

„für“ verlangt den Akkusativ; der Artikel im Singular lautet hier „den“.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-12-1

Revision: 10000000-0000-4000-8000-000000001025@1; context: context-12-1; transfer: transfer-12

Ich kaufe das für ___ Kurs. (der Kurs)

Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001025",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000012",
  "prompt": "Ich kaufe das für ___ Kurs. (der Kurs)",
  "instruction": {
    "en": "Choose the definite singular article; do not contract the preposition.",
    "de": "Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

„für“ verlangt den Akkusativ; der Artikel im Singular lautet hier „den“.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Artikel nach ohne

Target: 10000000-0000-4000-8000-000000000013 · B1 · preposition

Select the definite singular article after ohne.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 2f6d028dff9bec7318895301b45f75d291b122cbc9ff796f33ff693255e6783c

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-13-0

Revision: 10000000-0000-4000-8000-000000001026@1; context: context-13-0; transfer: none

Ich gehe ohne ___ Tasche. (die Tasche)

Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001026",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000013",
  "prompt": "Ich gehe ohne ___ Tasche. (die Tasche)",
  "instruction": {
    "en": "Choose the definite singular article; do not contract the preposition.",
    "de": "Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

„ohne“ verlangt den Akkusativ; der Artikel im Singular lautet hier „die“.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-13-1

Revision: 10000000-0000-4000-8000-000000001027@1; context: context-13-1; transfer: transfer-13

Das geht ohne ___ Zustimmung. (die Zustimmung)

Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001027",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000013",
  "prompt": "Das geht ohne ___ Zustimmung. (die Zustimmung)",
  "instruction": {
    "en": "Choose the definite singular article; do not contract the preposition.",
    "de": "Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

„ohne“ verlangt den Akkusativ; der Artikel im Singular lautet hier „die“.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Artikel nach aus

Target: 10000000-0000-4000-8000-000000000014 · B1 · preposition

Select the definite singular article after aus.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: b08c045aff12a0465bcff97006f71301d38553eaa5b1497c70eb65eafc70449d

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-14-0

Revision: 10000000-0000-4000-8000-000000001028@1; context: context-14-0; transfer: none

Das kommt aus ___ Büro. (das Büro)

Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001028",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000014",
  "prompt": "Das kommt aus ___ Büro. (das Büro)",
  "instruction": {
    "en": "Choose the definite singular article; do not contract the preposition.",
    "de": "Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

„aus“ verlangt den Dativ; der Artikel im Singular lautet hier „dem“.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-14-1

Revision: 10000000-0000-4000-8000-000000001029@1; context: context-14-1; transfer: transfer-14

Ich komme aus ___ Haus. (das Haus)

Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001029",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000014",
  "prompt": "Ich komme aus ___ Haus. (das Haus)",
  "instruction": {
    "en": "Choose the definite singular article; do not contract the preposition.",
    "de": "Wählen Sie den bestimmten Artikel im Singular; verschmelzen Sie ihn nicht mit der Präposition."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

„aus“ verlangt den Dativ; der Artikel im Singular lautet hier „dem“.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Adjektivendung: definite-masculine-nominative

Target: 10000000-0000-4000-8000-000000000015 · B1 · adjective

Supply the weak adjective ending for definite-masculine-nominative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 376d5404009dcbfcce4f2e0a92faa0bcae5fbd1de0df149c11783ef3769e7407

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-15-0

Revision: 10000000-0000-4000-8000-000000001030@1; context: context-15-0; transfer: none

Der neu___ Kollege kommt.

Schreiben Sie nur die fehlende Endung von „neu“.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001030",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000015",
  "prompt": "Der neu___ Kollege kommt.",
  "instruction": {
    "en": "Type only the missing ending of neu.",
    "de": "Schreiben Sie nur die fehlende Endung von „neu“."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "slots": [
    {
      "id": "ending",
      "label": "Endung"
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
        "slotId": "ending",
        "text": "e"
      }
    ]
  }
]
```

Nach dem bestimmten Artikel lautet die Endung in diesem Kasus -e.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-15-1

Revision: 10000000-0000-4000-8000-000000001031@1; context: context-15-1; transfer: transfer-15

Der neu___ Kunde wartet.

Schreiben Sie nur die fehlende Endung von „neu“.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001031",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000015",
  "prompt": "Der neu___ Kunde wartet.",
  "instruction": {
    "en": "Type only the missing ending of neu.",
    "de": "Schreiben Sie nur die fehlende Endung von „neu“."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "slots": [
    {
      "id": "ending",
      "label": "Endung"
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
        "slotId": "ending",
        "text": "e"
      }
    ]
  }
]
```

Nach dem bestimmten Artikel lautet die Endung in diesem Kasus -e.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Adjektivendung: definite-feminine-nominative

Target: 10000000-0000-4000-8000-000000000016 · B1 · adjective

Supply the weak adjective ending for definite-feminine-nominative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: deaa5b9987cc994b6c360b5cd14a46ae3fb4e6ada50bf54f2f93885655dbb4ee

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-16-0

Revision: 10000000-0000-4000-8000-000000001032@1; context: context-16-0; transfer: none

Die gut___ Nachricht freut mich.

Schreiben Sie nur die fehlende Endung von „gut“.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001032",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000016",
  "prompt": "Die gut___ Nachricht freut mich.",
  "instruction": {
    "en": "Type only the missing ending of gut.",
    "de": "Schreiben Sie nur die fehlende Endung von „gut“."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "slots": [
    {
      "id": "ending",
      "label": "Endung"
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
        "slotId": "ending",
        "text": "e"
      }
    ]
  }
]
```

Nach dem bestimmten Artikel lautet die Endung in diesem Kasus -e.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-16-1

Revision: 10000000-0000-4000-8000-000000001033@1; context: context-16-1; transfer: transfer-16

Die gut___ Idee hilft uns.

Schreiben Sie nur die fehlende Endung von „gut“.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001033",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000016",
  "prompt": "Die gut___ Idee hilft uns.",
  "instruction": {
    "en": "Type only the missing ending of gut.",
    "de": "Schreiben Sie nur die fehlende Endung von „gut“."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "slots": [
    {
      "id": "ending",
      "label": "Endung"
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
        "slotId": "ending",
        "text": "e"
      }
    ]
  }
]
```

Nach dem bestimmten Artikel lautet die Endung in diesem Kasus -e.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Adjektivendung: definite-neuter-nominative

Target: 10000000-0000-4000-8000-000000000017 · B1 · adjective

Supply the weak adjective ending for definite-neuter-nominative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: de88e26a9911690ed151783ed1c686daf087fc99020b3dd0cafc954427914531

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-17-0

Revision: 10000000-0000-4000-8000-000000001034@1; context: context-17-0; transfer: none

Das klein___ Büro ist frei.

Schreiben Sie nur die fehlende Endung von „klein“.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001034",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000017",
  "prompt": "Das klein___ Büro ist frei.",
  "instruction": {
    "en": "Type only the missing ending of klein.",
    "de": "Schreiben Sie nur die fehlende Endung von „klein“."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "slots": [
    {
      "id": "ending",
      "label": "Endung"
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
        "slotId": "ending",
        "text": "e"
      }
    ]
  }
]
```

Nach dem bestimmten Artikel lautet die Endung in diesem Kasus -e.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-17-1

Revision: 10000000-0000-4000-8000-000000001035@1; context: context-17-1; transfer: transfer-17

Das klein___ Zimmer ist hell.

Schreiben Sie nur die fehlende Endung von „klein“.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001035",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000017",
  "prompt": "Das klein___ Zimmer ist hell.",
  "instruction": {
    "en": "Type only the missing ending of klein.",
    "de": "Schreiben Sie nur die fehlende Endung von „klein“."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "slots": [
    {
      "id": "ending",
      "label": "Endung"
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
        "slotId": "ending",
        "text": "e"
      }
    ]
  }
]
```

Nach dem bestimmten Artikel lautet die Endung in diesem Kasus -e.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Adjektivendung: definite-masculine-accusative

Target: 10000000-0000-4000-8000-000000000018 · B1 · adjective

Supply the weak adjective ending for definite-masculine-accusative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 2ee03cac5836b4b4a339cacd1c43e1285c40b297baf53890bb722bc571418cbd

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-18-0

Revision: 10000000-0000-4000-8000-000000001036@1; context: context-18-0; transfer: none

Ich sehe den neu___ Kollegen.

Schreiben Sie nur die fehlende Endung von „neu“.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001036",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000018",
  "prompt": "Ich sehe den neu___ Kollegen.",
  "instruction": {
    "en": "Type only the missing ending of neu.",
    "de": "Schreiben Sie nur die fehlende Endung von „neu“."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "slots": [
    {
      "id": "ending",
      "label": "Endung"
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
        "slotId": "ending",
        "text": "en"
      }
    ]
  }
]
```

Nach dem bestimmten Artikel lautet die Endung in diesem Kasus -en.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-18-1

Revision: 10000000-0000-4000-8000-000000001037@1; context: context-18-1; transfer: transfer-18

Ich begrüße den neu___ Kunden.

Schreiben Sie nur die fehlende Endung von „neu“.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001037",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000018",
  "prompt": "Ich begrüße den neu___ Kunden.",
  "instruction": {
    "en": "Type only the missing ending of neu.",
    "de": "Schreiben Sie nur die fehlende Endung von „neu“."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "slots": [
    {
      "id": "ending",
      "label": "Endung"
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
        "slotId": "ending",
        "text": "en"
      }
    ]
  }
]
```

Nach dem bestimmten Artikel lautet die Endung in diesem Kasus -en.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Adjektivendung: definite-dative

Target: 10000000-0000-4000-8000-000000000019 · B1 · adjective

Supply the weak adjective ending for definite-dative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 2fe4d01f8033b48390b776ce373ee324ee4b629b04583a883538ca815187705f

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-19-0

Revision: 10000000-0000-4000-8000-000000001038@1; context: context-19-0; transfer: none

Ich spreche mit dem nett___ Kollegen.

Schreiben Sie nur die fehlende Endung von „nett“.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001038",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000019",
  "prompt": "Ich spreche mit dem nett___ Kollegen.",
  "instruction": {
    "en": "Type only the missing ending of nett.",
    "de": "Schreiben Sie nur die fehlende Endung von „nett“."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "slots": [
    {
      "id": "ending",
      "label": "Endung"
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
        "slotId": "ending",
        "text": "en"
      }
    ]
  }
]
```

Nach dem bestimmten Artikel lautet die Endung in diesem Kasus -en.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-19-1

Revision: 10000000-0000-4000-8000-000000001039@1; context: context-19-1; transfer: transfer-19

Ich spreche mit der nett___ Ärztin.

Schreiben Sie nur die fehlende Endung von „nett“.

```json
{
  "type": "cloze",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001039",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000019",
  "prompt": "Ich spreche mit der nett___ Ärztin.",
  "instruction": {
    "en": "Type only the missing ending of nett.",
    "de": "Schreiben Sie nur die fehlende Endung von „nett“."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "slots": [
    {
      "id": "ending",
      "label": "Endung"
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
        "slotId": "ending",
        "text": "en"
      }
    ]
  }
]
```

Nach dem bestimmten Artikel lautet die Endung in diesem Kasus -en.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Präsens: fahren

Target: 10000000-0000-4000-8000-000000000020 · B1 · verb

Recall singular present forms of fahren.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 6bcffed7f9bc056aae7fef7ef383f6981a0ae253c17c3f2753e853c95932645e

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-20-0

Revision: 10000000-0000-4000-8000-000000001040@1; context: context-20-0; transfer: none

Ergänzen Sie „fahren“ im Präsens: Du ___ mit dem Bus. Er ___ mit dem Bus.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "multi_slot",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001040",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000020",
  "prompt": "Ergänzen Sie „fahren“ im Präsens: Du ___ mit dem Bus. Er ___ mit dem Bus.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Die Formen lauten: du fährst, er fährt.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-20-1

Revision: 10000000-0000-4000-8000-000000001041@1; context: context-20-1; transfer: transfer-20

Ergänzen Sie „fahren“ im Präsens: Du ___ nach Berlin. Er ___ nach Berlin.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "multi_slot",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001041",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000020",
  "prompt": "Ergänzen Sie „fahren“ im Präsens: Du ___ nach Berlin. Er ___ nach Berlin.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Die Formen lauten: du fährst, er fährt.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Präsens: lesen

Target: 10000000-0000-4000-8000-000000000021 · B1 · verb

Recall singular present forms of lesen.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: ff4dcd5cbf910433b6350842dd4ca63f764f85b0853325ab27af7134e3c832e9

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-21-0

Revision: 10000000-0000-4000-8000-000000001042@1; context: context-21-0; transfer: none

Ergänzen Sie „lesen“ im Präsens: Du ___ ein Buch. Er ___ ein Buch.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "multi_slot",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001042",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000021",
  "prompt": "Ergänzen Sie „lesen“ im Präsens: Du ___ ein Buch. Er ___ ein Buch.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Die Formen lauten: du liest, er liest.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-21-1

Revision: 10000000-0000-4000-8000-000000001043@1; context: context-21-1; transfer: transfer-21

Ergänzen Sie „lesen“ im Präsens: Du ___ die Zeitung. Er ___ die Zeitung.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "multi_slot",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001043",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000021",
  "prompt": "Ergänzen Sie „lesen“ im Präsens: Du ___ die Zeitung. Er ___ die Zeitung.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Die Formen lauten: du liest, er liest.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Präsens: geben

Target: 10000000-0000-4000-8000-000000000022 · B1 · verb

Recall singular present forms of geben.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 43227e071999bdf17d14c145e32b770d5e96befee81dc7361d47cab95ce84667

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-22-0

Revision: 10000000-0000-4000-8000-000000001044@1; context: context-22-0; transfer: none

Ergänzen Sie „geben“ im Präsens: Du ___ mir das Buch. Er ___ mir das Buch.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "multi_slot",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001044",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000022",
  "prompt": "Ergänzen Sie „geben“ im Präsens: Du ___ mir das Buch. Er ___ mir das Buch.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Die Formen lauten: du gibst, er gibt.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-22-1

Revision: 10000000-0000-4000-8000-000000001045@1; context: context-22-1; transfer: transfer-22

Ergänzen Sie „geben“ im Präsens: Du ___ ihr den Schlüssel. Er ___ ihr den Schlüssel.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "multi_slot",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001045",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000022",
  "prompt": "Ergänzen Sie „geben“ im Präsens: Du ___ ihr den Schlüssel. Er ___ ihr den Schlüssel.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Die Formen lauten: du gibst, er gibt.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Präsens: nehmen

Target: 10000000-0000-4000-8000-000000000023 · B1 · verb

Recall singular present forms of nehmen.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: b0d6df06dbab6b2d422f4813ac55822cca69399cb51745df45a2bcae9d57600e

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-23-0

Revision: 10000000-0000-4000-8000-000000001046@1; context: context-23-0; transfer: none

Ergänzen Sie „nehmen“ im Präsens: Du ___ den Bus. Er ___ den Bus.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "multi_slot",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001046",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000023",
  "prompt": "Ergänzen Sie „nehmen“ im Präsens: Du ___ den Bus. Er ___ den Bus.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Die Formen lauten: du nimmst, er nimmt.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-23-1

Revision: 10000000-0000-4000-8000-000000001047@1; context: context-23-1; transfer: transfer-23

Ergänzen Sie „nehmen“ im Präsens: Du ___ das Fahrrad. Er ___ das Fahrrad.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "multi_slot",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001047",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000023",
  "prompt": "Ergänzen Sie „nehmen“ im Präsens: Du ___ das Fahrrad. Er ___ das Fahrrad.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Die Formen lauten: du nimmst, er nimmt.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Präsens: sprechen

Target: 10000000-0000-4000-8000-000000000024 · B1 · verb

Recall singular present forms of sprechen.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 5950cf837719b973d9797582baf7a9add428646ba03bc790a5d1c6acf2244162

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-24-0

Revision: 10000000-0000-4000-8000-000000001048@1; context: context-24-0; transfer: none

Ergänzen Sie „sprechen“ im Präsens: Du ___ Deutsch. Er ___ Deutsch.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "multi_slot",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001048",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000024",
  "prompt": "Ergänzen Sie „sprechen“ im Präsens: Du ___ Deutsch. Er ___ Deutsch.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Die Formen lauten: du sprichst, er spricht.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

### v-24-1

Revision: 10000000-0000-4000-8000-000000001049@1; context: context-24-1; transfer: transfer-24

Ergänzen Sie „sprechen“ im Präsens: Du ___ mit der Ärztin. Er ___ mit der Ärztin.

Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute.

```json
{
  "type": "multi_slot",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001049",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000024",
  "prompt": "Ergänzen Sie „sprechen“ im Präsens: Du ___ mit der Ärztin. Er ___ mit der Ärztin.",
  "instruction": {
    "en": "Complete the requested form; preserve capitalization and umlauts.",
    "de": "Ergänzen Sie die verlangte Form; beachten Sie Großschreibung und Umlaute."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
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

Die Formen lauten: du sprichst, er spricht.

Ambiguity: The lemma and requested grammatical form are explicit; alternative answers require reviewer adjudication.

## Verbstellung: weil

Target: 10000000-0000-4000-8000-000000000025 · B1 · word-order

Place the finite verb last after weil.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 555148bdd5c4cebf67aaba47b331df74169358ed7f77d94c2f759e72ba805079

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-25-0

Revision: 10000000-0000-4000-8000-000000001050@1; context: context-25-0; transfer: none

lerne / weil / Deutsch / ich

Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001050",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000025",
  "prompt": "lerne / weil / Deutsch / ich",
  "instruction": {
    "en": "Build a subordinate clause: conjunction, subject, object, verb.",
    "de": "Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "tokens": [
    {
      "id": "0",
      "text": "lerne"
    },
    {
      "id": "1",
      "text": "weil"
    },
    {
      "id": "2",
      "text": "Deutsch"
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

Das finite Verb steht am Ende des Nebensatzes.

Ambiguity: An isolated subordinate clause is requested, not a complete main sentence. Instruction fixes constituent order.

### v-25-1

Revision: 10000000-0000-4000-8000-000000001051@1; context: context-25-1; transfer: transfer-25

trinkt / weil / Kaffee / sie

Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001051",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000025",
  "prompt": "trinkt / weil / Kaffee / sie",
  "instruction": {
    "en": "Build a subordinate clause: conjunction, subject, object, verb.",
    "de": "Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "tokens": [
    {
      "id": "0",
      "text": "trinkt"
    },
    {
      "id": "1",
      "text": "weil"
    },
    {
      "id": "2",
      "text": "Kaffee"
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

Das finite Verb steht am Ende des Nebensatzes.

Ambiguity: An isolated subordinate clause is requested, not a complete main sentence. Instruction fixes constituent order.

## Verbstellung: dass

Target: 10000000-0000-4000-8000-000000000026 · B1 · word-order

Place the finite verb last after dass.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 57b567688a47098f7810719ace25c00dc63a09a848651a2faa04e7ac071df0f9

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-26-0

Revision: 10000000-0000-4000-8000-000000001052@1; context: context-26-0; transfer: none

lerne / dass / Deutsch / ich

Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001052",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000026",
  "prompt": "lerne / dass / Deutsch / ich",
  "instruction": {
    "en": "Build a subordinate clause: conjunction, subject, object, verb.",
    "de": "Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "tokens": [
    {
      "id": "0",
      "text": "lerne"
    },
    {
      "id": "1",
      "text": "dass"
    },
    {
      "id": "2",
      "text": "Deutsch"
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

Das finite Verb steht am Ende des Nebensatzes.

Ambiguity: An isolated subordinate clause is requested, not a complete main sentence. Instruction fixes constituent order.

### v-26-1

Revision: 10000000-0000-4000-8000-000000001053@1; context: context-26-1; transfer: transfer-26

trinkt / dass / Kaffee / sie

Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001053",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000026",
  "prompt": "trinkt / dass / Kaffee / sie",
  "instruction": {
    "en": "Build a subordinate clause: conjunction, subject, object, verb.",
    "de": "Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "tokens": [
    {
      "id": "0",
      "text": "trinkt"
    },
    {
      "id": "1",
      "text": "dass"
    },
    {
      "id": "2",
      "text": "Kaffee"
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

Das finite Verb steht am Ende des Nebensatzes.

Ambiguity: An isolated subordinate clause is requested, not a complete main sentence. Instruction fixes constituent order.

## Verbstellung: obwohl

Target: 10000000-0000-4000-8000-000000000027 · B1 · word-order

Place the finite verb last after obwohl.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 132ee2cef4b07b0426cd471f74aedaf830d71c41ef479f0a7d4daef563a112fc

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-27-0

Revision: 10000000-0000-4000-8000-000000001054@1; context: context-27-0; transfer: none

lerne / obwohl / Deutsch / ich

Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001054",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000027",
  "prompt": "lerne / obwohl / Deutsch / ich",
  "instruction": {
    "en": "Build a subordinate clause: conjunction, subject, object, verb.",
    "de": "Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "tokens": [
    {
      "id": "0",
      "text": "lerne"
    },
    {
      "id": "1",
      "text": "obwohl"
    },
    {
      "id": "2",
      "text": "Deutsch"
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

Das finite Verb steht am Ende des Nebensatzes.

Ambiguity: An isolated subordinate clause is requested, not a complete main sentence. Instruction fixes constituent order.

### v-27-1

Revision: 10000000-0000-4000-8000-000000001055@1; context: context-27-1; transfer: transfer-27

trinkt / obwohl / Kaffee / sie

Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001055",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000027",
  "prompt": "trinkt / obwohl / Kaffee / sie",
  "instruction": {
    "en": "Build a subordinate clause: conjunction, subject, object, verb.",
    "de": "Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "tokens": [
    {
      "id": "0",
      "text": "trinkt"
    },
    {
      "id": "1",
      "text": "obwohl"
    },
    {
      "id": "2",
      "text": "Kaffee"
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

Das finite Verb steht am Ende des Nebensatzes.

Ambiguity: An isolated subordinate clause is requested, not a complete main sentence. Instruction fixes constituent order.

## Verbstellung: wenn

Target: 10000000-0000-4000-8000-000000000028 · B1 · word-order

Place the finite verb last after wenn.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: d52621fc848bb30dc7066ee3a4de523a74de37f452fbce65e60e6d054cfa2126

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-28-0

Revision: 10000000-0000-4000-8000-000000001056@1; context: context-28-0; transfer: none

lerne / wenn / Deutsch / ich

Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001056",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000028",
  "prompt": "lerne / wenn / Deutsch / ich",
  "instruction": {
    "en": "Build a subordinate clause: conjunction, subject, object, verb.",
    "de": "Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "tokens": [
    {
      "id": "0",
      "text": "lerne"
    },
    {
      "id": "1",
      "text": "wenn"
    },
    {
      "id": "2",
      "text": "Deutsch"
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

Das finite Verb steht am Ende des Nebensatzes.

Ambiguity: An isolated subordinate clause is requested, not a complete main sentence. Instruction fixes constituent order.

### v-28-1

Revision: 10000000-0000-4000-8000-000000001057@1; context: context-28-1; transfer: transfer-28

trinkt / wenn / Kaffee / sie

Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001057",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000028",
  "prompt": "trinkt / wenn / Kaffee / sie",
  "instruction": {
    "en": "Build a subordinate clause: conjunction, subject, object, verb.",
    "de": "Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "tokens": [
    {
      "id": "0",
      "text": "trinkt"
    },
    {
      "id": "1",
      "text": "wenn"
    },
    {
      "id": "2",
      "text": "Kaffee"
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

Das finite Verb steht am Ende des Nebensatzes.

Ambiguity: An isolated subordinate clause is requested, not a complete main sentence. Instruction fixes constituent order.

## Verbstellung: ob

Target: 10000000-0000-4000-8000-000000000029 · B1 · word-order

Place the finite verb last after ob.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 87bf3ce41934b527ef26ab4a0a4bae5453793ef835d486c9eee797b0a92504d9

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-29-0

Revision: 10000000-0000-4000-8000-000000001058@1; context: context-29-0; transfer: none

lerne / ob / Deutsch / ich

Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001058",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000029",
  "prompt": "lerne / ob / Deutsch / ich",
  "instruction": {
    "en": "Build a subordinate clause: conjunction, subject, object, verb.",
    "de": "Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "tokens": [
    {
      "id": "0",
      "text": "lerne"
    },
    {
      "id": "1",
      "text": "ob"
    },
    {
      "id": "2",
      "text": "Deutsch"
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

Das finite Verb steht am Ende des Nebensatzes.

Ambiguity: An isolated subordinate clause is requested, not a complete main sentence. Instruction fixes constituent order.

### v-29-1

Revision: 10000000-0000-4000-8000-000000001059@1; context: context-29-1; transfer: transfer-29

trinkt / ob / Kaffee / sie

Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001059",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000029",
  "prompt": "trinkt / ob / Kaffee / sie",
  "instruction": {
    "en": "Build a subordinate clause: conjunction, subject, object, verb.",
    "de": "Bilden Sie einen Nebensatz: Konjunktion, Subjekt, Objekt, Verb."
  },
  "hint": {
    "en": "Identify the requested grammatical form before answering.",
    "de": "Bestimmen Sie vor der Antwort die verlangte grammatische Form."
  },
  "tokens": [
    {
      "id": "0",
      "text": "trinkt"
    },
    {
      "id": "1",
      "text": "ob"
    },
    {
      "id": "2",
      "text": "Kaffee"
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

Das finite Verb steht am Ende des Nebensatzes.

Ambiguity: An isolated subordinate clause is requested, not a complete main sentence. Instruction fixes constituent order.

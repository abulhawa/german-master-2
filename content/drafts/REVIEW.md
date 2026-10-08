# Initial German-language review workbook

Generated from initial-30.json. Agent-authored, unpublished drafts. Validation is structural and grading conformance evidence, not German-language approval.

Targets: 30; variants: 60; independently approved: 0.

For each target, check objective, B1/B2 suitability, grammar, naturalness, ambiguity, all accepted alternatives, distractors, hint leakage, explanation, context variation and provenance. Record reviewer, date, approved/changes-requested status and checklist findings in the JSON. Publication remains separately authorized.

## Plural: der Antrag

Target: 10000000-0000-4000-8000-000000000000 · B1 · plural

Choose the standard plural of der Antrag.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 6017a5b43890a480fdf54f90cf85442b4da8302ba97a7ab4973316e9962fb9d7

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-0-0

Revision: 10000000-0000-4000-8000-000000001000@2; context: context-0-0; transfer: none

Was ist der Plural von „der Antrag“?

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001000",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000000",
  "prompt": "Was ist der Plural von „der Antrag“?",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Antrage",
      "text": "Antrage"
    },
    {
      "id": "Anträge",
      "text": "Anträge"
    },
    {
      "id": "Antrags",
      "text": "Antrags"
    },
    {
      "id": "Anträgen",
      "text": "Anträgen"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Anträge"
  }
]
```

Anträge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

### v-0-1

Revision: 10000000-0000-4000-8000-000000001001@2; context: context-0-1; transfer: transfer-0

Für die Förderung liegen mehrere ___ vor. (Plural von „der Antrag“)

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001001",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000000",
  "prompt": "Für die Förderung liegen mehrere ___ vor. (Plural von „der Antrag“)",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Anträgen",
      "text": "Anträgen"
    },
    {
      "id": "Antrags",
      "text": "Antrags"
    },
    {
      "id": "Anträge",
      "text": "Anträge"
    },
    {
      "id": "Antrage",
      "text": "Antrage"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Anträge"
  }
]
```

Anträge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

## Plural: die Rechnung

Target: 10000000-0000-4000-8000-000000000001 · B1 · plural

Choose the standard plural of die Rechnung.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 8402cfe9fec48896b8bee44a787076ecfb1d32e1a6bb52db03d333bd939b127a

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-1-0

Revision: 10000000-0000-4000-8000-000000001002@2; context: context-1-0; transfer: none

Was ist der Plural von „die Rechnung“?

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001002",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000001",
  "prompt": "Was ist der Plural von „die Rechnung“?",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Rechnungen",
      "text": "Rechnungen"
    },
    {
      "id": "Rechnunge",
      "text": "Rechnunge"
    },
    {
      "id": "Rechnung",
      "text": "Rechnung"
    },
    {
      "id": "Rechnungs",
      "text": "Rechnungs"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Rechnungen"
  }
]
```

Rechnungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

### v-1-1

Revision: 10000000-0000-4000-8000-000000001003@2; context: context-1-1; transfer: transfer-1

Im Ordner liegen noch drei offene ___. (Plural von „die Rechnung“)

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001003",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000001",
  "prompt": "Im Ordner liegen noch drei offene ___. (Plural von „die Rechnung“)",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Rechnungs",
      "text": "Rechnungs"
    },
    {
      "id": "Rechnung",
      "text": "Rechnung"
    },
    {
      "id": "Rechnunge",
      "text": "Rechnunge"
    },
    {
      "id": "Rechnungen",
      "text": "Rechnungen"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Rechnungen"
  }
]
```

Rechnungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

## Plural: der Termin

Target: 10000000-0000-4000-8000-000000000002 · B1 · plural

Choose the standard plural of der Termin.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 45ac18662c7cff0e3fe4e019a28fb6fe535bf924db38210864a43d0d61a9961b

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-2-0

Revision: 10000000-0000-4000-8000-000000001004@2; context: context-2-0; transfer: none

Was ist der Plural von „der Termin“?

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001004",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000002",
  "prompt": "Was ist der Plural von „der Termin“?",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Terminen",
      "text": "Terminen"
    },
    {
      "id": "Termine",
      "text": "Termine"
    },
    {
      "id": "Termins",
      "text": "Termins"
    },
    {
      "id": "Termin",
      "text": "Termin"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Termine"
  }
]
```

Termine ist der Standardplural. Der Plural bekommt die Endung -e.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

### v-2-1

Revision: 10000000-0000-4000-8000-000000001005@2; context: context-2-1; transfer: transfer-2

Nächste Woche habe ich zwei wichtige ___. (Plural von „der Termin“)

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001005",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000002",
  "prompt": "Nächste Woche habe ich zwei wichtige ___. (Plural von „der Termin“)",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Termin",
      "text": "Termin"
    },
    {
      "id": "Termins",
      "text": "Termins"
    },
    {
      "id": "Terminen",
      "text": "Terminen"
    },
    {
      "id": "Termine",
      "text": "Termine"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Termine"
  }
]
```

Termine ist der Standardplural. Der Plural bekommt die Endung -e.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

## Plural: das Gespräch

Target: 10000000-0000-4000-8000-000000000003 · B1 · plural

Choose the standard plural of das Gespräch.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 7a9123328dd810847fae7be4691134c04730305d139783fc39248d804cf5a6ad

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-3-0

Revision: 10000000-0000-4000-8000-000000001006@2; context: context-3-0; transfer: none

Was ist der Plural von „das Gespräch“?

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001006",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000003",
  "prompt": "Was ist der Plural von „das Gespräch“?",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Gesprächs",
      "text": "Gesprächs"
    },
    {
      "id": "Gesprächen",
      "text": "Gesprächen"
    },
    {
      "id": "Gespräche",
      "text": "Gespräche"
    },
    {
      "id": "Gespräch",
      "text": "Gespräch"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Gespräche"
  }
]
```

Gespräche ist der Standardplural. Der Plural bekommt -e; der Stammvokal bleibt ä.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

### v-3-1

Revision: 10000000-0000-4000-8000-000000001007@2; context: context-3-1; transfer: transfer-3

Heute stehen noch zwei schwierige ___ an. (Plural von „das Gespräch“)

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001007",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000003",
  "prompt": "Heute stehen noch zwei schwierige ___ an. (Plural von „das Gespräch“)",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Gespräche",
      "text": "Gespräche"
    },
    {
      "id": "Gesprächs",
      "text": "Gesprächs"
    },
    {
      "id": "Gespräch",
      "text": "Gespräch"
    },
    {
      "id": "Gesprächen",
      "text": "Gesprächen"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Gespräche"
  }
]
```

Gespräche ist der Standardplural. Der Plural bekommt -e; der Stammvokal bleibt ä.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

## Plural: der Vertrag

Target: 10000000-0000-4000-8000-000000000004 · B1 · plural

Choose the standard plural of der Vertrag.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 1c8075fc0090a8fa85c288c84735aac278db2ac38df77660c167526aa45699c1

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-4-0

Revision: 10000000-0000-4000-8000-000000001008@2; context: context-4-0; transfer: none

Was ist der Plural von „der Vertrag“?

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001008",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000004",
  "prompt": "Was ist der Plural von „der Vertrag“?",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Verträgen",
      "text": "Verträgen"
    },
    {
      "id": "Vertrage",
      "text": "Vertrage"
    },
    {
      "id": "Verträge",
      "text": "Verträge"
    },
    {
      "id": "Vertrags",
      "text": "Vertrags"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Verträge"
  }
]
```

Verträge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

### v-4-1

Revision: 10000000-0000-4000-8000-000000001009@2; context: context-4-1; transfer: transfer-4

Die Firma hat mehrere neue ___ abgeschlossen. (Plural von „der Vertrag“)

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001009",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000004",
  "prompt": "Die Firma hat mehrere neue ___ abgeschlossen. (Plural von „der Vertrag“)",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Vertrags",
      "text": "Vertrags"
    },
    {
      "id": "Verträge",
      "text": "Verträge"
    },
    {
      "id": "Verträgen",
      "text": "Verträgen"
    },
    {
      "id": "Vertrage",
      "text": "Vertrage"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Verträge"
  }
]
```

Verträge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

## Plural: die Erfahrung

Target: 10000000-0000-4000-8000-000000000005 · B1 · plural

Choose the standard plural of die Erfahrung.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: ad3143936ca9e3d120c46397a98b52129697521af208320a3dc38c586ca13652

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-5-0

Revision: 10000000-0000-4000-8000-000000001010@2; context: context-5-0; transfer: none

Was ist der Plural von „die Erfahrung“?

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001010",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000005",
  "prompt": "Was ist der Plural von „die Erfahrung“?",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Erfahrung",
      "text": "Erfahrung"
    },
    {
      "id": "Erfahrunge",
      "text": "Erfahrunge"
    },
    {
      "id": "Erfahrungs",
      "text": "Erfahrungs"
    },
    {
      "id": "Erfahrungen",
      "text": "Erfahrungen"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Erfahrungen"
  }
]
```

Erfahrungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

### v-5-1

Revision: 10000000-0000-4000-8000-000000001011@2; context: context-5-1; transfer: transfer-5

Im Lebenslauf beschreibt sie ihre beruflichen ___. (Plural von „die Erfahrung“)

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001011",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000005",
  "prompt": "Im Lebenslauf beschreibt sie ihre beruflichen ___. (Plural von „die Erfahrung“)",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Erfahrungen",
      "text": "Erfahrungen"
    },
    {
      "id": "Erfahrung",
      "text": "Erfahrung"
    },
    {
      "id": "Erfahrungs",
      "text": "Erfahrungs"
    },
    {
      "id": "Erfahrunge",
      "text": "Erfahrunge"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Erfahrungen"
  }
]
```

Erfahrungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

## Plural: das Angebot

Target: 10000000-0000-4000-8000-000000000006 · B1 · plural

Choose the standard plural of das Angebot.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: ae4660eba5ce8eb51d4da0929cfa1e6e2890f7c025ada771238d600281440aba

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-6-0

Revision: 10000000-0000-4000-8000-000000001012@2; context: context-6-0; transfer: none

Was ist der Plural von „das Angebot“?

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001012",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000006",
  "prompt": "Was ist der Plural von „das Angebot“?",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Angebots",
      "text": "Angebots"
    },
    {
      "id": "Angebot",
      "text": "Angebot"
    },
    {
      "id": "Angebote",
      "text": "Angebote"
    },
    {
      "id": "Angeboten",
      "text": "Angeboten"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Angebote"
  }
]
```

Angebote ist der Standardplural. Der Plural bekommt die Endung -e.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

### v-6-1

Revision: 10000000-0000-4000-8000-000000001013@2; context: context-6-1; transfer: transfer-6

Wir vergleichen drei verschiedene ___. (Plural von „das Angebot“)

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001013",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000006",
  "prompt": "Wir vergleichen drei verschiedene ___. (Plural von „das Angebot“)",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Angeboten",
      "text": "Angeboten"
    },
    {
      "id": "Angebote",
      "text": "Angebote"
    },
    {
      "id": "Angebot",
      "text": "Angebot"
    },
    {
      "id": "Angebots",
      "text": "Angebots"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Angebote"
  }
]
```

Angebote ist der Standardplural. Der Plural bekommt die Endung -e.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

## Plural: die Entscheidung

Target: 10000000-0000-4000-8000-000000000007 · B1 · plural

Choose the standard plural of die Entscheidung.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 1b21224058808b2658500466278b20769ec3bc6f2495edac85a07da01506fb1a

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-7-0

Revision: 10000000-0000-4000-8000-000000001014@2; context: context-7-0; transfer: none

Was ist der Plural von „die Entscheidung“?

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001014",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000007",
  "prompt": "Was ist der Plural von „die Entscheidung“?",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Entscheidung",
      "text": "Entscheidung"
    },
    {
      "id": "Entscheidungs",
      "text": "Entscheidungs"
    },
    {
      "id": "Entscheidungen",
      "text": "Entscheidungen"
    },
    {
      "id": "Entscheidunge",
      "text": "Entscheidunge"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Entscheidungen"
  }
]
```

Entscheidungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

### v-7-1

Revision: 10000000-0000-4000-8000-000000001015@2; context: context-7-1; transfer: transfer-7

Das Team muss heute mehrere wichtige ___ treffen. (Plural von „die Entscheidung“)

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001015",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000007",
  "prompt": "Das Team muss heute mehrere wichtige ___ treffen. (Plural von „die Entscheidung“)",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Entscheidungen",
      "text": "Entscheidungen"
    },
    {
      "id": "Entscheidunge",
      "text": "Entscheidunge"
    },
    {
      "id": "Entscheidungs",
      "text": "Entscheidungs"
    },
    {
      "id": "Entscheidung",
      "text": "Entscheidung"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Entscheidungen"
  }
]
```

Entscheidungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

## Plural: die Voraussetzung

Target: 10000000-0000-4000-8000-000000000008 · B1 · plural

Choose the standard plural of die Voraussetzung.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 61700bb828d16a89c57592dc3d1ed71a683b05fc08542590b07efe9eb3e2b367

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-8-0

Revision: 10000000-0000-4000-8000-000000001016@2; context: context-8-0; transfer: none

Was ist der Plural von „die Voraussetzung“?

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001016",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000008",
  "prompt": "Was ist der Plural von „die Voraussetzung“?",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Voraussetzunge",
      "text": "Voraussetzunge"
    },
    {
      "id": "Voraussetzung",
      "text": "Voraussetzung"
    },
    {
      "id": "Voraussetzungs",
      "text": "Voraussetzungs"
    },
    {
      "id": "Voraussetzungen",
      "text": "Voraussetzungen"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Voraussetzungen"
  }
]
```

Voraussetzungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

### v-8-1

Revision: 10000000-0000-4000-8000-000000001017@2; context: context-8-1; transfer: transfer-8

Für die Stelle musst du mehrere ___ erfüllen. (Plural von „die Voraussetzung“)

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001017",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000008",
  "prompt": "Für die Stelle musst du mehrere ___ erfüllen. (Plural von „die Voraussetzung“)",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Voraussetzungs",
      "text": "Voraussetzungs"
    },
    {
      "id": "Voraussetzungen",
      "text": "Voraussetzungen"
    },
    {
      "id": "Voraussetzung",
      "text": "Voraussetzung"
    },
    {
      "id": "Voraussetzunge",
      "text": "Voraussetzunge"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Voraussetzungen"
  }
]
```

Voraussetzungen ist der Standardplural. Der Plural bekommt die Endung -en.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

## Plural: der Vorschlag

Target: 10000000-0000-4000-8000-000000000009 · B1 · plural

Choose the standard plural of der Vorschlag.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 4aca1333c8f87a7003fc78d9e0f83ab94469502db772f8adea8aeb077353563f

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-9-0

Revision: 10000000-0000-4000-8000-000000001018@2; context: context-9-0; transfer: none

Was ist der Plural von „der Vorschlag“?

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001018",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000009",
  "prompt": "Was ist der Plural von „der Vorschlag“?",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Vorschlage",
      "text": "Vorschlage"
    },
    {
      "id": "Vorschläge",
      "text": "Vorschläge"
    },
    {
      "id": "Vorschlägen",
      "text": "Vorschlägen"
    },
    {
      "id": "Vorschlags",
      "text": "Vorschlags"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Vorschläge"
  }
]
```

Vorschläge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

### v-9-1

Revision: 10000000-0000-4000-8000-000000001019@2; context: context-9-1; transfer: transfer-9

Im Meeting wurden drei konkrete ___ gemacht. (Plural von „der Vorschlag“)

Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001019",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000009",
  "prompt": "Im Meeting wurden drei konkrete ___ gemacht. (Plural von „der Vorschlag“)",
  "instruction": {
    "en": "Choose the standard plural form. Watch the ending and any umlaut.",
    "de": "Wähle die Standardpluralform. Achte auf die Endung und einen möglichen Umlaut."
  },
  "hint": {
    "en": "Think about the plural ending and whether the stem vowel changes.",
    "de": "Überlege, welche Pluralendung das Nomen bekommt und ob sich der Stammvokal ändert."
  },
  "options": [
    {
      "id": "Vorschläge",
      "text": "Vorschläge"
    },
    {
      "id": "Vorschlags",
      "text": "Vorschlags"
    },
    {
      "id": "Vorschlägen",
      "text": "Vorschlägen"
    },
    {
      "id": "Vorschlage",
      "text": "Vorschlage"
    }
  ]
}
```

Accepted answers (editorial only):

```json
[
  {
    "type": "choice",
    "optionId": "Vorschläge"
  }
]
```

Vorschläge ist der Standardplural. Der Plural bekommt -e; dabei wird a zu ä.

Ambiguity: The singular lemma and requested plural are explicit. Distractors model common learner errors such as a missing umlaut, a wrong plural ending, a dative-plural form, or an -s overgeneralization. This revision measures recognition rather than unrestricted written production.

## Dativartikel nach „mit“

Target: 10000000-0000-4000-8000-000000000010 · B1 · preposition

Choose the definite singular article after mit, using the dative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 7c4c1fc924f5b49df585b0ebba26a54e90ed56b4c00ae708f9044db4c507d18c

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-10-0

Revision: 10000000-0000-4000-8000-000000001020@1; context: context-10-0; transfer: none

Ich bespreche die Ergebnisse mit ___ Arzt. (der Arzt)

Wähle nur den bestimmten Artikel im Singular, der nach „mit“ zum Nomen passt.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001020",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000010",
  "prompt": "Ich bespreche die Ergebnisse mit ___ Arzt. (der Arzt)",
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “mit”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „mit“ zum Nomen passt."
  },
  "hint": {
    "en": "mit always takes the dative. Match the article to the noun’s gender.",
    "de": "„mit“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an."
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

„mit“ verlangt den Dativ. „der Arzt“ ist maskulin; im Dativ Singular lautet der bestimmte Artikel „dem“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### v-10-1

Revision: 10000000-0000-4000-8000-000000001021@1; context: context-10-1; transfer: transfer-10

Morgen fahre ich mit ___ Zug nach Hamburg. (der Zug)

Wähle nur den bestimmten Artikel im Singular, der nach „mit“ zum Nomen passt.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001021",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000010",
  "prompt": "Morgen fahre ich mit ___ Zug nach Hamburg. (der Zug)",
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “mit”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „mit“ zum Nomen passt."
  },
  "hint": {
    "en": "mit always takes the dative. Match the article to the noun’s gender.",
    "de": "„mit“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an."
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

„mit“ verlangt den Dativ. „der Zug“ ist maskulin; im Dativ Singular lautet der bestimmte Artikel „dem“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

## Dativartikel nach „bei“

Target: 10000000-0000-4000-8000-000000000011 · B1 · preposition

Choose the definite singular article after bei, using the dative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 05b8deded4c1004208e3cca0d44f6a7be54f860104d5d0ec5368726c2b11f5a7

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-11-0

Revision: 10000000-0000-4000-8000-000000001022@1; context: context-11-0; transfer: none

Wegen der Schmerzen bin ich heute bei ___ Ärztin. (die Ärztin)

Wähle nur den bestimmten Artikel im Singular, der nach „bei“ zum Nomen passt.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001022",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000011",
  "prompt": "Wegen der Schmerzen bin ich heute bei ___ Ärztin. (die Ärztin)",
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “bei”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „bei“ zum Nomen passt."
  },
  "hint": {
    "en": "bei always takes the dative. Match the article to the noun’s gender.",
    "de": "„bei“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an."
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

„bei“ verlangt den Dativ. „die Ärztin“ ist feminin; im Dativ Singular lautet der bestimmte Artikel „der“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### v-11-1

Revision: 10000000-0000-4000-8000-000000001023@1; context: context-11-1; transfer: transfer-11

Wir brauchen Hilfe bei ___ Vorbereitung der Präsentation. (die Vorbereitung)

Wähle nur den bestimmten Artikel im Singular, der nach „bei“ zum Nomen passt.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001023",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000011",
  "prompt": "Wir brauchen Hilfe bei ___ Vorbereitung der Präsentation. (die Vorbereitung)",
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “bei”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „bei“ zum Nomen passt."
  },
  "hint": {
    "en": "bei always takes the dative. Match the article to the noun’s gender.",
    "de": "„bei“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an."
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

„bei“ verlangt den Dativ. „die Vorbereitung“ ist feminin; im Dativ Singular lautet der bestimmte Artikel „der“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

## Akkusativartikel nach „für“

Target: 10000000-0000-4000-8000-000000000012 · B1 · preposition

Choose the definite singular article after für, using the accusative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: d0818e9e9d5ec193a64d73552165a6d2aa179256bf95e95ab8cdcaf16253b614

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-12-0

Revision: 10000000-0000-4000-8000-000000001024@1; context: context-12-0; transfer: none

Dieses Formular ist für ___ Kunden am Schalter. (der Kunde, Singular)

Wähle nur den bestimmten Artikel im Singular, der nach „für“ zum Nomen passt.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001024",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000012",
  "prompt": "Dieses Formular ist für ___ Kunden am Schalter. (der Kunde, Singular)",
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “für”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „für“ zum Nomen passt."
  },
  "hint": {
    "en": "für always takes the accusative. Match the article to the noun’s gender.",
    "de": "„für“ verlangt immer den Akkusativ. Passe den Artikel an das Genus des Nomens an."
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

„für“ verlangt den Akkusativ. „der Kunde“ ist maskulin; im Akkusativ Singular lautet der bestimmte Artikel „den“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### v-12-1

Revision: 10000000-0000-4000-8000-000000001025@1; context: context-12-1; transfer: transfer-12

Ich buche den Raum für ___ Kurs am Abend. (der Kurs)

Wähle nur den bestimmten Artikel im Singular, der nach „für“ zum Nomen passt.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001025",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000012",
  "prompt": "Ich buche den Raum für ___ Kurs am Abend. (der Kurs)",
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “für”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „für“ zum Nomen passt."
  },
  "hint": {
    "en": "für always takes the accusative. Match the article to the noun’s gender.",
    "de": "„für“ verlangt immer den Akkusativ. Passe den Artikel an das Genus des Nomens an."
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

„für“ verlangt den Akkusativ. „der Kurs“ ist maskulin; im Akkusativ Singular lautet der bestimmte Artikel „den“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

## Akkusativartikel nach „ohne“

Target: 10000000-0000-4000-8000-000000000013 · B1 · preposition

Choose the definite singular article after ohne, using the accusative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 7b4c56ccd06c51583c094041cf4127063aaacc0602b264dafbcfd1e855cdf7b9

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-13-0

Revision: 10000000-0000-4000-8000-000000001026@1; context: context-13-0; transfer: none

Sie geht nie ohne ___ Tasche aus dem Haus. (die Tasche)

Wähle nur den bestimmten Artikel im Singular, der nach „ohne“ zum Nomen passt.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001026",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000013",
  "prompt": "Sie geht nie ohne ___ Tasche aus dem Haus. (die Tasche)",
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “ohne”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „ohne“ zum Nomen passt."
  },
  "hint": {
    "en": "ohne always takes the accusative. Match the article to the noun’s gender.",
    "de": "„ohne“ verlangt immer den Akkusativ. Passe den Artikel an das Genus des Nomens an."
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

„ohne“ verlangt den Akkusativ. „die Tasche“ ist feminin; im Akkusativ Singular bleibt der bestimmte Artikel „die“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### v-13-1

Revision: 10000000-0000-4000-8000-000000001027@1; context: context-13-1; transfer: transfer-13

Wir dürfen das nicht ohne ___ Zustimmung veröffentlichen. (die Zustimmung)

Wähle nur den bestimmten Artikel im Singular, der nach „ohne“ zum Nomen passt.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001027",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000013",
  "prompt": "Wir dürfen das nicht ohne ___ Zustimmung veröffentlichen. (die Zustimmung)",
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “ohne”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „ohne“ zum Nomen passt."
  },
  "hint": {
    "en": "ohne always takes the accusative. Match the article to the noun’s gender.",
    "de": "„ohne“ verlangt immer den Akkusativ. Passe den Artikel an das Genus des Nomens an."
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

„ohne“ verlangt den Akkusativ. „die Zustimmung“ ist feminin; im Akkusativ Singular bleibt der bestimmte Artikel „die“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

## Dativartikel nach „aus“

Target: 10000000-0000-4000-8000-000000000014 · B1 · preposition

Choose the definite singular article after aus, using the dative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 5e99de0a26a422c7fb0b50551fa8c7f219bfe526241154f316939540a2e6086d

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-14-0

Revision: 10000000-0000-4000-8000-000000001028@1; context: context-14-0; transfer: none

Die Unterlagen kommen aus ___ Büro im Erdgeschoss. (das Büro)

Wähle nur den bestimmten Artikel im Singular, der nach „aus“ zum Nomen passt.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001028",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000014",
  "prompt": "Die Unterlagen kommen aus ___ Büro im Erdgeschoss. (das Büro)",
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “aus”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „aus“ zum Nomen passt."
  },
  "hint": {
    "en": "aus always takes the dative. Match the article to the noun’s gender.",
    "de": "„aus“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an."
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

„aus“ verlangt den Dativ. „das Büro“ ist neutral; im Dativ Singular lautet der bestimmte Artikel „dem“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

### v-14-1

Revision: 10000000-0000-4000-8000-000000001029@1; context: context-14-1; transfer: transfer-14

Wir hören Musik aus ___ Haus nebenan. (das Haus)

Wähle nur den bestimmten Artikel im Singular, der nach „aus“ zum Nomen passt.

```json
{
  "type": "choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001029",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000014",
  "prompt": "Wir hören Musik aus ___ Haus nebenan. (das Haus)",
  "instruction": {
    "en": "Choose only the definite singular article that fits the noun after “aus”.",
    "de": "Wähle nur den bestimmten Artikel im Singular, der nach „aus“ zum Nomen passt."
  },
  "hint": {
    "en": "aus always takes the dative. Match the article to the noun’s gender.",
    "de": "„aus“ verlangt immer den Dativ. Passe den Artikel an das Genus des Nomens an."
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

„aus“ verlangt den Dativ. „das Haus“ ist neutral; im Dativ Singular lautet der bestimmte Artikel „dem“.

Ambiguity: The prompt names the noun and asks specifically for a separate definite singular article, so contractions and indefinite articles are outside the task.

## Adjektivendung: Maskulinum im Nominativ nach „der“

Target: 10000000-0000-4000-8000-000000000015 · B1 · adjective

Choose the weak adjective ending after der in masculine nominative.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 566fca9b915aac35bef3380e9e29236f3153a901126ef3d9f5221515fa45ab10

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-15-0

Revision: 10000000-0000-4000-8000-000000001030@2; context: context-15-0; transfer: none

Der neu___ Kollege beginnt heute.

Wähle die fehlende Adjektivendung.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001030",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000015",
  "prompt": "Der neu___ Kollege beginnt heute.",
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "hint": {
    "en": "The definite article already marks masculine nominative, so the adjective uses the weak pattern.",
    "de": "Der bestimmte Artikel markiert bereits Maskulinum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination."
  },
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
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001031",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000015",
  "prompt": "Der freundlich___ Kunde wartet am Empfang.",
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "hint": {
    "en": "The definite article already marks masculine nominative, so the adjective uses the weak pattern.",
    "de": "Der bestimmte Artikel markiert bereits Maskulinum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination."
  },
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

Content SHA-256 for sign-off: 6c8a276eb3e644b7b76620c1957ac8b4f5c3a6b351b634e165cb97aab4a05451

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-16-0

Revision: 10000000-0000-4000-8000-000000001032@2; context: context-16-0; transfer: none

Die neu___ Kollegin arbeitet im Vertrieb.

Wähle die fehlende Adjektivendung.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001032",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000016",
  "prompt": "Die neu___ Kollegin arbeitet im Vertrieb.",
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "hint": {
    "en": "The definite article already marks feminine nominative, so the adjective uses the weak pattern.",
    "de": "Der bestimmte Artikel markiert bereits Femininum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination."
  },
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
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001033",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000016",
  "prompt": "Die wichtig___ Frage bleibt offen.",
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "hint": {
    "en": "The definite article already marks feminine nominative, so the adjective uses the weak pattern.",
    "de": "Der bestimmte Artikel markiert bereits Femininum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination."
  },
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

Content SHA-256 for sign-off: 677ec710c57a74a8f2ebd66384624ee6b5e966a6d014e12b889db682a812a4e8

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-17-0

Revision: 10000000-0000-4000-8000-000000001034@2; context: context-17-0; transfer: none

Das klein___ Büro ist frei.

Wähle die fehlende Adjektivendung.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001034",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000017",
  "prompt": "Das klein___ Büro ist frei.",
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "hint": {
    "en": "The definite article already marks neuter nominative, so the adjective uses the weak pattern.",
    "de": "Der bestimmte Artikel markiert bereits Neutrum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination."
  },
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
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001035",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000017",
  "prompt": "Das neu___ Gerät funktioniert gut.",
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "hint": {
    "en": "The definite article already marks neuter nominative, so the adjective uses the weak pattern.",
    "de": "Der bestimmte Artikel markiert bereits Neutrum und Nominativ; das Adjektiv folgt deshalb der schwachen Deklination."
  },
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

Content SHA-256 for sign-off: 8bdeaf0138c9c856712853f5c733998efa428490808058621ac21885fbfc2ad6

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-18-0

Revision: 10000000-0000-4000-8000-000000001036@2; context: context-18-0; transfer: none

Ich sehe den neu___ Kollegen.

Wähle die fehlende Adjektivendung.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001036",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000018",
  "prompt": "Ich sehe den neu___ Kollegen.",
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "hint": {
    "en": "After a definite article in masculine accusative, use the weak adjective pattern.",
    "de": "Nach einem bestimmten Artikel im maskulinen Akkusativ folgt das Adjektiv der schwachen Deklination."
  },
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
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001037",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000018",
  "prompt": "Wir begrüßen den wichtig___ Kunden.",
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "hint": {
    "en": "After a definite article in masculine accusative, use the weak adjective pattern.",
    "de": "Nach einem bestimmten Artikel im maskulinen Akkusativ folgt das Adjektiv der schwachen Deklination."
  },
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

Content SHA-256 for sign-off: 2827190edba3bc4ab911dbda6171ad8e2f1b779bdec88787dc47d2cf9795bb86

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-19-0

Revision: 10000000-0000-4000-8000-000000001038@2; context: context-19-0; transfer: none

Ich spreche mit dem nett___ Kollegen.

Wähle die fehlende Adjektivendung.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001038",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000019",
  "prompt": "Ich spreche mit dem nett___ Kollegen.",
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "hint": {
    "en": "After a definite article in the dative, weak adjectives use the same ending across these genders.",
    "de": "Nach einem bestimmten Artikel im Dativ haben schwach deklinierte Adjektive in diesen Genera dieselbe Endung."
  },
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
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001039",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000019",
  "prompt": "Sie arbeitet mit der erfahren___ Ärztin.",
  "instruction": {
    "en": "Choose the missing adjective ending.",
    "de": "Wähle die fehlende Adjektivendung."
  },
  "hint": {
    "en": "After a definite article in the dative, weak adjectives use the same ending across these genders.",
    "de": "Nach einem bestimmten Artikel im Dativ haben schwach deklinierte Adjektive in diesen Genera dieselbe Endung."
  },
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

Choose the second- and third-person singular present forms of fahren.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 7f40f83d39c66f71c4488318f198492895eb47f15f95a5bef160383bef445035

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-20-0

Revision: 10000000-0000-4000-8000-000000001040@2; context: context-20-0; transfer: none

Du ___ jeden Morgen mit dem Bus. Er ___ heute mit dem Zug.

Wähle beide Präsensformen von „fahren“.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001040",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000020",
  "prompt": "Du ___ jeden Morgen mit dem Bus. Er ___ heute mit dem Zug.",
  "instruction": {
    "en": "Choose both present-tense forms of fahren.",
    "de": "Wähle beide Präsensformen von „fahren“."
  },
  "hint": {
    "en": "In the du and er forms of fahren, the stem vowel a changes to ä.",
    "de": "Bei „fahren“ wird in den Formen mit „du“ und „er“ der Stammvokal a zu ä."
  },
  "slots": [
    {
      "id": "du",
      "label": "Du ___",
      "options": [
        {
          "id": "fährt",
          "text": "fährt"
        },
        {
          "id": "fährst",
          "text": "fährst"
        },
        {
          "id": "fahren",
          "text": "fahren"
        },
        {
          "id": "fahre",
          "text": "fahre"
        }
      ]
    },
    {
      "id": "er",
      "label": "Er ___",
      "options": [
        {
          "id": "fahre",
          "text": "fahre"
        },
        {
          "id": "fahren",
          "text": "fahren"
        },
        {
          "id": "fährt",
          "text": "fährt"
        },
        {
          "id": "fährst",
          "text": "fährst"
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
        "slotId": "du",
        "optionId": "fährst"
      },
      {
        "slotId": "er",
        "optionId": "fährt"
      }
    ]
  }
]
```

Der Stammvokal wechselt a → ä: du fährst, er fährt.

Ambiguity: Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

### v-20-1

Revision: 10000000-0000-4000-8000-000000001041@2; context: context-20-1; transfer: transfer-20

Du ___ morgen nach Leipzig. Er ___ am Wochenende zu seinen Eltern.

Wähle beide Präsensformen von „fahren“.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001041",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000020",
  "prompt": "Du ___ morgen nach Leipzig. Er ___ am Wochenende zu seinen Eltern.",
  "instruction": {
    "en": "Choose both present-tense forms of fahren.",
    "de": "Wähle beide Präsensformen von „fahren“."
  },
  "hint": {
    "en": "In the du and er forms of fahren, the stem vowel a changes to ä.",
    "de": "Bei „fahren“ wird in den Formen mit „du“ und „er“ der Stammvokal a zu ä."
  },
  "slots": [
    {
      "id": "du",
      "label": "Du ___",
      "options": [
        {
          "id": "fährst",
          "text": "fährst"
        },
        {
          "id": "fahre",
          "text": "fahre"
        },
        {
          "id": "fährt",
          "text": "fährt"
        },
        {
          "id": "fahren",
          "text": "fahren"
        }
      ]
    },
    {
      "id": "er",
      "label": "Er ___",
      "options": [
        {
          "id": "fahren",
          "text": "fahren"
        },
        {
          "id": "fährst",
          "text": "fährst"
        },
        {
          "id": "fahre",
          "text": "fahre"
        },
        {
          "id": "fährt",
          "text": "fährt"
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
        "slotId": "du",
        "optionId": "fährst"
      },
      {
        "slotId": "er",
        "optionId": "fährt"
      }
    ]
  }
]
```

Der Stammvokal wechselt a → ä: du fährst, er fährt.

Ambiguity: Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

## Präsens: lesen (du/er)

Target: 10000000-0000-4000-8000-000000000021 · B1 · verb

Choose the second- and third-person singular present forms of lesen.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 1101b7f2be839db334e2161c1536dcacb5f447acbfd182b4e76267ce0ca0f9af

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-21-0

Revision: 10000000-0000-4000-8000-000000001042@2; context: context-21-0; transfer: none

Du ___ gerade den Bericht. Er ___ jeden Morgen die Zeitung.

Wähle beide Präsensformen von „lesen“.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001042",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000021",
  "prompt": "Du ___ gerade den Bericht. Er ___ jeden Morgen die Zeitung.",
  "instruction": {
    "en": "Choose both present-tense forms of lesen.",
    "de": "Wähle beide Präsensformen von „lesen“."
  },
  "hint": {
    "en": "In the du and er forms of lesen, e changes to ie.",
    "de": "Bei „lesen“ wird in den Formen mit „du“ und „er“ e zu ie."
  },
  "slots": [
    {
      "id": "du",
      "label": "Du ___",
      "options": [
        {
          "id": "lesen",
          "text": "lesen"
        },
        {
          "id": "liest",
          "text": "liest"
        },
        {
          "id": "lest",
          "text": "lest"
        },
        {
          "id": "lese",
          "text": "lese"
        }
      ]
    },
    {
      "id": "er",
      "label": "Er ___",
      "options": [
        {
          "id": "lese",
          "text": "lese"
        },
        {
          "id": "lest",
          "text": "lest"
        },
        {
          "id": "lesen",
          "text": "lesen"
        },
        {
          "id": "liest",
          "text": "liest"
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
        "slotId": "du",
        "optionId": "liest"
      },
      {
        "slotId": "er",
        "optionId": "liest"
      }
    ]
  }
]
```

Der Stammvokal wechselt e → ie; beide Formen lauten „liest“: du liest, er liest.

Ambiguity: Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

### v-21-1

Revision: 10000000-0000-4000-8000-000000001043@2; context: context-21-1; transfer: transfer-21

Du ___ die Nachricht noch einmal. Er ___ oft deutsche Romane.

Wähle beide Präsensformen von „lesen“.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001043",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000021",
  "prompt": "Du ___ die Nachricht noch einmal. Er ___ oft deutsche Romane.",
  "instruction": {
    "en": "Choose both present-tense forms of lesen.",
    "de": "Wähle beide Präsensformen von „lesen“."
  },
  "hint": {
    "en": "In the du and er forms of lesen, e changes to ie.",
    "de": "Bei „lesen“ wird in den Formen mit „du“ und „er“ e zu ie."
  },
  "slots": [
    {
      "id": "du",
      "label": "Du ___",
      "options": [
        {
          "id": "lest",
          "text": "lest"
        },
        {
          "id": "lesen",
          "text": "lesen"
        },
        {
          "id": "liest",
          "text": "liest"
        },
        {
          "id": "lese",
          "text": "lese"
        }
      ]
    },
    {
      "id": "er",
      "label": "Er ___",
      "options": [
        {
          "id": "liest",
          "text": "liest"
        },
        {
          "id": "lest",
          "text": "lest"
        },
        {
          "id": "lese",
          "text": "lese"
        },
        {
          "id": "lesen",
          "text": "lesen"
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
        "slotId": "du",
        "optionId": "liest"
      },
      {
        "slotId": "er",
        "optionId": "liest"
      }
    ]
  }
]
```

Der Stammvokal wechselt e → ie; beide Formen lauten „liest“: du liest, er liest.

Ambiguity: Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

## Präsens: geben (du/er)

Target: 10000000-0000-4000-8000-000000000022 · B1 · verb

Choose the second- and third-person singular present forms of geben.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: bc0c2feaf57c09f2a3c72c6527ab035d2e71daf8635f50fb39c9fd3eb7512e96

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-22-0

Revision: 10000000-0000-4000-8000-000000001044@2; context: context-22-0; transfer: none

Du ___ mir bitte den Schlüssel. Er ___ der Kollegin die Unterlagen.

Wähle beide Präsensformen von „geben“.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001044",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000022",
  "prompt": "Du ___ mir bitte den Schlüssel. Er ___ der Kollegin die Unterlagen.",
  "instruction": {
    "en": "Choose both present-tense forms of geben.",
    "de": "Wähle beide Präsensformen von „geben“."
  },
  "hint": {
    "en": "In the du and er forms of geben, e changes to i.",
    "de": "Bei „geben“ wird in den Formen mit „du“ und „er“ e zu i."
  },
  "slots": [
    {
      "id": "du",
      "label": "Du ___",
      "options": [
        {
          "id": "gebe",
          "text": "gebe"
        },
        {
          "id": "gibt",
          "text": "gibt"
        },
        {
          "id": "gibst",
          "text": "gibst"
        },
        {
          "id": "geben",
          "text": "geben"
        }
      ]
    },
    {
      "id": "er",
      "label": "Er ___",
      "options": [
        {
          "id": "geben",
          "text": "geben"
        },
        {
          "id": "gibt",
          "text": "gibt"
        },
        {
          "id": "gebe",
          "text": "gebe"
        },
        {
          "id": "gibst",
          "text": "gibst"
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
        "slotId": "du",
        "optionId": "gibst"
      },
      {
        "slotId": "er",
        "optionId": "gibt"
      }
    ]
  }
]
```

Der Stammvokal wechselt e → i: du gibst, er gibt.

Ambiguity: Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

### v-22-1

Revision: 10000000-0000-4000-8000-000000001045@2; context: context-22-1; transfer: transfer-22

Du ___ dem Kind Wasser. Er ___ uns eine klare Antwort.

Wähle beide Präsensformen von „geben“.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001045",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000022",
  "prompt": "Du ___ dem Kind Wasser. Er ___ uns eine klare Antwort.",
  "instruction": {
    "en": "Choose both present-tense forms of geben.",
    "de": "Wähle beide Präsensformen von „geben“."
  },
  "hint": {
    "en": "In the du and er forms of geben, e changes to i.",
    "de": "Bei „geben“ wird in den Formen mit „du“ und „er“ e zu i."
  },
  "slots": [
    {
      "id": "du",
      "label": "Du ___",
      "options": [
        {
          "id": "gibt",
          "text": "gibt"
        },
        {
          "id": "gebe",
          "text": "gebe"
        },
        {
          "id": "geben",
          "text": "geben"
        },
        {
          "id": "gibst",
          "text": "gibst"
        }
      ]
    },
    {
      "id": "er",
      "label": "Er ___",
      "options": [
        {
          "id": "gibt",
          "text": "gibt"
        },
        {
          "id": "gibst",
          "text": "gibst"
        },
        {
          "id": "geben",
          "text": "geben"
        },
        {
          "id": "gebe",
          "text": "gebe"
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
        "slotId": "du",
        "optionId": "gibst"
      },
      {
        "slotId": "er",
        "optionId": "gibt"
      }
    ]
  }
]
```

Der Stammvokal wechselt e → i: du gibst, er gibt.

Ambiguity: Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

## Präsens: nehmen (du/er)

Target: 10000000-0000-4000-8000-000000000023 · B1 · verb

Choose the second- and third-person singular present forms of nehmen.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: e23fe98b36eaa3172d9163aa360a33a4f98d5ba4d6c91bae0bf81ec17ef8695e

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-23-0

Revision: 10000000-0000-4000-8000-000000001046@2; context: context-23-0; transfer: none

Du ___ morgens den Bus. Er ___ lieber das Fahrrad.

Wähle beide Präsensformen von „nehmen“.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001046",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000023",
  "prompt": "Du ___ morgens den Bus. Er ___ lieber das Fahrrad.",
  "instruction": {
    "en": "Choose both present-tense forms of nehmen.",
    "de": "Wähle beide Präsensformen von „nehmen“."
  },
  "hint": {
    "en": "The du and er forms use the irregular stem nimm-.",
    "de": "Die Formen mit „du“ und „er“ verwenden den unregelmäßigen Stamm „nimm-“."
  },
  "slots": [
    {
      "id": "du",
      "label": "Du ___",
      "options": [
        {
          "id": "nehmen",
          "text": "nehmen"
        },
        {
          "id": "nimmt",
          "text": "nimmt"
        },
        {
          "id": "nimmst",
          "text": "nimmst"
        },
        {
          "id": "nehme",
          "text": "nehme"
        }
      ]
    },
    {
      "id": "er",
      "label": "Er ___",
      "options": [
        {
          "id": "nehme",
          "text": "nehme"
        },
        {
          "id": "nimmt",
          "text": "nimmt"
        },
        {
          "id": "nimmst",
          "text": "nimmst"
        },
        {
          "id": "nehmen",
          "text": "nehmen"
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
        "slotId": "du",
        "optionId": "nimmst"
      },
      {
        "slotId": "er",
        "optionId": "nimmt"
      }
    ]
  }
]
```

Im Singular lautet der Stamm „nimm-“: du nimmst, er nimmt.

Ambiguity: Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

### v-23-1

Revision: 10000000-0000-4000-8000-000000001047@2; context: context-23-1; transfer: transfer-23

Du ___ noch einen Kaffee. Er ___ die letzte Tablette.

Wähle beide Präsensformen von „nehmen“.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001047",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000023",
  "prompt": "Du ___ noch einen Kaffee. Er ___ die letzte Tablette.",
  "instruction": {
    "en": "Choose both present-tense forms of nehmen.",
    "de": "Wähle beide Präsensformen von „nehmen“."
  },
  "hint": {
    "en": "The du and er forms use the irregular stem nimm-.",
    "de": "Die Formen mit „du“ und „er“ verwenden den unregelmäßigen Stamm „nimm-“."
  },
  "slots": [
    {
      "id": "du",
      "label": "Du ___",
      "options": [
        {
          "id": "nimmt",
          "text": "nimmt"
        },
        {
          "id": "nehmen",
          "text": "nehmen"
        },
        {
          "id": "nimmst",
          "text": "nimmst"
        },
        {
          "id": "nehme",
          "text": "nehme"
        }
      ]
    },
    {
      "id": "er",
      "label": "Er ___",
      "options": [
        {
          "id": "nimmt",
          "text": "nimmt"
        },
        {
          "id": "nehme",
          "text": "nehme"
        },
        {
          "id": "nehmen",
          "text": "nehmen"
        },
        {
          "id": "nimmst",
          "text": "nimmst"
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
        "slotId": "du",
        "optionId": "nimmst"
      },
      {
        "slotId": "er",
        "optionId": "nimmt"
      }
    ]
  }
]
```

Im Singular lautet der Stamm „nimm-“: du nimmst, er nimmt.

Ambiguity: Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

## Präsens: sprechen (du/er)

Target: 10000000-0000-4000-8000-000000000024 · B1 · verb

Choose the second- and third-person singular present forms of sprechen.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 29cdfebfe065610f81f4cda6984938d12afef22c681c30f144d6d11d425e441e

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-24-0

Revision: 10000000-0000-4000-8000-000000001048@2; context: context-24-0; transfer: none

Du ___ sehr gut Deutsch. Er ___ heute mit der Ärztin.

Wähle beide Präsensformen von „sprechen“.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001048",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000024",
  "prompt": "Du ___ sehr gut Deutsch. Er ___ heute mit der Ärztin.",
  "instruction": {
    "en": "Choose both present-tense forms of sprechen.",
    "de": "Wähle beide Präsensformen von „sprechen“."
  },
  "hint": {
    "en": "In the du and er forms of sprechen, e changes to i.",
    "de": "Bei „sprechen“ wird in den Formen mit „du“ und „er“ e zu i."
  },
  "slots": [
    {
      "id": "du",
      "label": "Du ___",
      "options": [
        {
          "id": "spreche",
          "text": "spreche"
        },
        {
          "id": "spricht",
          "text": "spricht"
        },
        {
          "id": "sprechen",
          "text": "sprechen"
        },
        {
          "id": "sprichst",
          "text": "sprichst"
        }
      ]
    },
    {
      "id": "er",
      "label": "Er ___",
      "options": [
        {
          "id": "sprechen",
          "text": "sprechen"
        },
        {
          "id": "spricht",
          "text": "spricht"
        },
        {
          "id": "sprichst",
          "text": "sprichst"
        },
        {
          "id": "spreche",
          "text": "spreche"
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
        "slotId": "du",
        "optionId": "sprichst"
      },
      {
        "slotId": "er",
        "optionId": "spricht"
      }
    ]
  }
]
```

Der Stammvokal wechselt e → i: du sprichst, er spricht.

Ambiguity: Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

### v-24-1

Revision: 10000000-0000-4000-8000-000000001049@2; context: context-24-1; transfer: transfer-24

Du ___ morgen mit deinem Chef. Er ___ oft über seine Arbeit.

Wähle beide Präsensformen von „sprechen“.

```json
{
  "type": "gap_choice",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001049",
  "revision": 2,
  "targetId": "10000000-0000-4000-8000-000000000024",
  "prompt": "Du ___ morgen mit deinem Chef. Er ___ oft über seine Arbeit.",
  "instruction": {
    "en": "Choose both present-tense forms of sprechen.",
    "de": "Wähle beide Präsensformen von „sprechen“."
  },
  "hint": {
    "en": "In the du and er forms of sprechen, e changes to i.",
    "de": "Bei „sprechen“ wird in den Formen mit „du“ und „er“ e zu i."
  },
  "slots": [
    {
      "id": "du",
      "label": "Du ___",
      "options": [
        {
          "id": "spricht",
          "text": "spricht"
        },
        {
          "id": "sprichst",
          "text": "sprichst"
        },
        {
          "id": "spreche",
          "text": "spreche"
        },
        {
          "id": "sprechen",
          "text": "sprechen"
        }
      ]
    },
    {
      "id": "er",
      "label": "Er ___",
      "options": [
        {
          "id": "spricht",
          "text": "spricht"
        },
        {
          "id": "sprechen",
          "text": "sprechen"
        },
        {
          "id": "spreche",
          "text": "spreche"
        },
        {
          "id": "sprichst",
          "text": "sprichst"
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
        "slotId": "du",
        "optionId": "sprichst"
      },
      {
        "slotId": "er",
        "optionId": "spricht"
      }
    ]
  }
]
```

Der Stammvokal wechselt e → i: du sprichst, er spricht.

Ambiguity: Both subjects and the infinitive are explicit. This low-typing revision measures recognition of the correct finite forms, not unrestricted written production.

## Nebensatz mit „weil“

Target: 10000000-0000-4000-8000-000000000025 · B1 · word-order

Place the finite verb at the end of a subordinate clause introduced by weil.

Review: pending; reviewer: pending

Content SHA-256 for sign-off: 6a831b4165c1c0c550d0bc06c0ccfb38e48c71ae1638108b26011474154bfcd7

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-25-0

Revision: 10000000-0000-4000-8000-000000001050@1; context: context-25-0; transfer: none

arbeite / weil / heute / ich

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001050",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000025",
  "prompt": "arbeite / weil / heute / ich",
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "hint": {
    "en": "After weil, the finite verb belongs at the end of the clause.",
    "de": "Nach „weil“ steht das finite Verb am Ende des Satzes."
  },
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
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001051",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000025",
  "prompt": "braucht / weil / Hilfe / sie",
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "hint": {
    "en": "After weil, the finite verb belongs at the end of the clause.",
    "de": "Nach „weil“ steht das finite Verb am Ende des Satzes."
  },
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

Content SHA-256 for sign-off: a13ed62d1c6010bc59ac1a63482e61c060f727da9f2c872347db77039832d166

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-26-0

Revision: 10000000-0000-4000-8000-000000001052@1; context: context-26-0; transfer: none

starten / dass / morgen / wir

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001052",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000026",
  "prompt": "starten / dass / morgen / wir",
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "hint": {
    "en": "After dass, the finite verb belongs at the end of the clause.",
    "de": "Nach „dass“ steht das finite Verb am Ende des Satzes."
  },
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
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001053",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000026",
  "prompt": "bestätigt / dass / den Termin / er",
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "hint": {
    "en": "After dass, the finite verb belongs at the end of the clause.",
    "de": "Nach „dass“ steht das finite Verb am Ende des Satzes."
  },
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

Content SHA-256 for sign-off: 44349f9b0efaa9fb61a1f0133bd07db119e444052c43485332a59ad7a3626044

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-27-0

Revision: 10000000-0000-4000-8000-000000001054@1; context: context-27-0; transfer: none

bin / obwohl / müde / ich

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001054",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000027",
  "prompt": "bin / obwohl / müde / ich",
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "hint": {
    "en": "After obwohl, the finite verb belongs at the end of the clause.",
    "de": "Nach „obwohl“ steht das finite Verb am Ende des Satzes."
  },
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
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001055",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000027",
  "prompt": "hat / obwohl / wenig Zeit / sie",
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "hint": {
    "en": "After obwohl, the finite verb belongs at the end of the clause.",
    "de": "Nach „obwohl“ steht das finite Verb am Ende des Satzes."
  },
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

Content SHA-256 for sign-off: d6c0468cf13bf17e0ba052e1c7ce560cd8b646d48487397c1767db3bbd195239

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-28-0

Revision: 10000000-0000-4000-8000-000000001056@1; context: context-28-0; transfer: none

habe / wenn / Zeit / ich

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001056",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000028",
  "prompt": "habe / wenn / Zeit / ich",
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "hint": {
    "en": "After wenn, the finite verb belongs at the end of the clause.",
    "de": "Nach „wenn“ steht das finite Verb am Ende des Satzes."
  },
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
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001057",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000028",
  "prompt": "kommt / wenn / pünktlich / der Zug",
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "hint": {
    "en": "After wenn, the finite verb belongs at the end of the clause.",
    "de": "Nach „wenn“ steht das finite Verb am Ende des Satzes."
  },
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

Content SHA-256 for sign-off: d3d07ba4070afcce369c77d5c32a9837be96c831c2b7446224bd2be0af6d4378

Original agent-authored examples for German Master 2.0; no imported dataset.

### v-29-0

Revision: 10000000-0000-4000-8000-000000001058@1; context: context-29-0; transfer: none

kommst / ob / morgen / du

Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb.

```json
{
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001058",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000029",
  "prompt": "kommst / ob / morgen / du",
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "hint": {
    "en": "After ob, the finite verb belongs at the end of the clause.",
    "de": "Nach „ob“ steht das finite Verb am Ende des Satzes."
  },
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
  "type": "word_order",
  "schemaVersion": 1,
  "id": "10000000-0000-4000-8000-000000001059",
  "revision": 1,
  "targetId": "10000000-0000-4000-8000-000000000029",
  "prompt": "hat / ob / genug Zeit / sie",
  "instruction": {
    "en": "Build the clause in this order: conjunction, subject, remaining phrase, finite verb.",
    "de": "Bilde den Satz in dieser Reihenfolge: Konjunktion, Subjekt, übriger Satzteil, finites Verb."
  },
  "hint": {
    "en": "After ob, the finite verb belongs at the end of the clause.",
    "de": "Nach „ob“ steht das finite Verb am Ende des Satzes."
  },
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

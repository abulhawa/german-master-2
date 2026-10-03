export const copy = {
  en: {
    title: "German Master",
    subtitle: "Shared foundation preview",
    notice:
      "Sample content under editorial review. Answers are not graded or saved.",
    language: "Interface language",
    hint: "Hint",
    answer: "Your answer",
    inspect: "Inspect answer",
    next: "Next exercise",
    reset: "Reset order",
    ready: "Answer prepared for the v2 API. Backend grading is next.",
    question: "Question",
    of: "of",
    theme: "Theme",
    system: "System",
    light: "Light",
    dark: "Dark",
    unavailable:
      "This exercise version is not supported. Please update the app.",
  },
  de: {
    title: "German Master",
    subtitle: "Vorschau der gemeinsamen Grundlage",
    notice:
      "Beispielinhalt in redaktioneller Prüfung. Antworten werden weder bewertet noch gespeichert.",
    language: "Sprache der Oberfläche",
    hint: "Hinweis",
    answer: "Ihre Antwort",
    inspect: "Antwort ansehen",
    next: "Nächste Aufgabe",
    reset: "Reihenfolge zurücksetzen",
    ready:
      "Antwort für die v2-API vorbereitet. Die Bewertung durch den Server folgt.",
    question: "Aufgabe",
    of: "von",
    theme: "Darstellung",
    system: "System",
    light: "Hell",
    dark: "Dunkel",
    unavailable:
      "Diese Aufgabenversion wird nicht unterstützt. Bitte aktualisieren Sie die App.",
  },
};
export type Locale = keyof typeof copy;

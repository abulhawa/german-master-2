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

export const backendCopy = {
  en: { ...copy.en, subtitle: "Backend session preview", notice: "Draft content under review. The local server confirms answers; this demo resets on restart and does not calculate mastery.",
    loading: "Preparing your session", retry: "Retry", submit: "Check answer", sending: "Checking…", connectionError: "Could not reach the server. Retry keeps the same submission.",
    rejected: "The server could not accept this answer. Start a new preview session.", correct: "Correct", incorrect: "Needs correction", assisted: "Assisted",
    yourAnswer: "Your answer", acceptedAnswer: "Accepted answer", next: "Continue", complete: "Session complete", summary: "All five answers were confirmed by the local server." },
  de: { ...copy.de, subtitle: "Vorschau der Serverbewertung", notice: "Entwurfsinhalt in Prüfung. Der lokale Server bestätigt Antworten; diese Demo wird beim Neustart zurückgesetzt und berechnet keinen Lernstand.",
    loading: "Ihre Übung wird vorbereitet", retry: "Erneut versuchen", submit: "Antwort prüfen", sending: "Wird geprüft…", connectionError: "Server nicht erreichbar. Beim erneuten Versuch bleibt die Abgabe gleich.",
    rejected: "Der Server konnte diese Antwort nicht annehmen. Starten Sie eine neue Vorschau.", correct: "Richtig", incorrect: "Korrektur nötig", assisted: "Mit Hilfe",
    yourAnswer: "Ihre Antwort", acceptedAnswer: "Akzeptierte Antwort", next: "Weiter", complete: "Übung abgeschlossen", summary: "Alle fünf Antworten wurden vom lokalen Server bestätigt." },
};

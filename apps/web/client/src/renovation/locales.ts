import { backendCopy } from "../foundation/locales";
export const learnerCopy = {
  en: { ...backendCopy.en, subtitle: "Local 2.0 preview", notice: "Five draft targets under review. Progress is confirmed by the local server. Restarting that server resets its data. This preview requires a connection; it is not offline practice.",
    home: "Home", progress: "Progress", start: "Start short practice", resume: "Continue practice", close: "Close practice", welcome: "Let’s find what to practise", priorities: "Your next practice", shorter: "This draft catalog supports a shorter session: 5 questions instead of the usual 15.",
    needs: "Needs practice", retention: "Retention checks", fresh: "Try something new", refresh: "Refresh confirmed progress", confirmed: "Server-confirmed progress", stale: "Last confirmed snapshot; refresh to check current progress.", empty: "No confirmed targets available yet.",
    summary: "Answers confirmed", correctCount: "Correct answers", retentionNote: "A correct answer is a first step. Retained improvement needs later unassisted checks.", pending: "Saved on this device; awaiting server confirmation.", saved: "Session saved on this device.", storageError: "Could not save practice on this device. New submissions are blocked. Restore storage access and retry.",
    damaged: "Saved preview data could not be read. It has been preserved. Restore or inspect this browser’s preview storage before continuing.", unavailable: "Could not refresh confirmed progress. Previously confirmed data stays available.", discard: "Discard this preview session", discardNote: "This removes the local session and any pending answer. Use this if the local server has reset.",
    states: { new: "New", learning: "Learning", needs_practice: "Needs practice", improving: "Improving", mastered: "Mastered" },
    due: "Review due", later: "Next review", unknown: "Practice target", checks: "Qualifying checks", explanation: "Targets become reliable through spaced, unassisted checks across different contexts.",
  },
  de: { ...backendCopy.de, subtitle: "Lokale Vorschau von 2.0", notice: "Fünf Lernziele als Entwurf in Prüfung. Der lokale Server bestätigt den Lernstand. Ein Serverneustart setzt seine Daten zurück. Diese Vorschau benötigt eine Verbindung; Offline-Üben ist noch nicht verfügbar.",
    home: "Start", progress: "Lernstand", start: "Kurze Übung starten", resume: "Übung fortsetzen", close: "Übung schließen", welcome: "Finden wir heraus, was Sie üben können", priorities: "Ihre nächste Übung", shorter: "Dieser Entwurfskatalog ermöglicht eine kürzere Übung: 5 Aufgaben statt der üblichen 15.",
    needs: "Übungsbedarf", retention: "Wiederholungen", fresh: "Etwas Neues ausprobieren", refresh: "Bestätigten Lernstand aktualisieren", confirmed: "Vom Server bestätigter Lernstand", stale: "Zuletzt bestätigter Stand; aktualisieren Sie den Lernstand.", empty: "Noch keine bestätigten Lernziele verfügbar.",
    summary: "Bestätigte Antworten", correctCount: "Richtige Antworten", retentionNote: "Eine richtige Antwort ist ein erster Schritt. Nachhaltige Verbesserung braucht spätere Prüfungen ohne Hilfe.", pending: "Auf diesem Gerät gespeichert; Serverbestätigung steht aus.", saved: "Übung auf diesem Gerät gespeichert.", storageError: "Übung konnte nicht auf diesem Gerät gespeichert werden. Neue Antworten sind blockiert. Stellen Sie den Speicherzugriff wieder her und versuchen Sie es erneut.",
    damaged: "Gespeicherte Vorschaudaten konnten nicht gelesen werden. Sie bleiben erhalten. Stellen Sie den Vorschau-Speicher dieses Browsers wieder her oder prüfen Sie ihn.", unavailable: "Bestätigter Lernstand konnte nicht aktualisiert werden. Der bisherige Stand bleibt verfügbar.", discard: "Diese Vorschauübung verwerfen", discardNote: "Entfernt die lokale Übung und jede ausstehende Antwort. Nutzen Sie dies nach einem Neustart des lokalen Servers.",
    states: { new: "Neu", learning: "Im Aufbau", needs_practice: "Übungsbedarf", improving: "In Verbesserung", mastered: "Gefestigt" },
    due: "Wiederholung fällig", later: "Nächste Wiederholung", unknown: "Lernziel", checks: "Qualifizierende Prüfungen", explanation: "Lernziele festigen sich durch zeitlich verteilte Prüfungen ohne Hilfe in verschiedenen Kontexten.",
  },
};

// Presentation metadata for the pinned unpublished fixture, without answers or learning policy.
export const targetLabels: Record<string, { en: string; de: string }> = Object.fromEntries([
  ["Plural of Beruf", "Plural von Beruf"], ["Dative after mit", "Dativ nach mit"], ["Direction with in", "Richtung mit in"],
  ["Word order after weil", "Wortstellung nach weil"], ["Present tense of arbeiten", "Präsens von arbeiten"],
].map(([en, de], i) => [`00000000-0000-4000-8000-00000000020${i}`, { en, de }]));

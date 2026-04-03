package com.germanverbmaster.android.ui.b2practice

import com.germanverbmaster.android.domain.model.B2Card
import com.germanverbmaster.android.domain.model.B2Category
import com.germanverbmaster.android.domain.model.DeklRow
import com.germanverbmaster.android.domain.model.DeklTable

object B2ContentData {

    // ─── Verben + Präpositionen (58) ─────────────────────────────────────────

    val verbenPraep: List<B2Card> = listOf(
        B2Card("vp01", B2Category.VERBEN_PRAEP, "sich bewerben", "to apply for", "um + Akk", "Ich bewerbe mich um die Stelle als Ingenieur."),
        B2Card("vp02", B2Category.VERBEN_PRAEP, "sich bewerben", "to apply at (a company)", "bei + Dat", "Ich bewerbe mich bei der Firma Bosch."),
        B2Card("vp03", B2Category.VERBEN_PRAEP, "abhängen", "to depend on", "von + Dat", "Der Erfolg hängt von der Vorbereitung ab."),
        B2Card("vp04", B2Category.VERBEN_PRAEP, "achten", "to pay attention to", "auf + Akk", "Bitte achten Sie auf die Fristen."),
        B2Card("vp05", B2Category.VERBEN_PRAEP, "sich ärgern", "to be annoyed about", "über + Akk", "Er ärgert sich über die Verspätung."),
        B2Card("vp06", B2Category.VERBEN_PRAEP, "anfangen", "to start with", "mit + Dat", "Wir fangen morgen mit dem Projekt an."),
        B2Card("vp07", B2Category.VERBEN_PRAEP, "antworten", "to reply to", "auf + Akk", "Sie antwortet auf meine E-Mail."),
        B2Card("vp08", B2Category.VERBEN_PRAEP, "arbeiten", "to work on", "an + Dat", "Wir arbeiten an einem neuen Konzept."),
        B2Card("vp09", B2Category.VERBEN_PRAEP, "aufhören", "to stop doing", "mit + Dat", "Er hat mit dem Rauchen aufgehört."),
        B2Card("vp10", B2Category.VERBEN_PRAEP, "sich auskennen", "to know one's way around", "in/mit + Dat", "Sie kennt sich in der Branche gut aus."),
        B2Card("vp11", B2Category.VERBEN_PRAEP, "sich bedanken", "to thank for", "für + Akk", "Ich bedanke mich für Ihre Unterstützung."),
        B2Card("vp12", B2Category.VERBEN_PRAEP, "sich befassen", "to deal with", "mit + Dat", "Wir befassen uns mit dem Thema Datenschutz."),
        B2Card("vp13", B2Category.VERBEN_PRAEP, "sich beklagen", "to complain about", "über + Akk", "Der Kunde beklagt sich über die Lieferzeit."),
        B2Card("vp14", B2Category.VERBEN_PRAEP, "berichten", "to report on", "über + Akk", "Bitte berichten Sie über Ihre Ergebnisse."),
        B2Card("vp15", B2Category.VERBEN_PRAEP, "sich beschäftigen", "to occupy oneself with", "mit + Dat", "Sie beschäftigt sich mit der Analyse."),
        B2Card("vp16", B2Category.VERBEN_PRAEP, "sich beschweren", "to complain to sb. about sth.", "bei + Dat / über + Akk", "Er beschwert sich beim Chef über den Kollegen."),
        B2Card("vp17", B2Category.VERBEN_PRAEP, "bestehen", "to consist of", "aus + Dat", "Das Team besteht aus zehn Mitarbeitern."),
        B2Card("vp18", B2Category.VERBEN_PRAEP, "bestehen", "to insist on", "auf + Dat", "Sie besteht auf einer schriftlichen Bestätigung."),
        B2Card("vp19", B2Category.VERBEN_PRAEP, "sich beziehen", "to refer to", "auf + Akk", "Ich beziehe mich auf unser Gespräch vom Montag."),
        B2Card("vp20", B2Category.VERBEN_PRAEP, "bitten", "to ask for", "um + Akk", "Ich bitte Sie um eine kurze Rückmeldung."),
        B2Card("vp21", B2Category.VERBEN_PRAEP, "danken", "to thank for", "für + Akk", "Ich danke Ihnen für Ihr Vertrauen."),
        B2Card("vp22", B2Category.VERBEN_PRAEP, "denken", "to think of / remember", "an + Akk", "Denken Sie bitte an die Unterlagen."),
        B2Card("vp23", B2Category.VERBEN_PRAEP, "diskutieren", "to discuss", "über + Akk", "Wir diskutieren über die neuen Regelungen."),
        B2Card("vp24", B2Category.VERBEN_PRAEP, "sich einigen", "to agree on", "auf + Akk", "Wir haben uns auf einen Termin geeinigt."),
        B2Card("vp25", B2Category.VERBEN_PRAEP, "sich entscheiden", "to decide for / against", "für + Akk / gegen + Akk", "Wir haben uns für diesen Anbieter entschieden."),
        B2Card("vp26", B2Category.VERBEN_PRAEP, "sich entschuldigen", "to apologize for", "für + Akk / bei + Dat", "Ich entschuldige mich für die Verzögerung."),
        B2Card("vp27", B2Category.VERBEN_PRAEP, "erinnern", "to remind of", "an + Akk", "Ich erinnere Sie an den Termin am Freitag."),
        B2Card("vp28", B2Category.VERBEN_PRAEP, "sich erkundigen", "to inquire about", "nach + Dat", "Ich erkundige mich nach dem Stand der Bestellung."),
        B2Card("vp29", B2Category.VERBEN_PRAEP, "fragen", "to ask about", "nach + Dat", "Er fragt nach Ihrem Namen."),
        B2Card("vp30", B2Category.VERBEN_PRAEP, "sich freuen", "to look forward to", "auf + Akk (Zukunft)", "Ich freue mich auf unser Treffen."),
        B2Card("vp31", B2Category.VERBEN_PRAEP, "sich freuen", "to be happy about", "über + Akk (Vergangenheit)", "Sie freut sich über das positive Feedback."),
        B2Card("vp32", B2Category.VERBEN_PRAEP, "gehören", "to belong to / be part of", "zu + Dat", "Das gehört zu meinen Aufgaben."),
        B2Card("vp33", B2Category.VERBEN_PRAEP, "sich gewöhnen", "to get used to", "an + Akk", "Man gewöhnt sich schnell an die Arbeitszeiten."),
        B2Card("vp34", B2Category.VERBEN_PRAEP, "handeln", "to be about", "von + Dat", "Der Bericht handelt von den Quartalszahlen."),
        B2Card("vp35", B2Category.VERBEN_PRAEP, "helfen", "to help with", "bei + Dat", "Kann ich Ihnen bei der Planung helfen?"),
        B2Card("vp36", B2Category.VERBEN_PRAEP, "hinweisen", "to point out", "auf + Akk", "Ich möchte auf einen Fehler hinweisen."),
        B2Card("vp37", B2Category.VERBEN_PRAEP, "hoffen", "to hope for", "auf + Akk", "Wir hoffen auf eine schnelle Lösung."),
        B2Card("vp38", B2Category.VERBEN_PRAEP, "sich informieren", "to find out about", "über + Akk", "Bitte informieren Sie sich über die Bedingungen."),
        B2Card("vp39", B2Category.VERBEN_PRAEP, "sich interessieren", "to be interested in", "für + Akk", "Ich interessiere mich für die ausgeschriebene Stelle."),
        B2Card("vp40", B2Category.VERBEN_PRAEP, "klagen", "to complain about", "über + Akk", "Viele Mitarbeiter klagen über Überstunden."),
        B2Card("vp41", B2Category.VERBEN_PRAEP, "sich konzentrieren", "to concentrate on", "auf + Akk", "Wir konzentrieren uns auf den Kernmarkt."),
        B2Card("vp42", B2Category.VERBEN_PRAEP, "sich kümmern", "to take care of", "um + Akk", "Ich kümmere mich um die Buchung."),
        B2Card("vp43", B2Category.VERBEN_PRAEP, "leiden", "to suffer from", "unter + Dat", "Die Qualität leidet unter dem Zeitdruck."),
        B2Card("vp44", B2Category.VERBEN_PRAEP, "nachdenken", "to think about", "über + Akk", "Wir denken über neue Strategien nach."),
        B2Card("vp45", B2Category.VERBEN_PRAEP, "profitieren", "to benefit from", "von + Dat", "Alle Abteilungen profitieren von der Lösung."),
        B2Card("vp46", B2Category.VERBEN_PRAEP, "rechnen", "to expect / count on", "mit + Dat", "Wir rechnen mit einer Lieferung nächste Woche."),
        B2Card("vp47", B2Category.VERBEN_PRAEP, "sich richten", "to be aimed at", "an + Akk", "Das Angebot richtet sich an Fachkräfte."),
        B2Card("vp48", B2Category.VERBEN_PRAEP, "sprechen", "to talk about", "über + Akk / von + Dat", "Wir sprechen über die Ergebnisse."),
        B2Card("vp49", B2Category.VERBEN_PRAEP, "sorgen", "to ensure / take care of", "für + Akk", "Bitte sorgen Sie für eine rechtzeitige Lieferung."),
        B2Card("vp50", B2Category.VERBEN_PRAEP, "teilnehmen", "to participate in", "an + Dat", "Ich nehme an der Schulung teil."),
        B2Card("vp51", B2Category.VERBEN_PRAEP, "überzeugen", "to be convinced of", "von + Dat", "Ich bin von seiner Kompetenz überzeugt."),
        B2Card("vp52", B2Category.VERBEN_PRAEP, "sich unterscheiden", "to differ from", "von + Dat", "Unser Produkt unterscheidet sich von der Konkurrenz."),
        B2Card("vp53", B2Category.VERBEN_PRAEP, "sich verlassen", "to rely on", "auf + Akk", "Auf Sie kann man sich wirklich verlassen."),
        B2Card("vp54", B2Category.VERBEN_PRAEP, "verweisen", "to refer to", "auf + Akk", "Ich verweise auf die beigefügten Unterlagen."),
        B2Card("vp55", B2Category.VERBEN_PRAEP, "verzichten", "to do without / forgo", "auf + Akk", "Wir verzichten auf unnötige Kosten."),
        B2Card("vp56", B2Category.VERBEN_PRAEP, "warten", "to wait for", "auf + Akk", "Ich warte auf Ihre Rückmeldung."),
        B2Card("vp57", B2Category.VERBEN_PRAEP, "sich wenden", "to turn to / contact", "an + Akk", "Bitte wenden Sie sich an unsere Hotline."),
        B2Card("vp58", B2Category.VERBEN_PRAEP, "zweifeln", "to doubt", "an + Dat", "Ich zweifle nicht an seiner Eignung."),
    )

    // ─── Nomen-Verb-Verbindungen (40) ────────────────────────────────────────

    val nomenVerb: List<B2Card> = listOf(
        B2Card("nv01", B2Category.NOMEN_VERB, "eine Frage stellen", "to ask a question", null, "Darf ich eine Frage stellen?"),
        B2Card("nv02", B2Category.NOMEN_VERB, "eine Entscheidung treffen", "to make a decision", null, "Wir müssen eine Entscheidung treffen."),
        B2Card("nv03", B2Category.NOMEN_VERB, "einen Termin vereinbaren", "to arrange an appointment", null, "Kann ich einen Termin vereinbaren?"),
        B2Card("nv04", B2Category.NOMEN_VERB, "einen Termin absagen", "to cancel an appointment", null, "Leider muss ich den Termin absagen."),
        B2Card("nv05", B2Category.NOMEN_VERB, "einen Termin verschieben", "to postpone an appointment", null, "Können wir den Termin verschieben?"),
        B2Card("nv06", B2Category.NOMEN_VERB, "Kontakt aufnehmen", "to get in touch", null, "Ich nehme morgen Kontakt auf."),
        B2Card("nv07", B2Category.NOMEN_VERB, "eine Lösung finden", "to find a solution", null, "Gemeinsam finden wir eine Lösung."),
        B2Card("nv08", B2Category.NOMEN_VERB, "einen Antrag stellen", "to submit an application", null, "Sie müssen einen Antrag stellen."),
        B2Card("nv09", B2Category.NOMEN_VERB, "eine Beschwerde einreichen", "to file a complaint", null, "Er hat eine Beschwerde eingereicht."),
        B2Card("nv10", B2Category.NOMEN_VERB, "Rücksicht nehmen", "to show consideration", null, "Bitte nehmen Sie Rücksicht auf Kollegen."),
        B2Card("nv11", B2Category.NOMEN_VERB, "zur Verfügung stehen", "to be available", null, "Ich stehe Ihnen gerne zur Verfügung."),
        B2Card("nv12", B2Category.NOMEN_VERB, "in Betracht ziehen", "to consider", null, "Das sollten wir in Betracht ziehen."),
        B2Card("nv13", B2Category.NOMEN_VERB, "in Frage kommen", "to be possible / eligible", null, "Das kommt leider nicht in Frage."),
        B2Card("nv14", B2Category.NOMEN_VERB, "eine Rolle spielen", "to play a role", null, "Die Kosten spielen eine wichtige Rolle."),
        B2Card("nv15", B2Category.NOMEN_VERB, "Verantwortung übernehmen", "to take responsibility", null, "Jeder muss Verantwortung übernehmen."),
        B2Card("nv16", B2Category.NOMEN_VERB, "Kritik üben", "to criticize", null, "Er übt Kritik an der Entscheidung."),
        B2Card("nv17", B2Category.NOMEN_VERB, "Maßnahmen ergreifen", "to take measures", null, "Wir müssen Maßnahmen ergreifen."),
        B2Card("nv18", B2Category.NOMEN_VERB, "einen Beitrag leisten", "to make a contribution", null, "Jeder kann einen Beitrag leisten."),
        B2Card("nv19", B2Category.NOMEN_VERB, "Rücksprache halten", "to consult / check back", null, "Ich halte kurz Rücksprache mit meinem Team."),
        B2Card("nv20", B2Category.NOMEN_VERB, "Stellung nehmen", "to give an opinion / comment", null, "Bitte nehmen Sie dazu Stellung."),
        B2Card("nv21", B2Category.NOMEN_VERB, "einen Überblick geben", "to give an overview", null, "Ich gebe Ihnen kurz einen Überblick."),
        B2Card("nv22", B2Category.NOMEN_VERB, "Verständnis zeigen", "to show understanding", null, "Ich zeige Verständnis für Ihre Situation."),
        B2Card("nv23", B2Category.NOMEN_VERB, "Bezug nehmen auf", "to refer to", null, "Ich nehme Bezug auf unser letztes Gespräch."),
        B2Card("nv24", B2Category.NOMEN_VERB, "in Anspruch nehmen", "to make use of / claim", null, "Sie können unser Angebot in Anspruch nehmen."),
        B2Card("nv25", B2Category.NOMEN_VERB, "zur Kenntnis nehmen", "to take note of", null, "Bitte nehmen Sie das zur Kenntnis."),
        B2Card("nv26", B2Category.NOMEN_VERB, "Einfluss nehmen", "to exert influence", null, "Er nimmt Einfluss auf die Entscheidung."),
        B2Card("nv27", B2Category.NOMEN_VERB, "einen Vorschlag machen", "to make a suggestion", null, "Ich möchte einen Vorschlag machen."),
        B2Card("nv28", B2Category.NOMEN_VERB, "Fortschritte machen", "to make progress", null, "Das Projekt macht gute Fortschritte."),
        B2Card("nv29", B2Category.NOMEN_VERB, "auf den Punkt kommen", "to get to the point", null, "Ich komme gleich auf den Punkt."),
        B2Card("nv30", B2Category.NOMEN_VERB, "eine Ausnahme machen", "to make an exception", null, "Können Sie in diesem Fall eine Ausnahme machen?"),
        B2Card("nv31", B2Category.NOMEN_VERB, "Rückmeldung geben", "to give feedback", null, "Bitte geben Sie mir kurz Rückmeldung."),
        B2Card("nv32", B2Category.NOMEN_VERB, "eine Vereinbarung treffen", "to make an agreement", null, "Wir haben eine Vereinbarung getroffen."),
        B2Card("nv33", B2Category.NOMEN_VERB, "zum Ausdruck bringen", "to express", null, "Ich möchte meinen Dank zum Ausdruck bringen."),
        B2Card("nv34", B2Category.NOMEN_VERB, "Kosten verursachen", "to cause costs", null, "Das würde hohe Kosten verursachen."),
        B2Card("nv35", B2Category.NOMEN_VERB, "eine Zusammenfassung geben", "to give a summary", null, "Ich gebe eine kurze Zusammenfassung."),
        B2Card("nv36", B2Category.NOMEN_VERB, "eine Anfrage bearbeiten", "to process an inquiry", null, "Wir bearbeiten Ihre Anfrage so schnell wie möglich."),
        B2Card("nv37", B2Category.NOMEN_VERB, "Vertrauen aufbauen", "to build trust", null, "Es ist wichtig, Vertrauen aufzubauen."),
        B2Card("nv38", B2Category.NOMEN_VERB, "Interesse wecken", "to arouse interest", null, "Das Angebot hat großes Interesse geweckt."),
        B2Card("nv39", B2Category.NOMEN_VERB, "eine Aufgabe erfüllen", "to fulfil a task", null, "Sie erfüllt ihre Aufgaben sehr gewissenhaft."),
        B2Card("nv40", B2Category.NOMEN_VERB, "Erfahrungen sammeln", "to gain experience", null, "Ich habe viel Erfahrung in diesem Bereich gesammelt."),
    )

    // ─── Wortschatz B2 Beruf (50) ────────────────────────────────────────────

    val wortschatz: List<B2Card> = listOf(
        // Telefonieren
        B2Card("ws01", B2Category.WORTSCHATZ, "sich melden", "to answer / pick up", null, "Niemand hat sich gemeldet.", "Telefonieren"),
        B2Card("ws02", B2Category.WORTSCHATZ, "verbinden", "to transfer / connect", null, "Ich verbinde Sie mit der Buchhaltung.", "Telefonieren"),
        B2Card("ws03", B2Category.WORTSCHATZ, "zurückrufen", "to call back", null, "Kann ich Sie zurückrufen?", "Telefonieren"),
        B2Card("ws04", B2Category.WORTSCHATZ, "ausrichten", "to pass on a message", null, "Kann ich etwas ausrichten?", "Telefonieren"),
        B2Card("ws05", B2Category.WORTSCHATZ, "hinterlassen", "to leave (a message)", null, "Möchten Sie eine Nachricht hinterlassen?", "Telefonieren"),
        B2Card("ws06", B2Category.WORTSCHATZ, "die Durchwahl", "direct line / extension", null, "Meine Durchwahl ist 204.", "Telefonieren"),
        B2Card("ws07", B2Category.WORTSCHATZ, "der Anrufbeantworter", "answering machine", null, "Sprechen Sie nach dem Signalton.", "Telefonieren"),
        B2Card("ws08", B2Category.WORTSCHATZ, "in der Leitung halten", "to put on hold", null, "Bleiben Sie bitte in der Leitung.", "Telefonieren"),
        B2Card("ws09", B2Category.WORTSCHATZ, "buchstabieren", "to spell out", null, "Könnten Sie das buchstabieren?", "Telefonieren"),
        B2Card("ws10", B2Category.WORTSCHATZ, "der Gesprächspartner", "conversation partner", null, "Mein Gesprächspartner war sehr freundlich.", "Telefonieren"),
        // Bewerbung
        B2Card("ws11", B2Category.WORTSCHATZ, "die Bewerbungsunterlagen", "application documents", null, "Bitte senden Sie Ihre Bewerbungsunterlagen.", "Bewerbung"),
        B2Card("ws12", B2Category.WORTSCHATZ, "der Lebenslauf", "CV / resume", null, "Mein Lebenslauf liegt bei.", "Bewerbung"),
        B2Card("ws13", B2Category.WORTSCHATZ, "das Vorstellungsgespräch", "job interview", null, "Das Vorstellungsgespräch war erfolgreich.", "Bewerbung"),
        B2Card("ws14", B2Category.WORTSCHATZ, "die Stelle", "position / job", null, "Ich bewerbe mich um die ausgeschriebene Stelle.", "Bewerbung"),
        B2Card("ws15", B2Category.WORTSCHATZ, "die Anforderungen", "requirements", null, "Ich erfülle alle Anforderungen.", "Bewerbung"),
        B2Card("ws16", B2Category.WORTSCHATZ, "die Qualifikation", "qualification", null, "Meine Qualifikationen passen gut zur Stelle.", "Bewerbung"),
        B2Card("ws17", B2Category.WORTSCHATZ, "die Berufserfahrung", "professional experience", null, "Ich habe fünf Jahre Berufserfahrung.", "Bewerbung"),
        B2Card("ws18", B2Category.WORTSCHATZ, "die Probezeit", "probationary period", null, "Die Probezeit beträgt drei Monate.", "Bewerbung"),
        B2Card("ws19", B2Category.WORTSCHATZ, "das Zeugnis", "reference / certificate", null, "Ich lege mein letztes Arbeitszeugnis bei.", "Bewerbung"),
        B2Card("ws20", B2Category.WORTSCHATZ, "die Gehaltsvorstellung", "salary expectation", null, "Meine Gehaltsvorstellung liegt bei 4.500 Euro.", "Bewerbung"),
        // Arbeitsalltag
        B2Card("ws21", B2Category.WORTSCHATZ, "die Abteilung", "department", null, "Ich arbeite in der IT-Abteilung.", "Arbeitsalltag"),
        B2Card("ws22", B2Category.WORTSCHATZ, "die Besprechung", "meeting", null, "Wir haben um 10 Uhr eine Besprechung.", "Arbeitsalltag"),
        B2Card("ws23", B2Category.WORTSCHATZ, "die Zuständigkeit", "responsibility / remit", null, "Das fällt in meine Zuständigkeit.", "Arbeitsalltag"),
        B2Card("ws24", B2Category.WORTSCHATZ, "der Vorgesetzte", "superior / manager", null, "Mein Vorgesetzter ist sehr fair.", "Arbeitsalltag"),
        B2Card("ws25", B2Category.WORTSCHATZ, "die Überstunden", "overtime", null, "Ich mache oft Überstunden.", "Arbeitsalltag"),
        B2Card("ws26", B2Category.WORTSCHATZ, "die Frist", "deadline", null, "Die Frist läuft morgen ab.", "Arbeitsalltag"),
        B2Card("ws27", B2Category.WORTSCHATZ, "der Auftrag", "order / assignment", null, "Wir haben einen neuen Auftrag bekommen.", "Arbeitsalltag"),
        B2Card("ws28", B2Category.WORTSCHATZ, "die Lieferung", "delivery", null, "Die Lieferung kommt nächste Woche.", "Arbeitsalltag"),
        B2Card("ws29", B2Category.WORTSCHATZ, "die Rechnung", "invoice / bill", null, "Die Rechnung wurde noch nicht bezahlt.", "Arbeitsalltag"),
        B2Card("ws30", B2Category.WORTSCHATZ, "das Protokoll", "minutes / record", null, "Ich schreibe das Protokoll der Sitzung.", "Arbeitsalltag"),
        B2Card("ws31", B2Category.WORTSCHATZ, "die Sitzung", "meeting / session", null, "Die Sitzung beginnt pünktlich.", "Arbeitsalltag"),
        B2Card("ws32", B2Category.WORTSCHATZ, "der Bericht", "report", null, "Ich schreibe einen Bericht über das Projekt.", "Arbeitsalltag"),
        B2Card("ws33", B2Category.WORTSCHATZ, "die Vereinbarung", "agreement / arrangement", null, "Wir haben eine Vereinbarung getroffen.", "Arbeitsalltag"),
        B2Card("ws34", B2Category.WORTSCHATZ, "die Zusammenarbeit", "cooperation", null, "Die Zusammenarbeit läuft sehr gut.", "Arbeitsalltag"),
        B2Card("ws35", B2Category.WORTSCHATZ, "weiterleiten", "to forward", null, "Ich leite Ihre E-Mail weiter.", "Arbeitsalltag"),
        B2Card("ws36", B2Category.WORTSCHATZ, "bearbeiten", "to process / handle", null, "Wir bearbeiten Ihre Anfrage sofort.", "Arbeitsalltag"),
        B2Card("ws37", B2Category.WORTSCHATZ, "klären", "to clarify", null, "Wir müssen das noch klären.", "Arbeitsalltag"),
        B2Card("ws38", B2Category.WORTSCHATZ, "erledigen", "to get done / take care of", null, "Ich erledige das bis Freitag.", "Arbeitsalltag"),
        B2Card("ws39", B2Category.WORTSCHATZ, "genehmigen", "to approve", null, "Der Antrag wurde genehmigt.", "Arbeitsalltag"),
        B2Card("ws40", B2Category.WORTSCHATZ, "der Ansprechpartner", "contact person", null, "Wer ist Ihr Ansprechpartner?", "Arbeitsalltag"),
        // Formell
        B2Card("ws41", B2Category.WORTSCHATZ, "bezüglich / betreff", "regarding / re:", null, "Bezüglich Ihres Schreibens vom 01.04.", "Formell"),
        B2Card("ws42", B2Category.WORTSCHATZ, "hiermit", "hereby", null, "Hiermit bestätige ich den Erhalt.", "Formell"),
        B2Card("ws43", B2Category.WORTSCHATZ, "anbei / beigefügt", "enclosed / attached", null, "Anbei sende ich Ihnen die Unterlagen.", "Formell"),
        B2Card("ws44", B2Category.WORTSCHATZ, "unverzüglich", "immediately / without delay", null, "Bitte antworten Sie unverzüglich.", "Formell"),
        B2Card("ws45", B2Category.WORTSCHATZ, "fristgerecht", "on time / within the deadline", null, "Die Lieferung erfolgte fristgerecht.", "Formell"),
        B2Card("ws46", B2Category.WORTSCHATZ, "rückwirkend", "retroactively", null, "Die Änderung gilt rückwirkend ab Januar.", "Formell"),
        B2Card("ws47", B2Category.WORTSCHATZ, "verbindlich", "binding / obligatory", null, "Dies ist eine verbindliche Zusage.", "Formell"),
        B2Card("ws48", B2Category.WORTSCHATZ, "zur Kenntnisnahme", "for your information (FYI)", null, "Zur Kenntnisnahme leite ich weiter.", "Formell"),
        B2Card("ws49", B2Category.WORTSCHATZ, "in Erwartung Ihrer Antwort", "awaiting your reply", null, "In Erwartung Ihrer Antwort verbleibe ich...", "Formell"),
        B2Card("ws50", B2Category.WORTSCHATZ, "mit freundlichen Grüßen", "kind regards", null, "Mit freundlichen Grüßen, [Name]", "Formell"),
    )

    // ─── Redemittel (28) ─────────────────────────────────────────────────────

    val redemittel: List<B2Card> = listOf(
        B2Card("rm01", B2Category.REDEMITTEL, "Sich am Telefon vorstellen", "Guten Tag, hier ist [Name] von [Firma]. Ich rufe an wegen...", null, "Introducing yourself on the phone", "Telefonieren"),
        B2Card("rm02", B2Category.REDEMITTEL, "Gesprächspartner erfragen", "Könnte ich bitte mit Frau/Herrn [Name] sprechen?", null, "Asking to speak to someone", "Telefonieren"),
        B2Card("rm03", B2Category.REDEMITTEL, "Nachricht hinterlassen", "Könnten Sie ihm/ihr bitte ausrichten, dass ich angerufen habe?", null, "Leaving a message", "Telefonieren"),
        B2Card("rm04", B2Category.REDEMITTEL, "Rückruf ankündigen", "Ich rufe in einer Stunde noch einmal an.", null, "Announcing a callback", "Telefonieren"),
        B2Card("rm05", B2Category.REDEMITTEL, "Nicht verstanden (Telefon)", "Entschuldigung, könnten Sie das bitte wiederholen? Ich habe Sie leider nicht ganz verstanden.", null, "Asking to repeat", "Telefonieren"),
        B2Card("rm06", B2Category.REDEMITTEL, "Buchstabieren bitten", "Könnten Sie das bitte buchstabieren?", null, "Asking to spell", "Telefonieren"),
        B2Card("rm07", B2Category.REDEMITTEL, "Zusammenfassen / Bestätigen", "Wenn ich Sie richtig verstanden habe, dann...", null, "Confirming understanding", "Telefonieren"),
        B2Card("rm08", B2Category.REDEMITTEL, "Gespräch beenden", "Vielen Dank für das Gespräch. Auf Wiederhören!", null, "Ending the call formally", "Telefonieren"),
        B2Card("rm09", B2Category.REDEMITTEL, "Meinung äußern", "Meiner Meinung nach... / Ich bin der Ansicht, dass...", null, "Expressing opinion", "Sprechen"),
        B2Card("rm10", B2Category.REDEMITTEL, "Zustimmen", "Da haben Sie völlig Recht. / Ich sehe das genauso.", null, "Agreeing", "Sprechen"),
        B2Card("rm11", B2Category.REDEMITTEL, "Widersprechen (höflich)", "Das mag sein, aber... / Ich sehe das etwas anders, weil...", null, "Politely disagreeing", "Sprechen"),
        B2Card("rm12", B2Category.REDEMITTEL, "Einschränken / Einwand", "Das stimmt zwar, aber man sollte auch bedenken, dass...", null, "Adding a caveat", "Sprechen"),
        B2Card("rm13", B2Category.REDEMITTEL, "Beispiel nennen", "Zum Beispiel... / Um ein Beispiel zu nennen...", null, "Giving an example", "Sprechen"),
        B2Card("rm14", B2Category.REDEMITTEL, "Zusammenfassen", "Zusammenfassend lässt sich sagen, dass...", null, "Summarizing", "Sprechen"),
        B2Card("rm15", B2Category.REDEMITTEL, "Nachfragen / Klärung", "Was meinen Sie genau damit? / Könnten Sie das näher erläutern?", null, "Asking for clarification", "Sprechen"),
        B2Card("rm16", B2Category.REDEMITTEL, "Vorschlag machen", "Ich schlage vor, dass wir... / Wäre es möglich,...?", null, "Making a suggestion", "Sprechen"),
        B2Card("rm17", B2Category.REDEMITTEL, "Zeit gewinnen", "Das ist eine interessante Frage. Ich muss kurz nachdenken...", null, "Buying time", "Sprechen"),
        B2Card("rm18", B2Category.REDEMITTEL, "Bedauern ausdrücken", "Leider ist das nicht möglich, weil... / Das tut mir leid, aber...", null, "Expressing regret", "Sprechen"),
        B2Card("rm19", B2Category.REDEMITTEL, "Auf Problem hinweisen", "Es gibt ein Problem, auf das ich hinweisen möchte.", null, "Pointing out a problem", "Sprechen"),
        B2Card("rm20", B2Category.REDEMITTEL, "Übergeben (Diskussion)", "Ich würde gerne Frau/Herrn [Name] das Wort übergeben.", null, "Handing over to someone", "Sprechen"),
        B2Card("rm21", B2Category.REDEMITTEL, "Brief einleiten – Bezug", "Ich beziehe mich auf Ihr Schreiben vom [Datum].", null, "Referring to a previous letter", "Schreiben"),
        B2Card("rm22", B2Category.REDEMITTEL, "Brief einleiten – Anfrage", "Ich wende mich an Sie, um... / Hiermit möchte ich anfragen, ob...", null, "Writing an inquiry", "Schreiben"),
        B2Card("rm23", B2Category.REDEMITTEL, "Beschwerde einleiten", "Leider muss ich mich über [Problem] beschweren.", null, "Starting a complaint", "Schreiben"),
        B2Card("rm24", B2Category.REDEMITTEL, "Entschuldigung (formell)", "Wir entschuldigen uns aufrichtig für die entstandenen Unannehmlichkeiten.", null, "Formal apology", "Schreiben"),
        B2Card("rm25", B2Category.REDEMITTEL, "Bitte formulieren", "Ich bitte Sie daher, [Infinitiv]... / Wir bitten Sie höflich um...", null, "Formulating a polite request", "Schreiben"),
        B2Card("rm26", B2Category.REDEMITTEL, "Hilfe anbieten (Abschluss)", "Für Rückfragen stehe ich Ihnen gerne zur Verfügung.", null, "Offering further assistance", "Schreiben"),
        B2Card("rm27", B2Category.REDEMITTEL, "Dank im Voraus", "Ich bedanke mich im Voraus für Ihre Mühe.", null, "Thanking in advance", "Schreiben"),
        B2Card("rm28", B2Category.REDEMITTEL, "Grußformel (formell)", "Mit freundlichen Grüßen / Hochachtungsvoll", null, "Formal closing", "Schreiben"),
    )

    // ─── Adjektiv-Deklination tables ─────────────────────────────────────────

    val deklTables: List<DeklTable> = listOf(
        DeklTable(
            title = "Nach dem bestimmten Artikel (der/die/das)",
            note = "Endungen sind fast immer -e oder -en",
            rows = listOf(
                DeklRow("Nominativ", "-e",  "-e",  "-e",  "-en"),
                DeklRow("Akkusativ", "-en", "-e",  "-e",  "-en"),
                DeklRow("Dativ",     "-en", "-en", "-en", "-en"),
                DeklRow("Genitiv",   "-en", "-en", "-en", "-en"),
            ),
            examples = listOf(
                "der alt-e Mann → den alt-en Mann (Akk)",
                "die neu-e Stelle → der neu-en Stelle (Gen)",
                "das wichtig-e Dokument → dem wichtig-en Dokument (Dat)",
                "die lang-en Gespräche → den lang-en Gesprächen (Dat pl)",
            )
        ),
        DeklTable(
            title = "Nach dem unbestimmten Artikel (ein/eine)",
            note = "Adjektiv trägt das Genus-Signal im Nominativ + Akk Neutrum",
            rows = listOf(
                DeklRow("Nominativ", "-er", "-e",  "-es", "-en"),
                DeklRow("Akkusativ", "-en", "-e",  "-es", "-en"),
                DeklRow("Dativ",     "-en", "-en", "-en", "-en"),
                DeklRow("Genitiv",   "-en", "-en", "-en", "-en"),
            ),
            examples = listOf(
                "ein gut-er Kollege (Nom m)",
                "eine wichtig-e Aufgabe (Nom f)",
                "ein neu-es Projekt (Nom n)",
                "mit einem gut-en Team (Dat n)",
            )
        ),
        DeklTable(
            title = "Ohne Artikel (starke Deklination)",
            note = "Adjektiv allein trägt alle Genus- und Kasusinformationen",
            rows = listOf(
                DeklRow("Nominativ", "-er", "-e",  "-es", "-e"),
                DeklRow("Akkusativ", "-en", "-e",  "-es", "-e"),
                DeklRow("Dativ",     "-em", "-er", "-em", "-en"),
                DeklRow("Genitiv",   "-en", "-er", "-en", "-er"),
            ),
            examples = listOf(
                "mit freundlich-en Grüßen (Dat pl)",
                "bei schlechtem Wetter (Dat n)",
                "trotz groß-er Probleme (Gen pl)",
                "frisch-er Kaffee (Nom m)",
            )
        ),
    )

    val b2Adjectives: List<Triple<String, String, String>> = listOf(
        Triple("beruflich",     "professional/occupational", "berufliche Erfahrung"),
        Triple("schriftlich",   "written",                   "eine schriftliche Bestätigung"),
        Triple("mündlich",      "oral/verbal",               "eine mündliche Vereinbarung"),
        Triple("gegenseitig",   "mutual",                    "gegenseitiges Vertrauen"),
        Triple("zuständig",     "responsible/competent",     "der zuständige Mitarbeiter"),
        Triple("verfügbar",     "available",                 "ein verfügbarer Termin"),
        Triple("verbindlich",   "binding/obligatory",        "eine verbindliche Zusage"),
        Triple("dringend",      "urgent",                    "eine dringende Angelegenheit"),
        Triple("nachhaltig",    "sustainable/lasting",       "nachhaltige Lösungen"),
        Triple("selbstständig", "independent/self-employed", "selbstständiges Arbeiten"),
        Triple("teamfähig",     "able to work in a team",   "ein teamfähiger Mitarbeiter"),
        Triple("leistungsfähig","efficient/capable",         "ein leistungsfähiges System"),
        Triple("kurzfristig",   "short-term",                "kurzfristige Maßnahmen"),
        Triple("langfristig",   "long-term",                 "langfristige Planung"),
        Triple("gelegentlich",  "occasional",                "gelegentliche Überstunden"),
    )

    // ─── Convenience: all flashcard-able cards combined ──────────────────────

    val allCards: List<B2Card> = verbenPraep + nomenVerb + wortschatz + redemittel

    fun cardsForCategory(category: B2Category): List<B2Card> = when (category) {
        B2Category.ALL           -> allCards
        B2Category.VERBEN_PRAEP  -> verbenPraep
        B2Category.NOMEN_VERB    -> nomenVerb
        B2Category.WORTSCHATZ    -> wortschatz
        B2Category.REDEMITTEL    -> redemittel
        B2Category.GRAMMAR       -> emptyList() // Grammar tab uses deklTables instead
    }
}

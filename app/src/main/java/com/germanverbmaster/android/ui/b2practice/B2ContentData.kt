package com.germanverbmaster.android.ui.b2practice

import com.germanverbmaster.android.domain.model.B2Card
import com.germanverbmaster.android.domain.model.B2Category
import com.germanverbmaster.android.domain.model.GrammarSection
import com.germanverbmaster.android.domain.model.GrammarTable

object B2ContentData {
    private val adjectiveDeclensionCoverageItems = listOf(
        "M+Nom", "M+Akk", "M+Dat", "M+Gen",
        "F+Nom", "F+Akk", "F+Dat", "F+Gen",
        "N+Nom", "N+Akk", "N+Dat", "N+Gen",
        "Pl+Nom", "Pl+Akk", "Pl+Dat", "Pl+Gen"
    )

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

    // ─── Grammar tables ──────────────────────────────────────────────────────

    val grammarTables: List<GrammarTable> = listOf(
        GrammarTable(
            title = "Adjektivdeklination: Bestimmter Artikel",
            note = "Nach: der, die, das, dieser, jener, jeder, welcher",
            headers = listOf("Kasus", "Mask.", "Fem.", "Neut.", "Plural"),
            rows = listOf(
                listOf("Nominativ", "-e",  "-e",  "-e",  "-en"),
                listOf("Akkusativ", "-en", "-e",  "-e",  "-en"),
                listOf("Dativ",     "-en", "-en", "-en", "-en"),
                listOf("Genitiv",   "-en", "-en", "-en", "-en"),
            ),
            examples = listOf(
                "M+Nom: Der neue Kollege ist nett.",
                "M+Akk: Ich sehe den neuen Kollegen.",
                "M+Dat: Ich helfe dem neuen Kollegen.",
                "M+Gen: Das ist der Wagen des neuen Kollegen.",
                "F+Nom: Die wichtige Sitzung beginnt.",
                "F+Akk: Wir planen die wichtige Sitzung.",
                "F+Dat: In der wichtigen Sitzung besprechen wir das Projekt.",
                "F+Gen: Das ist das Ende der wichtigen Sitzung.",
                "N+Nom: Das neue Projekt startet.",
                "N+Akk: Wir leiten das neue Projekt.",
                "N+Dat: Bei dem neuen Projekt gibt es Verzögerungen.",
                "N+Gen: Der Erfolg des neuen Projekts ist uns wichtig.",
                "Pl+Nom: Die neuen Fristen gelten.",
                "Pl+Akk: Wir prüfen die neuen Fristen.",
                "Pl+Dat: Mit den neuen Fristen haben wir mehr Zeit.",
                "Pl+Gen: Die Einhaltung der neuen Fristen ist Pflicht."
            ),
            rule = "Der Artikel zeigt Genus, Kasus und Numerus bereits deutlich; deshalb trägt das Adjektiv meistens die schwache Endung -e oder -en.",
            mistakes = listOf(
                "Nicht: der neuer Kollege. Richtig: der neue Kollege.",
                "Nach Dativ und Genitiv fast immer -en: mit dem neuen Plan, wegen des neuen Plans."
            ),
            coverageItems = adjectiveDeclensionCoverageItems
        ),
        GrammarTable(
            title = "Adjektivdeklination: Unbestimmter Artikel",
            note = "Nach: ein, eine, kein, mein, dein, sein, ihr...",
            headers = listOf("Kasus", "Mask.", "Fem.", "Neut.", "Plural"),
            rows = listOf(
                listOf("Nominativ", "-er", "-e",  "-es", "-en"),
                listOf("Akkusativ", "-en", "-e",  "-es", "-en"),
                listOf("Dativ",     "-en", "-en", "-en", "-en"),
                listOf("Genitiv",   "-en", "-en", "-en", "-en"),
            ),
            examples = listOf(
                "M+Nom: Ein guter Plan hilft.",
                "M+Akk: Wir brauchen einen guten Plan.",
                "M+Dat: Mit einem guten Plan sind wir schneller.",
                "M+Gen: Das ist der Erfolg eines guten Plans.",
                "F+Nom: Eine neue Stelle ist frei.",
                "F+Akk: Er sucht eine neue Stelle.",
                "F+Dat: In einer neuen Stelle findet man neue Freunde.",
                "F+Gen: Das sind die Anforderungen einer neuen Stelle.",
                "N+Nom: Ein neues Argument zählt.",
                "N+Akk: Er nennt ein neues Argument.",
                "N+Dat: Mit einem neuen Argument überzeugst du mich.",
                "N+Gen: Er macht das trotz eines neuen Arguments.",
                "Pl+Nom: Keine großen Probleme treten auf.",
                "Pl+Akk: Wir haben keine großen Probleme.",
                "Pl+Dat: Bei keinen großen Problemen läuft alles planmäßig.",
                "Pl+Gen: Trotz keiner großen Probleme prüfen wir den Ablauf noch einmal."
            ),
            rule = "Wenn der Artikel eine Endung trägt, übernimmt das Adjektiv meist -en. Wo der Artikel keine Endung zeigt, trägt das Adjektiv die starke Endung: ein guter Plan, ein neues Projekt.",
            mistakes = listOf(
                "Nicht: ein gute Plan. Richtig: ein guter Plan.",
                "Kein im Plural verhält sich wie ein Artikel mit Endung: keine großen Probleme."
            ),
            coverageItems = adjectiveDeclensionCoverageItems
        ),
        GrammarTable(
            title = "Adjektivdeklination: Ohne Artikel",
            note = "Starke Deklination nach Nullartikel, Mengenangaben oder unbestimmten Pluralformen.",
            headers = listOf("Kasus", "Mask.", "Fem.", "Neut.", "Plural"),
            rows = listOf(
                listOf("Nominativ", "-er", "-e",  "-es", "-e"),
                listOf("Akkusativ", "-en", "-e",  "-es", "-e"),
                listOf("Dativ",     "-em", "-er", "-em", "-en"),
                listOf("Genitiv",   "-en", "-er", "-en", "-er"),
            ),
            examples = listOf(
                "M+Nom: Guter Service ist wichtig.",
                "M+Akk: Wir brauchen guten Service.",
                "M+Dat: Mit gutem Service gewinnen wir Kunden.",
                "M+Gen: Trotz guten Services gab es Beschwerden.",
                "F+Nom: Schnelle Hilfe spart Zeit.",
                "F+Akk: Wir bieten schnelle Hilfe an.",
                "F+Dat: Mit schneller Hilfe lösen wir das Problem.",
                "F+Gen: Wegen schneller Hilfe konnten wir den Termin halten.",
                "N+Nom: Gutes Feedback hilft dem Team.",
                "N+Akk: Ich erwarte gutes Feedback.",
                "N+Dat: Mit gutem Feedback verbessern wir den Kurs.",
                "N+Gen: Trotz guten Feedbacks ändern wir die Aufgabe.",
                "Pl+Nom: Neue Ideen entstehen im Gespräch.",
                "Pl+Akk: Wir sammeln neue Ideen.",
                "Pl+Dat: Mit neuen Ideen verbessern wir den Prozess.",
                "Pl+Gen: Wegen neuer Ideen planen wir einen Workshop."
            ),
            rule = "Ohne Artikel muss das Adjektiv die fehlende Kasus- und Genusinformation selbst tragen.",
            mistakes = listOf(
                "Nicht: mit neu Ideen. Richtig: mit neuen Ideen.",
                "Im Genitiv Maskulin/Neutrum steht beim Adjektiv meist -en: guten Mutes, schweren Herzens."
            ),
            coverageItems = adjectiveDeclensionCoverageItems
        ),
        GrammarTable(
            title = "Satzverbindungen (Konnektoren)",
            note = "TELC B2 Beruf: Ursache, Folge, Gegensatz, Bedingung, Zweck und zeitliche Abfolge sicher verbinden.",
            headers = listOf("Typ", "Beispiele", "Verbposition"),
            rows = listOf(
                listOf("ADUSO", "aber, denn, und, sondern, oder", "Position 0 (keine Änderung)"),
                listOf("Subjunktionen: Grund", "weil, da, zumal", "Verb am Ende"),
                listOf("Subjunktionen: Gegensatz", "obwohl, auch wenn, während", "Verb am Ende"),
                listOf("Subjunktionen: Bedingung", "wenn, falls, sofern", "Verb am Ende"),
                listOf("Subjunktionen: Zeit", "bevor, nachdem, während, sobald, seitdem, bis", "Verb am Ende"),
                listOf("Subjunktionen: Zweck/Folge", "damit, sodass", "Verb am Ende"),
                listOf("Konjunktionaladverbien: Folge", "deshalb, deswegen, daher, darum, folglich, somit", "Inversion (Verb an Pos. 2)"),
                listOf("Konjunktionaladverbien: Gegensatz", "trotzdem, dennoch, allerdings, jedoch", "Inversion (Verb an Pos. 2)"),
                listOf("Konjunktionaladverbien: Ergänzung", "außerdem, zudem, darüber hinaus", "Inversion (Verb an Pos. 2)"),
                listOf("Konjunktionaladverbien: Ablauf", "zuerst, anschließend, danach, inzwischen, schließlich", "Inversion (Verb an Pos. 2)"),
                listOf("Nominale Konnektoren", "wegen, aufgrund, trotz, während, infolge", "Präposition + Kasus"),
            ),
            examples = listOf(
                "ADUSO: Die Lieferung ist angekommen, aber die Rechnung fehlt noch.",
                "Grund: Wir verschieben den Termin, weil mehrere Kollegen krank sind.",
                "Gegensatz: Obwohl die Kosten gestiegen sind, halten wir am Projekt fest.",
                "Bedingung: Falls Sie Rückfragen haben, wenden Sie sich bitte an die Personalabteilung.",
                "Zeit: Nachdem wir die Unterlagen geprüft haben, schicken wir Ihnen eine Rückmeldung.",
                "Zweck: Ich sende Ihnen die Liste, damit Sie die Daten kontrollieren können.",
                "Folge: Die Frist ist sehr kurz; deshalb brauchen wir heute eine Entscheidung.",
                "Ergänzung: Wir benötigen Ihre Unterschrift; außerdem fehlt noch die Kopie Ihres Ausweises.",
                "Nominal: Aufgrund der hohen Nachfrage verlängern sich die Lieferzeiten."
            ),
            rule = "Konnektoren verbinden Aussagen, bestimmen aber auch die Verbposition. ADUSO verbindet Hauptsätze ohne Umstellung; Subjunktionen schicken das finite Verb ans Ende; Konjunktionaladverbien stehen im Satzfeld und lösen Inversion aus. Nominale Konnektoren verbinden mit einem Nomen statt mit einem Nebensatz.",
            mistakes = listOf(
                "Nicht: Weil ich habe Zeit. Richtig: Weil ich Zeit habe.",
                "Nicht: Deshalb ich komme später. Richtig: Deshalb komme ich später.",
                "Nicht da und denn mischen: Da die Frist abläuft, brauchen wir eine Entscheidung. / Die Frist läuft ab, denn wir haben nur zwei Tage Zeit.",
                "Trotzdem ist ein Adverb mit Inversion; obwohl ist eine Subjunktion mit Verb am Ende."
            ),
            extraSections = listOf(
                GrammarSection(
                    title = "Doppelkonnektoren",
                    items = listOf(
                        "je ... desto: Je früher wir anfangen, desto schneller sind wir fertig.",
                        "entweder ... oder: Entweder wir verschieben den Termin, oder wir verkürzen die Agenda.",
                        "weder ... noch: Weder der Preis noch die Lieferzeit passen.",
                        "sowohl ... als auch: Das Angebot ist sowohl günstig als auch zuverlässig.",
                        "nicht nur ... sondern auch: Die Lösung spart nicht nur Zeit, sondern verbessert auch die Qualität."
                    )
                ),
                GrammarSection(
                    title = "TELC Beruf Funktionen",
                    items = listOf(
                        "Begründen: weil, da, denn, aufgrund, wegen.",
                        "Einschränken: obwohl, trotzdem, dennoch, allerdings, jedoch.",
                        "Bedingung nennen: wenn, falls, sofern, andernfalls.",
                        "Folge ausdrücken: deshalb, deswegen, daher, folglich, somit, sodass.",
                        "Ergänzen: außerdem, zudem, darüber hinaus.",
                        "Ablauf strukturieren: zuerst, anschließend, danach, inzwischen, schließlich.",
                        "Zweck nennen: damit, um ... zu."
                    )
                )
            ),
            coverageItems = listOf(
                "ADUSO",
                "Subjunktionen: Grund",
                "Subjunktionen: Gegensatz",
                "Subjunktionen: Bedingung",
                "Subjunktionen: Zeit",
                "Subjunktionen: Zweck/Folge",
                "Konjunktionaladverbien: Folge",
                "Konjunktionaladverbien: Gegensatz",
                "Konjunktionaladverbien: Ergänzung",
                "Konjunktionaladverbien: Ablauf",
                "Nominale Konnektoren",
                "Doppelkonnektoren",
                "TELC Beruf Funktionen"
            ),
            useListLayout = true
        ),
        GrammarTable(
            title = "Relativpronomen",
            note = "Das Relativpronomen richtet sich im Genus nach dem Bezugswort.",
            headers = listOf("Kasus", "Mask.", "Fem.", "Neut.", "Plural"),
            rows = listOf(
                listOf("Nominativ", "der", "die", "das", "die"),
                listOf("Akkusativ", "den", "die", "das", "die"),
                listOf("Dativ",     "dem", "der", "dem", "denen"),
                listOf("Genitiv",   "dessen", "deren", "dessen", "deren"),
            ),
            examples = listOf(
                "M+Nom: Der Kollege, der heute kommt, ist nett.",
                "M+Akk: Den Kollegen, den ich kenne, habe ich angerufen.",
                "M+Dat: Dem Kollegen, dem ich helfe, danke ich.",
                "M+Gen: Das ist der Kollege, dessen Auto alt ist.",
                "F+Nom: Die Firma, die dort ist, ist groß.",
                "F+Akk: Die Firma, die ich mag, hat Erfolg.",
                "F+Dat: Das ist die Firma, der ich vertraue.",
                "F+Gen: Das ist die Firma, deren Chef sehr freundlich ist.",
                "N+Nom: Das Kind, das dort spielt, lacht.",
                "N+Akk: Das Kind, das ich sehe, ist klein.",
                "N+Dat: Das ist das Kind, dem ich vorlese.",
                "N+Gen: Ich kenne das Kind, dessen Eltern dort wohnen.",
                "Pl+Nom: Die Kunden, die anrufen, sind wichtig.",
                "Pl+Akk: Die Kunden, die ich besuche, warten.",
                "Pl+Dat: Das sind die Kunden, denen ich antworte.",
                "Pl+Gen: Das sind die Kunden, deren Projekt nun endet."
            ),
            rule = "Genus und Numerus kommen vom Bezugswort; der Kasus kommt aus der Funktion im Relativsatz.",
            mistakes = listOf(
                "Nicht automatisch den Kasus des Bezugsworts übernehmen: Ich kenne den Kollegen, der heute kommt.",
                "Bei Dativ Plural heißt es denen, nicht den: Kunden, denen ich helfe."
            ),
            coverageItems = adjectiveDeclensionCoverageItems
        ),
        GrammarTable(
            title = "Konjunktiv II (Gegenwart & Vergangenheit)",
            note = "Wünsche, irreale Bedingungen, Höflichkeit und vorsichtige Aussagen.",
            headers = listOf("Funktion", "Struktur"),
            rows = listOf(
                listOf("Höfliche Bitte", "könnte / würde + Infinitiv"),
                listOf("Irreale Bedingung", "wenn + Konjunktiv II, dann ..."),
                listOf("Wunsch", "wäre / hätte / würde gern ..."),
                listOf("Rat / Vorschlag", "sollte / könnte + Infinitiv"),
                listOf("Vergangenheit", "hätte/wäre + Partizip II"),
            ),
            examples = listOf(
                "Bitte: Könnten Sie mir die Unterlagen schicken?",
                "Bedingung: Wenn ich Zeit hätte, würde ich den Bericht überarbeiten.",
                "Wunsch: Ich wäre gern bei der Besprechung dabei.",
                "Rat: Du solltest die Frist schriftlich bestätigen.",
                "Vergangenheit: Ich hätte früher reagieren sollen."
            ),
            rule = "Für viele Verben ist würde + Infinitiv die normale Form. Bei sein, haben und Modalverben sind die einfachen Formen sehr häufig: wäre, hätte, könnte, müsste, sollte, dürfte.",
            mistakes = listOf(
                "Nicht: Wenn ich würde Zeit haben. Richtig: Wenn ich Zeit hätte.",
                "Vergangenheit braucht hätte/wäre + Partizip II: Ich hätte angerufen."
            ),
            extraSections = listOf(
                GrammarSection(
                    title = "Weitere Beispiele",
                    items = listOf(
                        "Höfliche Bitte: Würden Sie bitte kurz warten?",
                        "Irreale Bedingung: Wenn die Software stabiler wäre, könnten wir schneller arbeiten.",
                        "Wunsch: Ich hätte gern mehr Zeit für die Vorbereitung.",
                        "Rat: An Ihrer Stelle würde ich die Antwort schriftlich bestätigen.",
                        "Vergangenheit: Wenn wir früher bestellt hätten, wäre die Ware pünktlich angekommen."
                    )
                ),
                GrammarSection(
                    title = "Modalverben",
                    items = listOf(
                        "könnte: vorsichtige Möglichkeit oder höfliche Bitte.",
                        "müsste: vorsichtige Notwendigkeit oder Vermutung.",
                        "sollte: Rat oder Empfehlung.",
                        "dürfte: vorsichtige Wahrscheinlichkeit."
                    )
                )
            ),
            coverageItems = listOf(
                "Höfliche Bitte",
                "Irreale Bedingung",
                "Wunsch",
                "Rat / Vorschlag",
                "Vergangenheit",
                "Weitere Beispiele",
                "Modalverben"
            )
        ),
        GrammarTable(
            title = "Passiv (Vorgangspassiv)",
            note = "Fokus auf die Handlung, nicht die Person.",
            headers = listOf("Form", "Struktur"),
            rows = listOf(
                listOf("Präsens", "werden + Partizip II"),
                listOf("Präteritum", "wurden + Partizip II"),
                listOf("Perfekt", "ist ... worden"),
                listOf("Modalverb", "muss ... werden"),
                listOf("Zustandspassiv", "sein + Partizip II"),
                listOf("Unpersönlich", "Es wird + Partizip II"),
            ),
            examples = listOf(
                "Präsens: Die Rechnung wird heute bezahlt.",
                "Präteritum: Die Passagiere wurden zügig abgefertigt.",
                "Perfekt: Der Vertrag ist unterschrieben worden.",
                "Modalverb: Die Pflichten müssen genau geregelt werden.",
                "Zustand: Das Büro ist schon geschlossen.",
                "Unpersönlich: In der Besprechung wird viel diskutiert."
            ),
            rule = "Vorgangspassiv beschreibt den Ablauf einer Handlung. Zustandspassiv beschreibt das Ergebnis. Der Handelnde kann mit von oder durch ergänzt werden.",
            mistakes = listOf(
                "Nicht: Die Rechnung ist bezahlt worden, wenn nur der Zustand gemeint ist. Besser: Die Rechnung ist bezahlt.",
                "Bei Perfekt Vorgangspassiv steht worden, nicht geworden: Der Antrag ist geprüft worden."
            ),
            extraSections = listOf(
                GrammarSection(
                    title = "Weitere Beispiele",
                    items = listOf(
                        "Präsens mit Agens: Der Antrag wird vom Amt bearbeitet.",
                        "Präteritum: Die Kundin wurde gestern informiert.",
                        "Perfekt: Die Unterlagen sind bereits weitergeleitet worden.",
                        "Plusquamperfekt: Die Rechnung war schon bezahlt worden.",
                        "Futur: Die Ergebnisse werden morgen veröffentlicht werden.",
                        "Zustandspassiv: Der Termin ist bestätigt.",
                        "Unpersönlich: Es wurde lange über die Lösung diskutiert."
                    )
                ),
                GrammarSection(
                    title = "Agens",
                    items = listOf(
                        "von + Dativ für Personen/Institutionen: Der Antrag wird vom Amt geprüft.",
                        "durch + Akkusativ für Mittel/Ursachen: Die Lieferung wurde durch den Streik verzögert."
                    )
                )
            ),
            coverageItems = listOf(
                "Präsens",
                "Präteritum",
                "Perfekt",
                "Modalverb",
                "Zustandspassiv",
                "Unpersönlich",
                "Weitere Beispiele",
                "Agens"
            )
        ),
        GrammarTable(
            title = "werden, worden, geworden & Passiversatzformen",
            note = "Werden bildet auch das Futur: werden + Infinitiv. Davon getrennt: Passiv, worden und geworden.",
            headers = listOf("Form", "Funktion"),
            rows = listOf(
                listOf("werden + Infinitiv", "Futur oder Vermutung: Der Termin wird stattfinden."),
                listOf("werden + Partizip II", "Vorgangspassiv: Der Antrag wird geprüft."),
                listOf("sein + Partizip II", "Zustandspassiv: Der Antrag ist geprüft."),
                listOf("worden", "Perfekt Passiv: Der Antrag ist geprüft worden."),
                listOf("geworden", "Vollverb werden: Die Lage ist schwieriger geworden."),
                listOf("Passiversatz: sich lassen + Infinitiv", "Das Problem lässt sich lösen."),
                listOf("Passiversatz: sein + zu + Infinitiv", "Die Frist ist einzuhalten."),
                listOf("Passiversatz: Adjektiv auf -bar/-lich", "Die Daten sind überprüfbar."),
            ),
            examples = listOf(
                "Futur: Wir werden die Unterlagen morgen senden.",
                "Vorgangspassiv Präsens: Die Unterlagen werden geprüft.",
                "Zustandspassiv: Die Unterlagen sind geprüft.",
                "Perfekt Passiv: Die Unterlagen sind geprüft worden.",
                "Vollverb werden: Die Bearbeitung ist schneller geworden.",
                "sich lassen: Der Fehler lässt sich leicht beheben.",
                "sein + zu: Die Vorschriften sind unbedingt zu beachten.",
                "-bar/-lich: Die Entscheidung ist nachvollziehbar."
            ),
            rule = "Werden + Infinitiv bildet Futur oder eine Vermutung. Werden + Partizip II bildet Vorgangspassiv. Worden steht nur im Perfekt/Plusquamperfekt des Vorgangspassivs. Geworden ist das Partizip II des Vollverbs werden. Bei sich lassen richtet sich lassen nach dem Subjekt: Das Problem lässt sich lösen, die Probleme lassen sich lösen.",
            mistakes = listOf(
                "Nicht: Der Antrag ist geprüft geworden. Richtig: Der Antrag ist geprüft worden.",
                "Nicht jedes werden ist Passiv: Wir werden antworten = Futur, nicht Passiv.",
                "Zustand und Vorgang trennen: Die Tür wird geöffnet = jemand öffnet sie; die Tür ist geöffnet = sie ist offen.",
                "Bei sich lassen nicht immer lässt sich verwenden: Die Daten lassen sich exportieren."
            ),
            extraSections = listOf(
                GrammarSection(
                    title = "sich lassen Beispiele",
                    items = listOf(
                        "Präsens Singular: Das Problem lässt sich lösen.",
                        "Präsens Plural: Die Daten lassen sich exportieren.",
                        "Präteritum: Der Fehler ließ sich gestern nicht reproduzieren.",
                        "Präteritum Plural: Die Dateien ließen sich nicht öffnen.",
                        "Perfekt: Der Termin hat sich kurzfristig verschieben lassen.",
                        "Modalverb: Die Kosten müssen sich transparent darstellen lassen."
                    )
                ),
                GrammarSection(
                    title = "Schnelltest",
                    items = listOf(
                        "Kannst du eine handelnde Person mit von ergänzen? Dann ist oft Vorgangspassiv möglich.",
                        "Beschreibt der Satz nur das Ergebnis? Dann ist Zustandspassiv mit sein oft besser.",
                        "Bedeutet werden eine Entwicklung? Dann ist geworden richtig: Es ist besser geworden."
                    )
                )
            ),
            coverageItems = listOf(
                "werden + Infinitiv",
                "werden + Partizip II",
                "sein + Partizip II",
                "worden",
                "geworden",
                "sich lassen + Infinitiv",
                "sich lassen Beispiele",
                "sein + zu + Infinitiv",
                "-bar/-lich",
                "Schnelltest"
            )
        ),
        GrammarTable(
            title = "N-Deklination",
            note = "Maskuline Nomen mit -n/-en in allen Kasus außer Nominativ.",
            headers = listOf("Gruppe", "Beispiele"),
            rows = listOf(
                listOf("Personen", "der Kollege, der Kunde, der Mensch"),
                listOf("Berufe", "der Praktikant, der Journalist"),
                listOf("Nationalitäten", "der Franzose, der Pole"),
                listOf("Sonderfall", "der Name, der Herr"),
            ),
            examples = listOf(
                "Personen: Ich spreche mit dem Kollegen (Dativ).",
                "Berufe: Er sucht einen neuen Praktikanten (Akkusativ).",
                "Nationalitäten: Ich kenne einen netten Franzosen.",
                "Sonderfall Name: Der Name des Kunden steht auf der Rechnung.",
                "Sonderfall Herr: Ich begrüße den Herrn am Empfang."
            ),
            rule = "Betroffen sind vor allem maskuline Personen- und Berufsbezeichnungen sowie einige feste Sonderfälle.",
            mistakes = listOf(
                "Nicht: mit dem Kollege. Richtig: mit dem Kollegen.",
                "Der Herr bekommt -n: Ich frage den Herrn."
            ),
            extraSections = listOf(
                GrammarSection(
                    title = "Kasusbeispiele",
                    items = listOf(
                        "Nominativ: Der Kunde wartet am Empfang.",
                        "Akkusativ: Ich rufe den Kunden zurück.",
                        "Dativ: Wir senden dem Kunden die Bestätigung.",
                        "Genitiv: Die Anfrage des Kunden ist dringend."
                    )
                )
            ),
            coverageItems = listOf("Personen", "Berufe", "Nationalitäten", "Sonderfall")
        ),
        GrammarTable(
            title = "Präpositionen mit Genitiv",
            note = "Oft in der Schriftsprache oder formellen Kontexten.",
            headers = listOf("Präposition", "Bedeutung"),
            rows = listOf(
                listOf("trotz", "despite"),
                listOf("wegen", "because of"),
                listOf("während", "during"),
                listOf("außerhalb / innerhalb", "outside / inside"),
                listOf("aufgrund", "due to"),
                listOf("bezüglich / betreffend", "regarding"),
            ),
            examples = listOf(
                "trotz: Trotz des Fehlers gab es keinen Tadel.",
                "wegen: Wegen eines Termins kann ich nicht kommen.",
                "während: Während der Arbeitszeit ist es verboten.",
                "außerhalb: Außerhalb der Geschäftszeiten erreichen Sie uns per E-Mail.",
                "innerhalb: Innerhalb einer Woche erhalten Sie eine Antwort.",
                "aufgrund: Aufgrund der hohen Kosten wurde es gestrichen.",
                "bezüglich: Bezüglich Ihres Schreibens haben wir noch Fragen."
            ),
            rule = "Diese Präpositionen verlangen in Standardsprache den Genitiv; in Alltagssprache kommt besonders bei wegen auch Dativ vor.",
            mistakes = listOf(
                "In formellen Texten besser Genitiv: wegen eines Fehlers, nicht wegen einem Fehler.",
                "Bei Plural ohne Artikel muss die Endung sichtbar sein: trotz guter Ergebnisse."
            ),
            coverageItems = listOf("trotz", "wegen", "während", "außerhalb", "innerhalb", "aufgrund", "bezüglich")
        ),
        GrammarTable(
            title = "Infinitiv mit zu / ohne zu",
            note = "B2-relevant für komplexe Sätze und formelle Redemittel.",
            headers = listOf("Typ", "Struktur"),
            rows = listOf(
                listOf("mit zu", "versuchen, planen, bitten, empfehlen + zu + Infinitiv"),
                listOf("trennbare Verben", "zu zwischen Präfix und Verb"),
                listOf("ohne zu", "Modalverben, lassen, gehen, bleiben, hören/sehen"),
            ),
            examples = listOf(
                "Mit zu: Wir planen, den Vertrag morgen zu unterschreiben.",
                "Mit zu: Ich empfehle Ihnen, die Unterlagen vollständig einzureichen.",
                "Mit zu: Es ist wichtig, die Frist einzuhalten.",
                "Trennbar: Ich bitte Sie, die Unterlagen weiterzuleiten.",
                "Trennbar: Wir versuchen, den Termin vorzuziehen.",
                "Ohne zu: Wir müssen die Frist einhalten.",
                "Ohne zu: Ich lasse den Antrag prüfen.",
                "Ohne zu: Ich gehe die Unterlagen holen."
            ),
            rule = "Zu steht vor dem Infinitiv; bei trennbaren Verben steht es zwischen Präfix und Verbstamm.",
            mistakes = listOf(
                "Nicht: weiter zu leiten. Richtig: weiterzuleiten.",
                "Nach Modalverben kein zu: Wir müssen antworten."
            ),
            coverageItems = listOf("mit zu", "trennbare Verben", "ohne zu")
        ),
        GrammarTable(
            title = "Partizip I & II als Adjektive",
            note = "Verdichtet Informationen in formeller Schriftsprache.",
            headers = listOf("Form", "Bedeutung"),
            rows = listOf(
                listOf("Partizip I", "aktiv / gleichzeitig: der wartende Kunde"),
                listOf("Partizip II", "passiv oder abgeschlossen: der unterschriebene Vertrag"),
            ),
            examples = listOf(
                "Partizip I: Die wartenden Kunden wurden informiert.",
                "Partizip I: Der fehlende Nachweis muss nachgereicht werden.",
                "Partizip II: Die bezahlte Rechnung liegt im System.",
                "Partizip II: Die verschobene Sitzung findet nächste Woche statt.",
                "Erweitert: Die gestern eingereichten Unterlagen werden geprüft.",
                "Erweitert: Der von der Firma angebotene Kurs beginnt im Mai."
            ),
            rule = "Partizipien werden wie Adjektive dekliniert und können vor dem Nomen ganze Relativsätze ersetzen.",
            mistakes = listOf(
                "Nicht die Adjektivendung vergessen: die wartenden Kunden.",
                "Aktiv/passiv unterscheiden: der prüfende Mitarbeiter, der geprüfte Antrag."
            ),
            coverageItems = listOf("Partizip I", "Partizip II", "Erweitert")
        ),
        GrammarTable(
            title = "Nominalisierung",
            note = "Macht Aussagen sachlicher und formeller.",
            headers = listOf("Verbaler Stil", "Nominaler Stil"),
            rows = listOf(
                listOf("Wir prüfen den Antrag.", "die Prüfung des Antrags"),
                listOf("Die Preise steigen.", "der Anstieg der Preise"),
                listOf("Wir entscheiden morgen.", "die Entscheidung am morgigen Tag"),
            ),
            examples = listOf(
                "Verbal: Wir prüfen den Antrag bis Freitag.",
                "Nominal: Die Prüfung des Antrags erfolgt bis Freitag.",
                "Verbal: Weil die Nachfrage steigt, erhöhen wir die Produktion.",
                "Nominal: Aufgrund der steigenden Nachfrage erhöhen wir die Produktion.",
                "Verbal: Nachdem wir entschieden haben, informieren wir das Team.",
                "Nominal: Nach der Entscheidung informieren wir das Team.",
                "Verbal: Weil sich die Lieferung verspätet, ändern wir den Plan.",
                "Nominal: Wegen der verspäteten Lieferung ändern wir den Plan."
            ),
            rule = "Nominalisierung ersetzt Verben oder Nebensätze durch Nomen, oft mit Genitiv oder Präposition.",
            mistakes = listOf(
                "Nominalstil nicht übertreiben; zu viele Nomen machen Texte schwer lesbar.",
                "Genitiv sauber bilden: die Prüfung des Antrags, nicht die Prüfung den Antrag."
            ),
            coverageItems = listOf("prüfen", "steigen", "entscheiden", "Aufgrund")
        ),
        GrammarTable(
            title = "Subjektive Bedeutung der Modalverben",
            note = "Modalverben können Vermutungen ausdrücken.",
            headers = listOf("Form", "Bedeutung"),
            rows = listOf(
                listOf("muss", "sehr sicher: Das muss ein Fehler sein."),
                listOf("dürfte", "wahrscheinlich: Das dürfte reichen."),
                listOf("könnte", "möglich: Das könnte funktionieren."),
                listOf("soll", "man sagt / angeblich: Er soll krank sein."),
            ),
            examples = listOf(
                "Das muss ein Missverständnis sein.",
                "Das muss gestern passiert sein.",
                "Die Lieferung dürfte morgen eintreffen.",
                "Die Lösung dürfte für alle Abteilungen passen.",
                "Der Termin könnte verschoben werden.",
                "Der Fehler könnte durch das Update entstanden sein.",
                "Der neue Vertrag soll bereits unterschrieben sein.",
                "Die Firma soll nächste Woche eine Entscheidung treffen."
            ),
            rule = "Die subjektive Bedeutung bewertet, wie sicher die Aussage ist. Sie beschreibt nicht Fähigkeit, Erlaubnis oder Pflicht.",
            mistakes = listOf(
                "Nicht jede Form von müssen bedeutet Pflicht: Das muss stimmen = Ich bin fast sicher.",
                "Für vorsichtige Vermutungen ist dürfte oft formeller als könnte."
            ),
            coverageItems = listOf("muss", "dürfte", "könnte", "soll")
        ),
        GrammarTable(
            title = "Rektion von Adjektiven",
            note = "Viele Adjektive verlangen eine feste Präposition mit Kasus.",
            headers = listOf("Adjektiv", "Ergänzung"),
            rows = listOf(
                listOf("zufrieden", "mit + Dat"),
                listOf("abhängig", "von + Dat"),
                listOf("verantwortlich", "für + Akk"),
                listOf("interessiert", "an + Dat"),
                listOf("geeignet", "für + Akk"),
            ),
            examples = listOf(
                "Wir sind mit dem Ergebnis zufrieden.",
                "Der Kunde ist mit der Lieferzeit unzufrieden.",
                "Der Erfolg ist von der Vorbereitung abhängig.",
                "Die Entscheidung ist von mehreren Faktoren abhängig.",
                "Sie ist für die Abrechnung verantwortlich.",
                "Unser Team ist für die technische Umsetzung zuständig.",
                "Ich bin an einer schnellen Lösung interessiert.",
                "Diese Methode ist für Anfänger geeignet.",
                "Die Schulung ist für neue Mitarbeiter hilfreich."
            ),
            rule = "Die Präposition gehört zum Adjektiv und bestimmt den Kasus der Ergänzung.",
            mistakes = listOf(
                "Nicht aus dem Englischen übertragen: interessiert an, nicht interessiert in.",
                "Kasus mitlernen: zufrieden mit dem Ergebnis, verantwortlich für den Ablauf."
            ),
            coverageItems = listOf("zufrieden", "abhängig", "verantwortlich", "interessiert", "geeignet", "zuständig")
        ),
        GrammarTable(
            title = "Wortstellung im Mittelfeld",
            note = "Hilft bei längeren Haupt- und Nebensätzen.",
            headers = listOf("Bereich", "Tendenz"),
            rows = listOf(
                listOf("Pronomen", "vor Nomen: Ich gebe es dem Kollegen."),
                listOf("Zeit", "meist vor Grund/Art/Ort"),
                listOf("Negation", "nicht vor dem Teil, der verneint wird"),
                listOf("Ort", "oft nach Zeit und Art"),
            ),
            examples = listOf(
                "Ich schicke dem Kunden morgen per E-Mail die Unterlagen.",
                "Ich schicke sie ihm morgen per E-Mail.",
                "Wir treffen uns morgen wegen des Projekts im Büro.",
                "Ich habe den Vertrag nicht gestern unterschrieben, sondern heute.",
                "Ich habe gestern wegen der Reklamation lange mit dem Kunden telefoniert.",
                "Leider können wir Ihnen die Ware erst nächste Woche liefern.",
                "Wir stellen dem Kunden die neuen Bedingungen schriftlich vor.",
                "Wir stellen sie ihm morgen schriftlich vor."
            ),
            rule = "Die Reihenfolge ist nicht mechanisch, aber Pronomen stehen früh; neue oder betonte Informationen stehen oft später.",
            mistakes = listOf(
                "Nicht jedes nicht steht am Satzende; es steht vor dem verneinten Satzteil.",
                "Bei zwei Pronomen steht Akkusativ meist vor Dativ: Ich gebe es ihm."
            ),
            coverageItems = listOf("Pronomen", "Zeit", "Negation", "Ort")
        ),
    )

    // ─── Convenience: all flashcard-able cards combined ──────────────────────

    val allCards: List<B2Card> = verbenPraep + nomenVerb + redemittel

    fun cardsForCategory(category: B2Category): List<B2Card> = when (category) {
        B2Category.ALL           -> allCards
        B2Category.VERBEN_PRAEP  -> verbenPraep
        B2Category.NOMEN_VERB    -> nomenVerb
        B2Category.REDEMITTEL    -> redemittel
    }
}

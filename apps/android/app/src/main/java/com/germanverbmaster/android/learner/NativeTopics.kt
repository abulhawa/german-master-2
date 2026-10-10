package com.germanverbmaster.android.learner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.foundation.contract.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Presentation of catalog metadata and confirmed snapshots only; no learner policy. */
@Composable
fun NativeTopicsView(cache: LearnerCache, screen: String, id: String, german: Boolean, busy: Boolean,
                     open: (String, String) -> Unit, resume: () -> Unit, start: (PracticeFocus) -> Unit) {
    fun text(en: String, de: String) = if (german) de else en
    fun localized(value: LocalizedText) = if (german) value.de else value.en
    @Composable fun button(label: String, enabled: Boolean = !busy, action: () -> Unit) {
        OutlinedButton(onClick = action, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(label) }
    }
    val catalog = cache.catalog
    if (catalog == null) {
        Text(text("Topics could not be loaded. Try refreshing.", "Die Themen konnten nicht geladen werden. Versuche es erneut."))
        return
    }
    if (cache.practice != null) {
        Text(text("Resume or discard saved practice before starting another session.", "Gespeicherte Übung fortsetzen oder verwerfen, bevor eine neue Sitzung beginnt."))
        button(text("Continue practice", "Übung fortsetzen"), action = resume)
    }
    @Composable fun focused(focus: PracticeFocus, available: Int) {
        Text(text("$available questions available.", "$available Aufgaben verfügbar."))
        if (available == 0) Text(text("No questions available for these preferences.", "Für diese Einstellungen sind keine Fragen verfügbar."))
        val count = if (focus is TargetFocus) 1 else minOf(5, available)
        button(text("Practise this ($count)", "Gezielt üben ($count)"), !busy && available > 0 && cache.practice == null && cache.pending == null && cache.profile?.setupCompleted == true) { start(focus) }
    }
    when (screen) {
        "topics" -> {
            if (catalog.topics.isEmpty()) Text(text("No topics available.", "Keine Themen verfügbar."))
            catalog.topics.forEach { topic ->
                button(localized(topic.title)) { open("topic", topic.id) }
                val targets = catalog.targets.filter { it.topicId == topic.id }
                Text(text("${targets.sumOf { it.availableQuestionCount }} questions available.", "${targets.sumOf { it.availableQuestionCount }} Aufgaben verfügbar."))
            }
        }
        "topic" -> {
            val topic = catalog.topics.find { it.id == id }
            if (topic == null) Text(text("Topic unavailable. Refresh to retry.", "Thema nicht verfügbar. Bitte erneut aktualisieren."))
            else {
                Text(localized(topic.title), Modifier.semantics { heading() }, style = MaterialTheme.typography.titleLarge)
                val targets = catalog.targets.filter { it.topicId == id }
                focused(TopicFocus(id), targets.sumOf { it.availableQuestionCount })
                targets.forEach { target -> button(localized(target.title) + " · " + target.level) { open("target", target.id) } }
            }
        }
        "target" -> {
            val target = catalog.targets.find { it.id == id }
            val confirmed = cache.targets.find { it.targetId == id }
            if (target == null) Text(text("Skill details could not be loaded. Try refreshing.", "Die Informationen zu dieser Fähigkeit konnten nicht geladen werden. Versuche es erneut."))
            else {
                Text(localized(target.title), Modifier.semantics { heading() }, style = MaterialTheme.typography.titleLarge)
                Text(localized(target.description))
                Text(target.level)
                catalog.topics.find { it.id == target.topicId }?.let { topic -> button(localized(topic.title)) { open("topic", topic.id) } }
                focused(TargetFocus(id), target.availableQuestionCount)
            }
            if (confirmed == null) Text(text("No practice results to show yet.", "Noch keine Übungsergebnisse vorhanden."))
            else {
                Text(text("Your progress", "Dein Lernstand") + ": " + when (confirmed.state) {
                    "needs_practice" -> text("Needs practice", "Braucht Übung")
                    "improving" -> text("Improving", "Wird sicherer")
                    "mastered" -> text("Mastered", "Sicher")
                    "new" -> text("Not started", "Neu")
                    else -> text("Getting started", "Im Aufbau")
                })
                Text(text("Review checks", "Wiederholungsprüfungen") + ": ${confirmed.qualifyingCheckCount}")
                if (confirmed.isDue) Text(text("Review due in your saved progress", "Laut gespeichertem Lernstand zur Wiederholung fällig"))
                confirmed.schedule.forEach { schedule ->
                    val timezone = cache.profile?.preferences?.timezone ?: "UTC"
                    val date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.of(timezone)).format(Instant.parse(schedule.dueAt))
                    Text(text("Review", "Wiederholung") + ": $date ($timezone)")
                }
            }
        }
    }
}

package com.germanverbmaster.android.learner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.foundation.FoundationTheme
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LearnerPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { FoundationTheme {
            val loaded by produceState<Result<LearnerRepository>?>(null) {
                value = withContext(Dispatchers.IO) { runCatching {
                    LearnerRepository(LocalLearnerApi(), AtomicLearnerStore(File(filesDir, "german-master-v2-local-learner.json")))
                } }
            }
            loaded?.fold(onSuccess = { LearnerShell(it) }, onFailure = {
                Surface { Text("Saved local preview data cannot be read. It has been preserved. / Gespeicherte Vorschaudaten sind nicht lesbar und bleiben erhalten.", Modifier.safeDrawingPadding().padding(24.dp)) }
            })
        } }
    }
}

@Composable
fun LearnerShell(repository: LearnerRepository) {
    var cache by remember { mutableStateOf(repository.state) }
    var busy by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var fresh by remember { mutableStateOf(false) }
    var screen by rememberSaveable { mutableStateOf("home") }
    var detailId by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    fun run(refresh: Boolean = true, completed: () -> Unit = {}, action: suspend () -> Unit) {
        if (busy) return
        busy = true; failed = false; if(refresh) fresh = false
        scope.launch {
            try { withContext(Dispatchers.IO) { action(); if (refresh) repository.refresh() }; if(refresh) fresh = true; completed() }
            catch (cancel: kotlinx.coroutines.CancellationException) { throw cancel }
            catch (_: Exception) { failed = true }
            finally { cache = repository.state; if(cache.practice?.let { it.session != null && it.index == it.session.questions.size } == true) fresh = !failed; busy = false }
        }
    }
    LaunchedEffect(repository) { run {} }
    val german = cache.profile?.preferences?.locale == "de"
    fun label(en: String, de: String) = if (german) de else en
    val setup = screen == "setup" || cache.profile?.setupCompleted != true
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.safeDrawingPadding().imePadding().widthIn(max = 720.dp).fillMaxWidth()
            .verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(label("Local learner preview", "Lokale Lernvorschau"))
            Text(label("Unpublished fixture · shared local account", "Unveröffentlichte Beispieldaten · gemeinsames lokales Konto"))
            if (busy) Text(label("Loading…", "Wird geladen…"), Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            if (cache.contentReport != null && !cache.reportRecorded) {
                Text(label("Exercise report saved; awaiting confirmation.", "Übungsmeldung gespeichert; Bestätigung ausstehend."), Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                LearnerButton(label("Retry saved report", "Gespeicherte Meldung erneut senden"), !busy) { run(false) { repository.reportProblem(requireNotNull(cache.contentReport).category) } }
            }
            if (failed) Text(label("Could not refresh or save. Saved work remains available. Retry the saved operation.", "Aktualisieren oder Speichern fehlgeschlagen. Gespeicherte Daten bleiben erhalten. Gespeicherten Vorgang erneut versuchen."), Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            if (screen == "practice" && cache.practice != null) {
                NativePracticeView(requireNotNull(cache.practice), german, busy, { operation -> run(false) { operation(); if(repository.state.practice?.let { it.session != null && it.index == it.session.questions.size } == true) repository.refresh() } }, repository, { operation -> try { operation() } catch (_: Exception) { failed = true }; cache = repository.state }, openProgress = { screen = "progress"; run {} }) { screen = "home" }
            } else {
            if (cache.pending != null) {
                Text(label("Preferences saved on this device, awaiting confirmation. Progress below is server-confirmed only.", "Einstellungen lokal gespeichert, Bestätigung ausstehend. Fortschritt zeigt nur bestätigte Daten."))
                LearnerButton(label("Retry saved preferences", "Gespeicherte Einstellungen erneut senden"), !busy) { run { repository.retry() } }
                LearnerButton(label("Reload current preferences", "Aktuelle Einstellungen laden"), !busy) { run { repository.reloadProfile() } }
            }
            LearnerButton(label("Refresh", "Aktualisieren"), !busy) { run {} }
            val profile = cache.profile
            if (profile != null && setup) {
                Text(label("Setup and preferences", "Einrichtung und Einstellungen"), Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineMedium)
                key(profile.revision, cache.pending) {
                    var locale by rememberSaveable { mutableStateOf(profile.preferences.locale) }
                    var level by rememberSaveable { mutableStateOf(profile.preferences.level) }
                    var count by rememberSaveable { mutableIntStateOf(profile.preferences.sessionQuestionCount) }
                    var timezone by rememberSaveable { mutableStateOf(profile.preferences.timezone) }
                    val editable = !busy && cache.pending == null
                    LearnerButton("Language / Sprache: $locale", editable) { locale = if (locale == "en") "de" else "en" }
                    LearnerButton(label("Level", "Niveau") + ": $level", editable) { level = if (level == "B1") "B2" else "B1" }
                    LearnerButton(label("Preferred questions", "Gewünschte Fragen") + ": $count", editable) { count = if (count == 5) 15 else 5 }
                    OutlinedTextField(timezone, { timezone = it }, enabled = editable, label = { Text(label("Timezone (IANA)", "Zeitzone (IANA)")) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp))
                    LearnerButton(label("Save preferences", "Einstellungen speichern"), editable) { run { repository.save(ProfilePreferences(locale, timezone, level, count)) } }
                }
                if (profile.setupCompleted) LearnerButton(label("Back to Home", "Zur Startseite"), !busy) { screen = "home" }
            } else if (profile != null) {
                LearnerButton(label("Home", "Startseite"), !busy) { screen = "home" }
                LearnerButton(label("Progress", "Fortschritt"), !busy) { screen = "progress" }
                LearnerButton(label("Topics", "Themen"), !busy) { screen = "topics" }
                LearnerButton(label("Edit preferences", "Einstellungen ändern"), !busy) { screen = "setup" }
                Text(when(screen) { "progress" -> label("Confirmed Progress", "Bestätigter Fortschritt"); "topics" -> label("Topics", "Themen"); "topic" -> label("Topic detail", "Themendetails"); "target" -> label("Target detail", "Lernzieldetails"); else -> label("Home", "Startseite") }, Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineMedium)
                Text(label("Confirmed snapshot", "Bestätigter Datenstand") + ": " + (cache.generatedAt ?: "—"))
                if (!fresh) Text(label("Saved snapshot; due flags may be outdated. Refresh to update.", "Gespeicherter Datenstand; Fälligkeiten können veraltet sein. Bitte aktualisieren."))
                if (screen == "home") {
                    Text(label("Needs practice", "Übungsbedarf") + ": ${cache.targets.count { it.state == "needs_practice" }}")
                    Text(label("Retention checks", "Behalten überprüfen") + ": ${cache.targets.count { it.isDue && it.state != "needs_practice" }}")
                    if (cache.targets.isEmpty()) Text(label("Let’s find what to practise.", "Finden wir heraus, was du üben kannst."))
                    val available = cache.catalog?.targets?.sumOf { it.availableQuestionCount }
                    Text(if (available == null) label("Availability unknown; refresh.", "Verfügbarkeit unbekannt; bitte aktualisieren.") else if (available == 0) label("No questions available for these preferences.", "Für diese Einstellungen sind keine Fragen verfügbar.") else label("$available draft questions available; preference: ${profile.preferences.sessionQuestionCount}.", "$available Entwurfsfragen verfügbar; Wunsch: ${profile.preferences.sessionQuestionCount}."))
                    LearnerButton(label(if(cache.practice == null) "Start practice" else "Continue practice", if(cache.practice == null) "Übung starten" else "Übung fortsetzen"), !busy && (cache.practice != null || (cache.pending == null && (available ?: 0) > 0))) { screen = "practice"; run(false) { repository.startPractice() } }
                } else if (screen in setOf("topics", "topic", "target")) {
                    NativeTopicsView(cache, screen, detailId, german, busy,
                        open = { destination, id -> screen = destination; detailId = id },
                        resume = { screen = "practice"; run(false) { repository.startPractice() } },
                        start = { focus -> run(false, { screen = "practice" }) { repository.startPractice(focus) } })
                } else {
                    listOf("needs_practice" to label("Needs practice", "Übungsbedarf"), "improving" to label("Improving", "Verbessert"), "mastered" to label("Mastered", "Beherrscht")).forEach { (state, title) ->
                        val targets = cache.targets.filter { it.state == state }
                        Text("$title (${targets.size})", Modifier.semantics { heading() }, style = MaterialTheme.typography.titleLarge)
                        targets.forEach { target ->
                            val metadata = cache.catalog?.targets?.find { it.id == target.targetId }
                            LearnerButton(metadata?.title?.let { if (german) it.de else it.en } ?: target.targetId, !busy) { detailId = target.targetId; screen = "target" }
                            Text(label("Qualifying checks", "Qualifizierte Prüfungen") + ": ${target.qualifyingCheckCount}")
                            target.schedule.firstOrNull()?.let {
                                val date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.of(profile.preferences.timezone)).format(Instant.parse(it.dueAt))
                                Text(label("Review", "Wiederholung") + ": $date (${profile.preferences.timezone})")
                            }
                        }
                    }
                    if (cache.targets.none { it.state in setOf("needs_practice", "improving", "mastered") }) Text(label("No confirmed gains or weaknesses yet.", "Noch keine bestätigten Fortschritte oder Schwächen."))
                }
            }
        }
    }
}

}

@Composable
private fun LearnerButton(text: String, enabled: Boolean, action: () -> Unit) {
    OutlinedButton(onClick = action, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(text) }
}

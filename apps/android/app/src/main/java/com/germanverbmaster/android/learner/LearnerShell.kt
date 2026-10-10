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
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import com.germanverbmaster.android.R
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.foundation.FoundationTheme
import com.germanverbmaster.android.BuildConfig
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LearnerShell(repository: LearnerRepository, onSignIn: (() -> Unit)? = null, localOnly: Boolean = false) {
    key(repository) { AccountLearnerShell(repository,onSignIn,localOnly) }
}

@Composable
private fun AccountLearnerShell(repository: LearnerRepository, onSignIn: (() -> Unit)?, localOnly: Boolean) {
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
            finally { cache = repository.state; if(cache.practice?.let { it.offlinePack == null && it.session != null && it.index == it.session.questions.size } == true) fresh = !failed; busy = false }
        }
    }
    LaunchedEffect(repository) { if (cache.identityDeletion == null && cache.deletion == null && !cache.signedOut) run {} }
    val german = cache.profile?.preferences?.locale == "de"
    fun label(en: String, de: String) = if (german) de else en
    val setup = screen == "setup" || cache.profile?.setupCompleted != true
    if(cache.identityDeletion!=null) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.safeDrawingPadding().imePadding().padding(24.dp).verticalScroll(rememberScrollState())) {
                NativeIdentityDeletionControl(repository,german,false) {cache=repository.state}
            }
        }
        return
    }
    if (cache.deletion != null) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.safeDrawingPadding().padding(24.dp).verticalScroll(rememberScrollState())) {
                NativePrivacyDeletion(repository,german,false) { cache = repository.state }
            }
        }
        return
    }
    if(cache.signedOut) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.safeDrawingPadding().padding(24.dp).verticalScroll(rememberScrollState())) {
                onSignIn?.let { action -> LearnerButton(providerCopy(if(german) "de" else "en").signIn,!busy,action) }
                NativePrivacySignOut(repository,german,false) { cache = repository.state }
            }
        }
        return
    }
    val design = studyCopy(german)
    val focused = screen == "practice" && cache.practice != null
    Scaffold(containerColor = MaterialTheme.colorScheme.background, bottomBar = {
        if (!focused && !setup && cache.profile != null) NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
            listOf(Triple("home", R.string.study_home, Icons.Outlined.Home),
                Triple("progress", R.string.study_progress, Icons.AutoMirrored.Outlined.TrendingUp),
                Triple("topics", R.string.study_topics, Icons.AutoMirrored.Outlined.MenuBook)).forEach { (destination, title, icon) ->
                NavigationBarItem(selected = screen == destination || destination == "topics" && screen in setOf("topic", "target"),
                    onClick = { screen = destination }, enabled = !busy,
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    icon = { Icon(icon, contentDescription = null) }, label = { Text(design(title)) })
            }
        }
    }) { insets ->
        Column(Modifier.padding(insets).imePadding().widthIn(max = 720.dp).fillMaxWidth()
            .verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (!focused) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("German Master", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                TextButton(onClick = { screen = "account" }, enabled = !busy) { Text(design(R.string.study_account)) }
            }
            if(screen == "account") {
                val authCopy=providerCopy(if(german) "de" else "en")
                if(localOnly) Text(authCopy.local)
                onSignIn?.let { action -> LearnerButton(authCopy.signIn,!busy,action) }
            }
            if(!repository.authenticatedAccount && screen == "account") Text(label("Unpublished fixture · shared local account", "Unveröffentlichte Beispieldaten · gemeinsames lokales Konto"))
            if (busy) Text(label("Loading…", "Wird geladen…"), Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            if (localOnly && screen != "account") {
                val authCopy = providerCopy(if (german) "de" else "en")
                Text(authCopy.local, color = MaterialTheme.colorScheme.onSurfaceVariant)
                onSignIn?.let { signIn -> TextButton(onClick = signIn, enabled = !busy) { Text(authCopy.signIn) } }
            }
            if (cache.contentReport != null && !cache.reportRecorded) {
                Text(label("Exercise report saved; awaiting confirmation.", "Übungsmeldung gespeichert; Bestätigung ausstehend."), Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                LearnerButton(label("Retry saved report", "Gespeicherte Meldung erneut senden"), !busy) { run(false) { repository.reportProblem(requireNotNull(cache.contentReport).category) } }
            }
            if (failed) Text(label("Could not refresh or save. Saved work remains available. Retry the saved operation.", "Aktualisieren oder Speichern fehlgeschlagen. Gespeicherte Daten bleiben erhalten. Gespeicherten Vorgang erneut versuchen."), Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            if (screen == "practice" && cache.practice != null) {
                NativePracticeView(requireNotNull(cache.practice), german, busy, { operation -> run(false) { operation(); if(repository.state.practice?.let { it.offlinePack == null && it.session != null && it.index == it.session.questions.size } == true) repository.refresh() } }, repository, { operation -> try { operation() } catch (_: Exception) { failed = true }; cache = repository.state }, openProgress = { screen = "progress"; run {} }) { screen = "home" }
            } else {
            if (cache.pending != null) {
                Text(label("Your preferences are saved here and still need to sync. Your progress shows previously saved results.", "Deine Einstellungen sind hier gespeichert und müssen noch synchronisiert werden. Dein Lernstand zeigt die bisher gespeicherten Ergebnisse."))
                LearnerButton(label("Retry saved preferences", "Gespeicherte Einstellungen erneut senden"), !busy) { run { repository.retry() } }
                LearnerButton(label("Reload current preferences", "Aktuelle Einstellungen laden"), !busy) { run { repository.reloadProfile() } }
            }
            if (screen == "account") {
                LearnerButton(label("Refresh", "Aktualisieren"), !busy) { run {} }
                LearnerButton(label("Sync all saved work", "Alle gespeicherten Vorgänge synchronisieren"), !busy) { run { repository.syncSavedWork() } }
            }
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
                    OutlinedTextField(timezone, { timezone = it }, enabled = editable, singleLine = true, label = { Text(label("Timezone (IANA)", "Zeitzone (IANA)")) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp))
                    LearnerButton(label("Save preferences", "Einstellungen speichern"), editable) { run { repository.save(ProfilePreferences(locale, timezone, level, count)) } }
                }
                if (profile.setupCompleted) LearnerButton(label("Back to Home", "Zur Startseite"), !busy) { screen = "home" }
            } else if (profile != null) {
                if (screen != "home") {
                    Text(when(screen) { "account" -> design(R.string.study_account); "progress" -> label("Your progress", "Dein Lernstand"); "topics" -> label("Topics", "Themen"); "topic" -> label("Topic detail", "Themendetails"); else -> label("Skill detail", "Fähigkeit im Detail") }, Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineMedium)
                }
                if (screen == "progress") {                    if (!fresh) Text(label("Showing your last saved progress. Refresh when online.", "Dein zuletzt gespeicherter Lernstand wird angezeigt. Aktualisiere ihn, wenn du online bist."))
                    LearnerButton(label("Refresh", "Aktualisieren"), !busy) { run {} }
                }
                if (screen == "home") {
                    StudyHome(cache, german, busy, fresh,
                        start = { screen = "practice"; run(false) { repository.startPractice() } },
                        preferences = { screen = "setup" }, refresh = { run {} },
                        openTopic = { detailId = it; screen = "topic" }, openTarget = { detailId = it; screen = "target" })
                } else if (screen == "account") {
                    LearnerButton(design(R.string.study_preferences), !busy) { screen = "setup" }
                    NativePrivacyExport(repository, german, busy) { cache = repository.state }
                    if(!repository.authenticatedAccount) NativePrivacyDeletion(repository, german, busy) { cache = repository.state }
                    if(repository.identityDeletionEnabled) NativeIdentityDeletionControl(repository,german,busy) {cache=repository.state}
                    NativePrivacySignOut(repository, german, busy) { cache = repository.state }
                    val available = cache.catalog?.targets?.sumOf { it.availableQuestionCount }
                    Text(label("Downloaded practice", "Heruntergeladene Übungen"), Modifier.semantics { heading() })
                    val pack = cache.preparedPack
                    val prepared = pack?.sessions?.count { it.id !in cache.consumedPreparedSessions } ?: 0
                    val valid = pack?.let { com.germanverbmaster.android.foundation.PreparedPackReader.canStart(it,java.time.Instant.now()) } == true
                    Text(label("Sessions available to start", "Sitzungen zum Starten verfügbar") + ": ${if(valid) prepared else 0}")
                    LearnerButton(if(cache.packRequest == null) label("Download two sessions", "Zwei Sitzungen herunterladen") else label("Retry saved download", "Gespeicherten Download wiederholen"), !busy && (cache.packRequest != null || cache.pending == null && (available ?: 0) > 0)) { run(false) { repository.prepareReserve() } }
                    LearnerButton(label("Start downloaded practice", "Heruntergeladene Übungen starten"), !busy && cache.practice == null && valid && prepared > 0) { run(false, {screen = "practice"}) { repository.startOffline() } }
                    cache.completedOffline.forEachIndexed { index, p ->
                        Text(label("Saved session", "Gespeicherte Sitzung") + " ${index + 1}: ${p.outbox.count { !it.delivered }} " + label("waiting to sync", "noch zu synchronisieren"))
                        LearnerButton(label("Sync saved session", "Gespeicherte Sitzung synchronisieren") + " ${index + 1}", !busy && p.outbox.any { !it.delivered }) { run { repository.syncSavedWork() } }
                        p.outbox.forEach { event ->
                            val result = when(val receipt = event.attemptReceipt) { is AttemptAcknowledgment -> receipt.evaluation; is AttemptDuplicate -> receipt.evaluation; else -> null }
                            if(result != null && result.outcome != event.provisional?.outcome) Text(label("Updated result: ", "Aktualisiertes Ergebnis: ") + if(german) result.explanation.de else result.explanation.en)
                            if(event.attemptReceipt is AttemptRejection || event.exposureReceipt is ExposureRejected) Text(label("An answer could not be saved to your account. Sync has stopped; your work is still on this device.", "Eine Antwort konnte nicht in deinem Konto gespeichert werden. Die Synchronisierung wurde angehalten; deine Übungen bleiben auf diesem Gerät."))
                        }
                    }
                } else if (screen in setOf("topics", "topic", "target")) {
                    NativeTopicsView(cache, screen, detailId, german, busy,
                        open = { destination, id -> screen = destination; detailId = id },
                        resume = { screen = "practice"; run(false) { repository.startPractice() } },
                        start = { focus -> run(false, { screen = "practice" }) { repository.startPractice(focus) } })
                } else {
                    listOf("needs_practice" to label("Needs practice", "Braucht Übung"), "improving" to label("Improving", "Wird sicherer"), "mastered" to label("Mastered", "Sicher")).forEach { (state, title) ->
                        val targets = cache.targets.filter { it.state == state }
                        Text("$title (${targets.size})", Modifier.semantics { heading() }, style = MaterialTheme.typography.titleLarge)
                        targets.forEach { target ->
                            val metadata = cache.catalog?.targets?.find { it.id == target.targetId }
                            LearnerButton(metadata?.title?.let { if (german) it.de else it.en } ?: target.targetId, !busy) { detailId = target.targetId; screen = "target" }
                            Text(label("Review checks", "Wiederholungsprüfungen") + ": ${target.qualifyingCheckCount}")
                            target.schedule.firstOrNull()?.let {
                                val date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.of(profile.preferences.timezone)).format(Instant.parse(it.dueAt))
                                Text(label("Review", "Wiederholung") + ": $date (${profile.preferences.timezone})")
                            }
                        }
                    }
                    if (cache.targets.none { it.state in setOf("needs_practice", "improving", "mastered") }) Text(label("Complete some practice to see what to review next.", "Übe zuerst, um zu sehen, was du als Nächstes wiederholen kannst."))
                }
            }
        }
    }
}

}

@Composable
internal fun LearnerButton(text: String, enabled: Boolean, action: () -> Unit) {
    OutlinedButton(onClick = action, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(text) }
}

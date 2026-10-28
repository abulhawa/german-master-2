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
internal fun SettingsSection(title: String, german: Boolean, content: @Composable ColumnScope.() -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).semantics {
            stateDescription = if (expanded) { if (german) "Geöffnet" else "Expanded" } else { if (german) "Geschlossen" else "Collapsed" }
        }) { Text(title, Modifier.weight(1f)); Text(if (expanded) "−" else "+", Modifier.clearAndSetSemantics {}) }
        if (expanded) content()
    }
}

@Composable
internal fun AccountDeletionRequest(german: Boolean) {
    val context = androidx.compose.ui.platform.LocalContext.current
    TextButton(onClick = { context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW,
        android.net.Uri.parse("https://germanmaster.qortxai.com/delete-account"))) }) {
        Text(if(german) "Kontolöschung anfordern" else "Request account deletion")
    }
}

@Composable
fun LearnerShell(repository: LearnerRepository, onSignIn: (() -> Unit)? = null, localOnly: Boolean = false, accountEmail: () -> String? = { null }) {
    key(repository) { AccountLearnerShell(repository,onSignIn,localOnly,accountEmail) }
}

@Composable
private fun AccountLearnerShell(repository: LearnerRepository, onSignIn: (() -> Unit)?, localOnly: Boolean, accountEmail: () -> String?) {
    var cache by remember { mutableStateOf(repository.state) }
    var busy by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var fresh by remember { mutableStateOf(false) }
    var screen by rememberSaveable { mutableStateOf("home") }
    var detailId by rememberSaveable { mutableStateOf("") }
    val pageScroll = rememberScrollState()
    LaunchedEffect(screen, detailId) { pageScroll.scrollTo(0) }
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
    Scaffold(modifier=Modifier.fillMaxSize(),contentWindowInsets=WindowInsets.safeDrawing,containerColor = MaterialTheme.colorScheme.background, bottomBar = {
        if (!focused && !setup && cache.profile != null) {
            LearnerNavigation(screen,german,!busy) { screen=it }
        }
    }) { insets ->
        Column(Modifier.padding(insets).imePadding().widthIn(max = 720.dp).fillMaxWidth()
            .verticalScroll(pageScroll).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (!focused) StudyHeader(german, { screen = "account" }, enabled = !busy, showGrammar = screen != "account")
            else GrammarReferenceButton(german,!busy)
            if(screen == "account") {
                val authCopy=providerCopy(if(german) "de" else "en")
                if(localOnly) Text(authCopy.local)
                if(localOnly) onSignIn?.let { action -> LearnerButton(authCopy.signIn,!busy,action) }
            }
            if(!repository.authenticatedAccount && screen == "account") Text(label("Local preview only. This practice is not linked to a sign-in account.", "Nur lokale Vorschau. Diese Ãœbungen sind mit keinem Anmeldekonto verbunden."))
            if (busy) Text(label("Loadingâ€¦", "Wird geladenâ€¦"), Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            if (localOnly && screen != "account") {
                val authCopy = providerCopy(if (german) "de" else "en")
                Text(authCopy.local, color = MaterialTheme.colorScheme.onSurfaceVariant)
                onSignIn?.let { signIn -> TextButton(onClick = signIn, enabled = !busy) { Text(authCopy.signIn) } }
            }
            if (cache.contentReport != null && !cache.reportRecorded) {
                Text(label("Exercise report saved; awaiting confirmation.", "Ãœbungsmeldung gespeichert; BestÃ¤tigung ausstehend."), Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                LearnerButton(label("Retry saved report", "Gespeicherte Meldung erneut senden"), !busy) { run(false) { repository.reportProblem(requireNotNull(cache.contentReport).category) } }
            }
            if (failed) Text(label("Could not refresh or save. Saved work remains available. Retry the saved operation.", "Aktualisieren oder Speichern fehlgeschlagen. Gespeicherte Daten bleiben erhalten. Gespeicherten Vorgang erneut versuchen."), Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            if (screen == "practice" && cache.practice != null) {
                NativePracticeView(requireNotNull(cache.practice), german, busy, { operation -> run(false) { operation(); if(repository.state.practice?.let { it.offlinePack == null && it.session != null && it.index == it.session.questions.size } == true) repository.refresh() } }, repository, { operation -> try { operation() } catch (_: Exception) { failed = true }; cache = repository.state }, openProgress = { screen = "progress"; run {} }) { screen = "home" }
            } else {
            if (cache.pending != null) {
                Text(label("Your preferences are saved here and still need to sync. Your progress shows previously saved results.", "Deine Einstellungen sind hier gespeichert und mÃ¼ssen noch synchronisiert werden. Dein Lernstand zeigt die bisher gespeicherten Ergebnisse."))
                LearnerButton(label("Retry saved preferences", "Gespeicherte Einstellungen erneut senden"), !busy) { run { repository.retry() } }
                LearnerButton(label("Reload current preferences", "Aktuelle Einstellungen laden"), !busy) { run { repository.reloadProfile() } }
            }
            if (screen == "account") {
                Text(label("Settings", "Einstellungen"), Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineMedium)
                StudyPanel {
                Text(label("Account", "Konto"), Modifier.semantics { heading() }, style = MaterialTheme.typography.titleLarge)
                val address = if (localOnly) null else accountEmail()
                if (address != null) {
                    Text(label("Email", "E-Mail"), style = MaterialTheme.typography.labelLarge)
                    androidx.compose.foundation.text.selection.SelectionContainer { Text(address) }
                } else Text(label("Reconnect your account to view your email.", "Verbinde dein Konto erneut, um deine E-Mail zu sehen."))
                SettingsSection(label("Sign out", "Abmelden"), german) {
                    NativePrivacySignOut(repository, german, busy) { cache = repository.state }
                }
                AccountDeletionRequest(german)
                if(repository.identityDeletionEnabled) SettingsSection(label("Delete account", "Konto löschen"), german) {
                    NativeIdentityDeletionControl(repository,german,busy) {cache=repository.state}
                }
                }
            }
            val profile = cache.profile
            if (profile != null && setup) {
                Text(label("Setup and preferences", "Einrichtung und Einstellungen"), Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineMedium)
                key(profile.revision, cache.pending) {
                    var locale by rememberSaveable { mutableStateOf(profile.preferences.locale) }
                    var level by rememberSaveable { mutableStateOf(profile.preferences.level) }
                    var learningContext by rememberSaveable { mutableStateOf(profile.preferences.learningContext ?: "general") }
                    var timezone by rememberSaveable { mutableStateOf(profile.preferences.timezone) }
                    val editable = !busy && cache.pending == null
                    LearnerButton("Language / Sprache: $locale", editable) { locale = if (locale == "en") "de" else "en" }
                    LearnerButton(label("Level", "Niveau") + ": $level", editable) { level = if (level == "B1") "B2" else "B1" }
                    if(NativeReleaseCapabilities.workplacePractice) LearnerButton(label("Practice", "Übung") + ": " + if(learningContext=="work")label("German for work","Deutsch im Beruf")else label("General German","Allgemeines Deutsch"), editable) {learningContext=if(learningContext=="general")"work" else "general"}
                    LearnerButton(label("Save preferences", "Einstellungen speichern"), editable) { run { repository.save(ProfilePreferences(locale, timezone, level, 5, learningContext)) } }
                }
                if (profile.setupCompleted) LearnerButton(label("Back to Home", "Zur Startseite"), !busy) { screen = "home" }
            } else if (profile != null) {
                if (screen != "home" && screen != "account") {
                    Text(when(screen) { "account" -> label("Settings", "Einstellungen"); "progress" -> label("Your progress", "Dein Lernstand"); "topics" -> label("Topics", "Themen"); "topic" -> label("Topic detail", "Themendetails"); else -> label("Skill detail", "FÃ¤higkeit im Detail") }, Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineMedium)
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
                    SettingsSection(label("Language and appearance", "Sprache und Darstellung"), german) {
                        StudyAppearanceSettings(german, { run { repository.save(profile.preferences.copy(locale=if(german) "en" else "de")) } }, !busy && cache.pending==null)
                    }
                    LearnerButton(design(R.string.study_preferences), !busy) { screen = "setup" }
                } else if (screen in setOf("topics", "topic", "target")) {
                    NativeTopicsView(cache, screen, detailId, german, busy,
                        open = { destination, id -> screen = destination; detailId = id },
                        resume = { screen = "practice"; run(false) { repository.startPractice() } },
                        start = { focus -> run(false, { screen = "practice" }) { repository.startPractice(focus) } })
                } else {
                    listOf("needs_practice" to label("Needs practice", "Braucht Ãœbung"), "improving" to label("Improving", "Wird sicherer"), "mastered" to label("Mastered", "Sicher")).forEach { (state, title) ->
                        val targets = cache.targets.filter { it.state == state }
                        if(targets.isNotEmpty()) Text("$title (${targets.size})", Modifier.semantics { heading() }, style = MaterialTheme.typography.titleLarge)
                        targets.forEach { target ->
                            val metadata = cache.catalog?.targets?.find { it.id == target.targetId }
                            LearnerButton(metadata?.title?.let { if (german) it.de else it.en } ?: target.targetId, !busy) { detailId = target.targetId; screen = "target" }
                            Text(label("Review checks", "WiederholungsprÃ¼fungen") + ": ${target.qualifyingCheckCount}")
                            target.schedule.firstOrNull()?.let {
                                val date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.of(profile.preferences.timezone)).format(Instant.parse(it.dueAt))
                                Text(label("Review", "Wiederholung") + ": $date (${profile.preferences.timezone})")
                            }
                        }
                    }
                    if (cache.targets.none { it.state in setOf("needs_practice", "improving", "mastered") }) StudyPanel {
                        Text(label("Your progress starts with practice", "Dein Fortschritt beginnt mit dem Ãœben"),style=MaterialTheme.typography.titleLarge)
                        Text(label("Complete some practice to see what to review next.", "Ãœbe zuerst, um zu sehen, was du als NÃ¤chstes wiederholen kannst."),color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    StudyPrimary(design(if(cache.practice!=null) R.string.study_continue else R.string.study_start),
                        !busy && (cache.practice!=null || cache.pending==null && (cache.catalog?.targets?.sumOf {it.availableQuestionCount} ?: 0)>0)) {
                        screen="practice";run(false) {repository.startPractice()}
                    }
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

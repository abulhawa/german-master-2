package com.germanverbmaster.android.learner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.*
import java.io.File

/** Configured preview host; no legacy AuthRepository, raw-table client or background worker. */
@Composable
fun NativeProviderHost(project: String, publishableKey: String, origin: String, directory: File) {
    val context = LocalContext.current
    val client = remember(project,publishableKey) { createLearnerAuthClient(context,project,publishableKey) }
    val subjectStore = remember(project) { AtomicVerifiedSubjectStore(File(context.noBackupFilesDir,"gm-v2-last-verified-$project")) }
    val provider = remember(client) { VerifiedLearnerProvider(SupabaseLearnerAuth(client),project,subjectStore) }
    val status by client.auth.sessionStatus.collectAsState()
    val scope = rememberCoroutineScope()
    DisposableEffect(client) { onDispose { provider.invalidate();CoroutineScope(Dispatchers.IO).launch { client.close() } } }
    var showLogin by remember { mutableStateOf(false) }
    var german by remember { mutableStateOf(false) }
    var localOnly by remember { mutableStateOf(runCatching { subjectStore.read() != null }.getOrDefault(false)) }
    LaunchedEffect(status) { if(status is SessionStatus.Authenticated) { localOnly=false;showLogin=false } }
    if(!showLogin && (localOnly || status is SessionStatus.Authenticated)) {
        // Each provider session-state replacement keys a new bound learner tree.
        key(status,localOnly) { ProviderLearnerShell(provider,origin,directory,0,localOnly,
            locale=if(german) "de" else "en",
            onLocal=if(!localOnly && runCatching { subjectStore.read()!=null }.getOrDefault(false)) ({localOnly=true}) else null,
            onSignIn={ showLogin=true }) }
        return
    }
    LaunchedEffect(status) { provider.invalidate() }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    fun label(en: String,de: String) = if(german) de else en
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.safeDrawingPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
            Text(label("Sign in to German Master","Bei German Master anmelden"),Modifier.semantics { heading() },style=MaterialTheme.typography.headlineMedium)
            LearnerButton("English / Deutsch",!busy) { german=!german }
            if(runCatching { subjectStore.read() != null }.getOrDefault(false)) {
                LearnerButton(label("Continue saved practice","Gespeicherte Übungen fortsetzen"),!busy) { localOnly=true;showLogin=false }
            }
            OutlinedTextField(email,{email=it},label={Text(label("Email","E-Mail"))},singleLine=true,enabled=!busy,modifier=Modifier.fillMaxWidth())
            OutlinedTextField(password,{password=it},label={Text(label("Password","Passwort"))},visualTransformation=PasswordVisualTransformation(),singleLine=true,enabled=!busy,modifier=Modifier.fillMaxWidth())
            LearnerButton(label("Sign in","Anmelden"),!busy&&email.isNotBlank()&&password.isNotEmpty()) {
                if(!busy) {
                    busy=true;failed=false
                    val signInEmail=email; val signInPassword=password
                    scope.launch {
                        try { client.auth.signInWith(Email) { this.email=signInEmail;this.password=signInPassword } }
                        catch(cancel:CancellationException) {throw cancel}
                        catch(_:Exception) {failed=true}
                        finally {password="";busy=false}
                    }
                }
            }
            if(busy) Text(label("Verifying your account…","Dein Konto wird geprüft…"),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
            if(failed) Text(label("Sign-in could not finish. Saved learner work remains on this device.","Anmeldung nicht abgeschlossen. Gespeicherte Lerndaten bleiben auf diesem Gerät."),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
        }
    }
}

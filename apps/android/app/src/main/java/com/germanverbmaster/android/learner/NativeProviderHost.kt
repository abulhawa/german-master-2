package com.germanverbmaster.android.learner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.Azure
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.*
import java.io.File

/** Dedicated v2 host; no legacy AuthRepository, raw-table client or background worker. */
@Composable
fun NativeProviderHost(project: String, publishableKey: String, origin: String, directory: File, authCallback: android.content.Intent? = null) {
    // Unconfigured debug and preview builds never fall back to the old UI or crash.
    if (project.isBlank() || publishableKey.isBlank() || origin.isBlank()) {
        Surface(Modifier.fillMaxSize()) {
            Text("German Master 2.0 needs public Auth and API configuration.", Modifier.safeDrawingPadding().padding(24.dp))
        }
        return
    }
    val context = LocalContext.current
    val client = remember(project,publishableKey) { createLearnerAuthClient(context,project,publishableKey) }
    val oauthClient = remember(project,publishableKey) { createLearnerAuthClient(context,project,publishableKey,oauthOnly=true) }
    val subjectStore = remember(project) { AtomicVerifiedSubjectStore(File(context.noBackupFilesDir,"gm-v2-last-verified-$project")) }
    val provider = remember(client) { VerifiedLearnerProvider(SupabaseLearnerAuth(client),project,subjectStore) }
    val status by client.auth.sessionStatus.collectAsState()
    DisposableEffect(client) { onDispose { provider.invalidate();CoroutineScope(Dispatchers.IO).launch { client.close();oauthClient.close() } } }
    var showLogin by remember { mutableStateOf(false) }
    var accountMode by remember { mutableStateOf<String?>(null) }
    val appearancePrefs = remember(context) { context.getSharedPreferences("gm-v2-appearance", android.content.Context.MODE_PRIVATE) }
    var german by remember { mutableStateOf(appearancePrefs.getBoolean("german",false)) }
    fun changeLanguage() { german=!german;appearancePrefs.edit().putBoolean("german",german).apply() }
    var localOnly by remember { mutableStateOf(runCatching { subjectStore.read() != null }.getOrDefault(false)) }
    LaunchedEffect(status) { if(status is SessionStatus.Authenticated) { localOnly=false;showLogin=false;accountMode=null } }
    var callbackFailed by remember { mutableStateOf(false) }
    LaunchedEffect(authCallback) {
        val uri=authCallback?.data ?: return@LaunchedEffect
        if(!isLearnerOAuthCallback(uri,context.packageName))return@LaunchedEffect
        try {
            client.auth.awaitInitialization()
            val code=learnerOAuthCode(uri,context.packageName)
            val session=oauthClient.auth.exchangeCodeForSession(code,saveSession=false)
            client.auth.importSession(session)
        } catch(cancel:CancellationException){throw cancel}
        catch(_:Exception){callbackFailed=true;accountMode="sign-in";showLogin=true}
    }
    if(callbackFailed) {
        AlertDialog(onDismissRequest={callbackFailed=false},
            title={Text(if(german) "Anmeldung nicht abgeschlossen" else "Sign-in not completed")},
            text={Text(if(german) "Versuche es erneut. Deine Übungen bleiben gespeichert." else "Try again. Your saved practice is preserved.")},
            confirmButton={TextButton(onClick={callbackFailed=false}){Text("OK")}})
    }
    if(!showLogin && (localOnly || status is SessionStatus.Authenticated)) {
        // Each provider session-state replacement keys a new bound learner tree.
        key(status,localOnly) { ProviderLearnerShell(provider,origin,directory,0,localOnly,
            locale=if(german) "de" else "en",
            onLocal=if(!localOnly && runCatching { subjectStore.read()!=null }.getOrDefault(false)) ({localOnly=true}) else null,
            onSignIn={ accountMode="sign-in";showLogin=true }) }
        return
    }
    LaunchedEffect(status) { provider.invalidate() }
    val mode = accountMode
    if(mode == null) {
        NativeGuestEntry(File(context.noBackupFilesDir,"gm-v2-guest"), german,
            {changeLanguage()}, {accountMode=it},
            if(runCatching {subjectStore.read()!=null}.getOrDefault(false)) ({localOnly=true;showLogin=false}) else null)
    } else {
        NativeAccountEntry(mode,german,{changeLanguage()},{accountMode=it},socialSignIn={ name ->
            client.auth.awaitInitialization()
            val external=when(name){"google"->Google;"azure"->Azure;else->error("Unknown sign-in provider")}
            val url=oauthClient.auth.getOAuthUrl(external,learnerOAuthRedirect(context.packageName)) {
                if(name=="azure")scopes.add("email")
            }
            context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW,android.net.Uri.parse(url)))
        }) { action,email,password ->
            // The accepted web email callback belongs to this production Auth project only.
            // The recovered production web callbacks were accepted on the clean replacement.
            require(action == "sign-in" || project == "sqgjsmiaprsuilcjmaav")
            when(action) {
                "sign-in" -> client.auth.signInWith(Email) {this.email=email;this.password=password}
                "register" -> client.auth.signUpWith(Email, redirectUrl="https://germanmaster.qortxai.com/") {this.email=email;this.password=password}
                "reset" -> client.auth.resetPasswordForEmail(email, redirectUrl="https://germanmaster.qortxai.com/?auth=recovery")
            }
        }
    }
}

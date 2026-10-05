package com.germanverbmaster.android.learner

import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Host changes authGeneration on sign-in/out/account replacement. A new key removes
 * the previous subject's Compose tree before binding or loading the next cache. */
@Composable
fun ProviderLearnerShell(provider: VerifiedLearnerProvider, origin: String, directory: File, authGeneration: Long) {
    key(provider, authGeneration) {
        var retry by remember { mutableIntStateOf(0) }
        val loaded by produceState<Result<LearnerRepository>?>(null, retry) {
            value = null
            value = withContext(Dispatchers.IO) { runCatching { provider.repository(directory,origin) } }
        }
        DisposableEffect(provider) { onDispose { provider.invalidate() } }
        loaded?.fold(onSuccess = { LearnerShell(it) },onFailure = {
            Column(Modifier.safeDrawingPadding().padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
                Text("Sign in to this account to continue. Saved learner work remains on this device. / Melde dich bei diesem Konto an, um fortzufahren. Gespeicherte Lerndaten bleiben auf diesem Gerät.")
                LearnerButton("Retry account verification / Kontoprüfung wiederholen",true) { retry++ }
            }
        }) ?: Text("Verifying account… / Konto wird geprüft…",Modifier.safeDrawingPadding().padding(24.dp))
    }
}

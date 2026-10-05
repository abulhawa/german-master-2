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
fun ProviderLearnerShell(provider: VerifiedLearnerProvider, origin: String, directory: File, authGeneration: Long, localOnly: Boolean = false, locale: String = "en", onLocal: (() -> Unit)? = null, onSignIn: (() -> Unit)? = null) {
    key(provider, authGeneration) {
        var retry by remember { mutableIntStateOf(0) }
        val loaded by produceState<Result<LearnerRepository>?>(null, retry) {
            value = null
            value = withContext(Dispatchers.IO) { runCatching { provider.repository(directory,origin,localOnly) } }
        }
        DisposableEffect(provider) { onDispose { provider.invalidate() } }
        val c=providerCopy(loaded?.getOrNull()?.state?.profile?.preferences?.locale ?: locale)
        loaded?.fold(onSuccess = { LearnerShell(it,onSignIn,localOnly) },onFailure = {
            Column(Modifier.safeDrawingPadding().padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
                Text(c.failed)
                LearnerButton(c.retry,true) { retry++ }
                onLocal?.let { action -> LearnerButton(c.resume,true,action) }
                onSignIn?.let { action -> LearnerButton(c.signIn,true,action) }
            }
        }) ?: Text(c.checking,Modifier.safeDrawingPadding().padding(24.dp))
    }
}

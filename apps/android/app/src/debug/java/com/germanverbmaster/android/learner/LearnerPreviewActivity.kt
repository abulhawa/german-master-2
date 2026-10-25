package com.germanverbmaster.android.learner

import android.os.Bundle
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.germanverbmaster.android.BuildConfig
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LearnerPreviewActivity : ComponentActivity() {
    private var authCallback by mutableStateOf<Intent?>(null)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        authCallback=intent
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authCallback=intent
        enableEdgeToEdge()
        setContent { FoundationTheme {
            if(BuildConfig.V2_AUTH_PROJECT.isNotEmpty()) {
                NativeProviderHost(BuildConfig.V2_AUTH_PROJECT,BuildConfig.V2_AUTH_PUBLISHABLE_KEY,BuildConfig.V2_API_ORIGIN,filesDir,authCallback)
                return@FoundationTheme
            }
            val loaded by produceState<Result<LearnerRepository>?>(null) {
                value = withContext(Dispatchers.IO) { runCatching {
                    val identity = LearnerIdentity(FIXTURE_SUBJECT, 0)
                    val account = LearnerAccount(identity) { identity }
                    LearnerRepository(LocalLearnerApi(expectedSubject = identity.subject), account.store(filesDir), account)
                } }
            }
            loaded?.fold(onSuccess = { LearnerShell(it) }, onFailure = {
                Surface { Text("Saved local preview data cannot be read. It has been preserved. / Gespeicherte Vorschaudaten sind nicht lesbar und bleiben erhalten.", Modifier.safeDrawingPadding().padding(24.dp)) }
            })
        } }
    }
}

package com.germanverbmaster.android

import android.os.Bundle
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.germanverbmaster.android.foundation.FoundationTheme
import com.germanverbmaster.android.learner.NativeProviderHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var authCallback by mutableStateOf<Intent?>(null)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        authCallback=intent
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authCallback=intent
        enableEdgeToEdge()
        setContent {
            FoundationTheme {
                NativeProviderHost(
                    BuildConfig.V2_AUTH_PROJECT,
                    BuildConfig.V2_AUTH_PUBLISHABLE_KEY,
                    BuildConfig.V2_API_ORIGIN,
                    noBackupFilesDir,
                    authCallback,
                )
            }
        }
    }
}

package com.germanverbmaster.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.germanverbmaster.android.foundation.FoundationTheme
import com.germanverbmaster.android.learner.NativeProviderHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoundationTheme {
                NativeProviderHost(
                    BuildConfig.V2_AUTH_PROJECT,
                    BuildConfig.V2_AUTH_PUBLISHABLE_KEY,
                    BuildConfig.V2_API_ORIGIN,
                    noBackupFilesDir,
                )
            }
        }
    }
}

package com.germanverbmaster.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.germanverbmaster.android.domain.model.AppTheme
import com.germanverbmaster.android.navigation.AppNavGraph
import com.germanverbmaster.android.ui.theme.GermanVerbMasterTheme
import com.germanverbmaster.android.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if(BuildConfig.V2_ENABLED) {
            setContent { com.germanverbmaster.android.foundation.FoundationTheme {
                com.germanverbmaster.android.learner.NativeProviderHost(BuildConfig.V2_AUTH_PROJECT,BuildConfig.V2_AUTH_PUBLISHABLE_KEY,BuildConfig.V2_API_ORIGIN,noBackupFilesDir)
            } }
            return
        }
        setContent {
            val themeMode by themeViewModel.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            GermanVerbMasterTheme(darkTheme = darkTheme) {
                AppNavGraph()
            }
        }
    }
}

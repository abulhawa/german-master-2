package com.germanverbmaster.android.foundation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text

// Debug-only entry point. Backend mode uses loopback fixture auth; no release navigation.
class FoundationPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (intent.getBooleanExtra("backend", false)) {
            val api = LocalFoundationApi()
            setContent { FoundationTheme { FoundationBackendPreview(api) } }
            return
        }
        val result = runCatching { assets.open(if(intent.getBooleanExtra("formats", false)) "practice-formats-session.json" else "session.json").bufferedReader().use { ContractReader.session(it.readText()) } }
        setContent { FoundationTheme {
            result.fold(onSuccess={ FoundationPreview(it) }, onFailure={ Text(getString(com.germanverbmaster.android.R.string.foundation_unsupported)) })
        } }
    }
}

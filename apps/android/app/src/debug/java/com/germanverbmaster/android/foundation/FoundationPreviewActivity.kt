package com.germanverbmaster.android.foundation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text

// Debug-only entry point: no authentication, grading, progress or release navigation.
class FoundationPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val result = runCatching { assets.open("session.json").bufferedReader().use { ContractReader.session(it.readText()) } }
        setContent { FoundationTheme {
            result.fold(onSuccess={ FoundationPreview(it) }, onFailure={ Text(getString(com.germanverbmaster.android.R.string.foundation_unsupported)) })
        } }
    }
}

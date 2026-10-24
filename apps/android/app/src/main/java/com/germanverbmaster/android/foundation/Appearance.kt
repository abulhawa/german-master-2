package com.germanverbmaster.android.foundation

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

val LocalFoundationDark = staticCompositionLocalOf { false }

/** Device appearance is available before authentication and never contains learner data. */
@Composable
fun rememberAppearance(): Pair<String, (String) -> Unit> {
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("gm-v2-appearance", Context.MODE_PRIVATE) }
    var theme by remember(prefs) { mutableStateOf(prefs.getString("theme", "system") ?: "system") }
    DisposableEffect(prefs) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if(key == "theme") theme = prefs.getString("theme", "system") ?: "system"
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    return theme to { value ->
        require(value in setOf("system", "light", "dark"))
        prefs.edit().putString("theme", value).apply()
    }
}

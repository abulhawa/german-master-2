package com.germanverbmaster.android.learner

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.germanverbmaster.android.R

/**
 * Original four-destination Android bottom-bar pattern, backed exclusively by
 * v2 learner routes. The retired vocabulary/exam repositories are never opened.
 * Home is the Practice destination, not a second guest-mode entry point.
 */
@Composable
internal fun LearnerNavigation(screen: String, german: Boolean, enabled: Boolean = true, navigate: (String) -> Unit) {
    val labels = studyCopy(german)
    val items = listOf(
        Triple("topics", R.string.study_topics, Icons.AutoMirrored.Outlined.MenuBook),
        Triple("progress", R.string.study_progress, Icons.AutoMirrored.Outlined.TrendingUp),
        Triple("home", R.string.nav_practice, Icons.Filled.School),
        Triple("account", R.string.nav_account, Icons.Filled.AccountCircle)
    )
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { (destination, title, icon) ->
            val selected = when (destination) {
                "topics" -> screen in setOf("topics", "topic", "target")
                "account" -> screen in setOf("account", "setup", "downloads")
                else -> screen == destination
            }
            NavigationBarItem(
                selected = selected,
                onClick = { navigate(destination) },
                enabled = enabled,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                icon = { Icon(icon, contentDescription = null) },
                label = { Text(labels(title)) }
            )
        }
    }
}

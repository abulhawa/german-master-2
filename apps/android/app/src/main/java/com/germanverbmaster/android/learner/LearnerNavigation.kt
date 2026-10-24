package com.germanverbmaster.android.learner

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.germanverbmaster.android.R

/** Same primary destinations whether practice is local or account-backed. */
@Composable
internal fun LearnerNavigation(screen:String,german:Boolean,enabled:Boolean=true,navigate:(String)->Unit) {
    val labels=studyCopy(german)
    NavigationBar(containerColor=MaterialTheme.colorScheme.surface) {
        listOf(Triple("home",R.string.study_home,Icons.Outlined.Home),
            Triple("progress",R.string.study_progress,Icons.AutoMirrored.Outlined.TrendingUp),
            Triple("topics",R.string.study_topics,Icons.AutoMirrored.Outlined.MenuBook)).forEach { (destination,title,icon) ->
            NavigationBarItem(selected=screen==destination || destination=="topics" && screen in setOf("topic","target"),
                onClick={navigate(destination)},enabled=enabled,
                colors=NavigationBarItemDefaults.colors(selectedIconColor=MaterialTheme.colorScheme.primary,
                    selectedTextColor=MaterialTheme.colorScheme.primary,
                    indicatorColor=MaterialTheme.colorScheme.primary.copy(alpha=0.12f),
                    unselectedIconColor=MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor=MaterialTheme.colorScheme.onSurfaceVariant),
                icon={Icon(icon,contentDescription=null)},label={Text(labels(title))})
        }
    }
}

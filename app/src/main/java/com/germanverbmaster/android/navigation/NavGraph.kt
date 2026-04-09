package com.germanverbmaster.android.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.germanverbmaster.android.ui.analytics.AnalyticsScreen
import com.germanverbmaster.android.ui.auth.AuthScreen
import com.germanverbmaster.android.ui.b2practice.B2PracticeScreen
import com.germanverbmaster.android.ui.history.AnswerHistoryScreen
import com.germanverbmaster.android.ui.home.HomeScreen
import com.germanverbmaster.android.ui.wortschatz.WortschatzScreen

private data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: @Composable () -> Unit,
)

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        NavItem(Screen.Home, "Practice") { Icon(Icons.Default.Stars, "Practice") },
        NavItem(Screen.B2Practice, "B2 Prüfung") { Icon(Icons.Default.School, "B2 Prüfung") },
        NavItem(Screen.Wortschatz, "Wortschatz") { Icon(Icons.AutoMirrored.Filled.MenuBook, "Wortschatz") },
        NavItem(Screen.Auth, "Account") { Icon(Icons.Default.AccountCircle, "Account") },
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == item.screen.route
                    } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = item.icon,
                        label = { Text(item.label) },
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route)        { HomeScreen() }
            composable(Screen.B2Practice.route)  { B2PracticeScreen() }
            composable(Screen.Wortschatz.route)  { WortschatzScreen() }
            composable(Screen.Analytics.route)   { AnalyticsScreen() }
            composable(Screen.History.route)     { AnswerHistoryScreen() }
            composable(Screen.Auth.route) {
                AuthScreen(
                    onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onAuthSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

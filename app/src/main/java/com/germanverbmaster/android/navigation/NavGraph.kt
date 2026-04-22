package com.germanverbmaster.android.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.germanverbmaster.android.R
import com.germanverbmaster.android.ui.analytics.AnalyticsScreen
import com.germanverbmaster.android.ui.auth.AuthScreen
import com.germanverbmaster.android.ui.b2practice.B2PracticeScreen
import com.germanverbmaster.android.ui.history.AnswerHistoryScreen
import com.germanverbmaster.android.ui.home.HomeScreen
import com.germanverbmaster.android.ui.worddetail.WordDetailScreen
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

    // Double back to exit logic
    val context = LocalContext.current
    var lastBackPressTime by remember { mutableLongStateOf(0L) }
    val isAtRoot = navController.previousBackStackEntry == null

    BackHandler(enabled = isAtRoot) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < 2000) {
            (context as? Activity)?.finish()
        } else {
            lastBackPressTime = currentTime
            Toast.makeText(context, R.string.press_back_again_to_exit, Toast.LENGTH_SHORT).show()
        }
    }

    val items = listOf(
        NavItem(Screen.Wortschatz, stringResource(R.string.nav_wortschatz)) { Icon(Icons.AutoMirrored.Filled.MenuBook, stringResource(R.string.nav_wortschatz)) },
        NavItem(Screen.B2Practice, stringResource(R.string.nav_b2_practice)) { Icon(Icons.Default.School, stringResource(R.string.nav_b2_practice)) },
        NavItem(Screen.Home, stringResource(R.string.nav_practice)) { Icon(Icons.Default.Stars, stringResource(R.string.nav_practice)) },
        NavItem(Screen.Auth, stringResource(R.string.nav_account)) { Icon(Icons.Default.AccountCircle, stringResource(R.string.nav_account)) },
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
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                // Only restore state if we are navigating between top-level items
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
            startDestination = Screen.Wortschatz.route,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route)        { HomeScreen(onNavigateToHistory = { res -> navController.navigate(Screen.History.createRoute(res)) }) }
            composable(Screen.B2Practice.route)  { B2PracticeScreen(onNavigateToHistory = { res -> navController.navigate(Screen.History.createRoute(res)) }) }
            composable(Screen.Wortschatz.route)  { 
                WortschatzScreen(
                    onNavigateToHistory = { res -> navController.navigate(Screen.History.createRoute(res)) },
                    onNavigateToWordDetail = { wordId -> navController.navigate(Screen.WordDetail.createRoute(wordId)) }
                ) 
            }
            composable(
                route = Screen.WordDetail.route,
                arguments = listOf(navArgument("wordId") { type = NavType.IntType })
            ) {
                WordDetailScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Analytics.route)   { AnalyticsScreen() }
            composable(
                route = Screen.History.route + "?result={result}",
                arguments = listOf(navArgument("result") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) {
                AnswerHistoryScreen(
                    onNavigateToWordDetail = { wordId -> navController.navigate(Screen.WordDetail.createRoute(wordId)) }
                )
            }
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

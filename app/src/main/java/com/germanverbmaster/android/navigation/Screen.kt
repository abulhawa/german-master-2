package com.germanverbmaster.android.navigation

sealed class Screen(val route: String) {
    data object Home        : Screen("home")
    data object B2Practice  : Screen("b2_practice")
    data object Wortschatz  : Screen("wortschatz")
    data object Analytics   : Screen("analytics")
    data object History     : Screen("history")
}

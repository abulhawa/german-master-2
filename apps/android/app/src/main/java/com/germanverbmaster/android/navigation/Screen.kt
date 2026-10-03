package com.germanverbmaster.android.navigation

sealed class Screen(val route: String) {
    data object Home        : Screen("home")
    data object B2Practice  : Screen("b2_practice")
    data object Wortschatz  : Screen("wortschatz")
    data object Analytics   : Screen("analytics")
    data object History     : Screen("history") {
        fun createRoute(result: String? = null) = if (result != null) "history?result=$result" else "history"
    }
    data object Auth        : Screen("auth")
    data object WordDetail  : Screen("word_detail/{wordId}") {
        fun createRoute(wordId: Int) = "word_detail/$wordId"
    }
}

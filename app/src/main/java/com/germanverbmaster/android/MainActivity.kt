package com.germanverbmaster.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.germanverbmaster.android.navigation.AppNavGraph
import com.germanverbmaster.android.ui.theme.GermanVerbMasterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GermanVerbMasterTheme {
                AppNavGraph()
            }
        }
    }
}

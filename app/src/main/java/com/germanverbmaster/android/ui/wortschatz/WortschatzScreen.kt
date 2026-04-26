package com.germanverbmaster.android.ui.wortschatz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.germanverbmaster.android.speech.TextToSpeechHelper

@Composable
fun WortschatzScreen(
    viewModel: WortschatzViewModel = hiltViewModel(),
    onNavigateToHistory: (String) -> Unit,
    onNavigateToWordDetail: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val ttsHelper = remember { TextToSpeechHelper(context) }

    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

    WortschatzScreenContent(
        state = state,
        onTriggerSync = { viewModel.triggerSync(force = true) },
        onSelectTab = viewModel::selectTab,
        onUpdateSearchQuery = viewModel::updateSearchQuery,
        onToggleLevel = viewModel::toggleLevel,
        onTogglePos = viewModel::togglePos,
        onNavigateToHistory = onNavigateToHistory,
        onNavigateToWordDetail = onNavigateToWordDetail,
        onFlip = viewModel::flip,
        onMarkCorrect = viewModel::markCorrect,
        onMarkWrong = viewModel::markWrong,
        onRestartDrill = viewModel::restartDrill,
        onSpeak = { ttsHelper.speak(it) }
    )
}

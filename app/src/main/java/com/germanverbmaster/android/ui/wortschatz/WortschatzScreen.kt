package com.germanverbmaster.android.ui.wortschatz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WortschatzScreen(
    viewModel: WortschatzViewModel = hiltViewModel(),
    onNavigateToHistory: (String) -> Unit,
    onNavigateToWordDetail: (Int) -> Unit,
    onShowGrammar: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val targetLanguage by viewModel.targetLanguage.collectAsStateWithLifecycle()
    val isDownloading by viewModel.isDownloading.collectAsStateWithLifecycle()
    val downloadError by viewModel.downloadError.collectAsStateWithLifecycle()

    WortschatzScreenContent(
        state = state,
        targetLanguage = targetLanguage,
        isDownloading = isDownloading,
        downloadError = downloadError,
        onTriggerSync = { viewModel.triggerSync(force = true) },
        onSelectTab = viewModel::selectTab,
        onUpdateSearchQuery = viewModel::updateSearchQuery,
        onToggleLevel = viewModel::toggleLevel,
        onTogglePos = viewModel::togglePos,
        onNavigateToHistory = onNavigateToHistory,
        onNavigateToWordDetail = onNavigateToWordDetail,
        onShowGrammar = onShowGrammar,
        onFlip = viewModel::flip,
        onMarkCorrect = viewModel::markCorrect,
        onMarkWrong = viewModel::markWrong,
        onRestartDrill = viewModel::restartDrill,
        onExitDrill = viewModel::onExitDrill,
        onSpeak = { viewModel.speak(it) },
        onRefreshAi = viewModel::requestAiTranslation,
        onSetTargetLanguage = viewModel::setTargetLanguage,
        onDownloadModels = viewModel::downloadModels,
        onDeleteLanguageModel = viewModel::deleteLanguageModel,
    )
}

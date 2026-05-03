package com.germanverbmaster.android.data.util

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelDownloadManager @Inject constructor() {
    private val modelManager = RemoteModelManager.getInstance()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading = _isDownloading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    suspend fun isModelDownloaded(language: String): Boolean {
        val model = TranslateRemoteModel.Builder(language).build()
        return modelManager.isModelDownloaded(model).await()
    }

    suspend fun downloadModels(allowMobileData: Boolean) {
        _isDownloading.value = true
        _error.value = null
        try {
            val conditionsBuilder = DownloadConditions.Builder()
            if (!allowMobileData) {
                conditionsBuilder.requireWifi()
            }
            val conditions = conditionsBuilder.build()

            val germanModel = TranslateRemoteModel.Builder(TranslateLanguage.GERMAN).build()
            val englishModel = TranslateRemoteModel.Builder(TranslateLanguage.ENGLISH).build()

            // Download both needed for DE <-> EN
            modelManager.download(germanModel, conditions).await()
            modelManager.download(englishModel, conditions).await()
            
            Log.d("ModelDownloadManager", "Models downloaded successfully")
        } catch (e: Exception) {
            Log.e("ModelDownloadManager", "Error downloading models", e)
            _error.value = e.message ?: "Download failed"
        } finally {
            _isDownloading.value = false
        }
    }

    suspend fun deleteModels() {
        val germanModel = TranslateRemoteModel.Builder(TranslateLanguage.GERMAN).build()
        val englishModel = TranslateRemoteModel.Builder(TranslateLanguage.ENGLISH).build()
        modelManager.deleteDownloadedModel(germanModel).await()
        modelManager.deleteDownloadedModel(englishModel).await()
    }
}

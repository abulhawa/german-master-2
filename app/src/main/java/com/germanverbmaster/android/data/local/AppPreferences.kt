package com.germanverbmaster.android.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.germanverbmaster.android.domain.model.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "sync_prefs")
private val LEXEME_LAST_SYNC = stringPreferencesKey("lexeme_last_sync")
private val TASK_LAST_SYNC   = stringPreferencesKey("task_last_sync")
private val INFLECTION_LAST_SYNC = stringPreferencesKey("inflection_last_sync")
private val WORTSCHATZ_LAST_SYNC = stringPreferencesKey("wortschatz_last_sync")
private val HISTORY_LAST_SYNC = stringPreferencesKey("history_last_sync")
private val B2_CATEGORY = stringPreferencesKey("b2_category")
private val B2_INDEX = stringPreferencesKey("b2_index")
private val B2_SHUFFLE = stringPreferencesKey("b2_shuffle")
private val B2_CORRECT = stringPreferencesKey("b2_correct")
private val B2_WRONG = stringPreferencesKey("b2_wrong")
private val DRILL_INDEX = stringPreferencesKey("drill_index")
private val DRILL_CORRECT = stringPreferencesKey("drill_correct")
private val DRILL_WRONG = stringPreferencesKey("drill_wrong")
private val DRILL_SEED = stringPreferencesKey("drill_seed")
private val THEME_MODE = stringPreferencesKey("theme_mode")

@Singleton
class AppPreferences @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    val themeMode: Flow<AppTheme> = context.dataStore.data.map { prefs ->
        prefs[THEME_MODE]?.let { AppTheme.valueOf(it) } ?: AppTheme.SYSTEM
    }

    suspend fun setThemeMode(mode: AppTheme) {
        context.dataStore.edit { it[THEME_MODE] = mode.name }
    }

    suspend fun getLexemeLastSync(): String? =
        context.dataStore.data.first()[LEXEME_LAST_SYNC]

    suspend fun setLexemeLastSync(value: String) =
        context.dataStore.edit { it[LEXEME_LAST_SYNC] = value }

    suspend fun getTaskLastSync(): String? =
        context.dataStore.data.first()[TASK_LAST_SYNC]

    suspend fun setTaskLastSync(value: String) =
        context.dataStore.edit { it[TASK_LAST_SYNC] = value }

    suspend fun getInflectionLastSync(): String? =
        context.dataStore.data.first()[INFLECTION_LAST_SYNC]

    suspend fun setInflectionLastSync(value: String) =
        context.dataStore.edit { it[INFLECTION_LAST_SYNC] = value }

    suspend fun getWortschatzLastSync(): Long =
        context.dataStore.data.first()[WORTSCHATZ_LAST_SYNC]?.toLongOrNull() ?: 0L

    suspend fun setWortschatzLastSync(value: Long) =
        context.dataStore.edit { it[WORTSCHATZ_LAST_SYNC] = value.toString() }

    suspend fun getHistoryLastSync(): String? =
        context.dataStore.data.first()[HISTORY_LAST_SYNC]

    suspend fun setHistoryLastSync(value: String) =
        context.dataStore.edit { it[HISTORY_LAST_SYNC] = value }

    suspend fun clearHistoryLastSync() =
        context.dataStore.edit { it.remove(HISTORY_LAST_SYNC) }

    suspend fun getB2Category(): String? =
        context.dataStore.data.first()[B2_CATEGORY]

    suspend fun setB2Category(value: String) =
        context.dataStore.edit { it[B2_CATEGORY] = value }

    suspend fun getB2Index(): Int? =
        context.dataStore.data.first()[B2_INDEX]?.toIntOrNull()

    suspend fun setB2Index(value: Int) =
        context.dataStore.edit { it[B2_INDEX] = value.toString() }

    suspend fun getB2Shuffle(): Boolean =
        context.dataStore.data.first()[B2_SHUFFLE]?.toBoolean() ?: true

    suspend fun setB2Shuffle(value: Boolean) =
        context.dataStore.edit { it[B2_SHUFFLE] = value.toString() }

    suspend fun getB2Correct(): Int =
        context.dataStore.data.first()[B2_CORRECT]?.toIntOrNull() ?: 0

    suspend fun setB2Correct(value: Int) =
        context.dataStore.edit { it[B2_CORRECT] = value.toString() }

    suspend fun getB2Wrong(): Int =
        context.dataStore.data.first()[B2_WRONG]?.toIntOrNull() ?: 0

    suspend fun setB2Wrong(value: Int) =
        context.dataStore.edit { it[B2_WRONG] = value.toString() }

    suspend fun getDrillIndex(): Int =
        context.dataStore.data.first()[DRILL_INDEX]?.toIntOrNull() ?: 0

    suspend fun setDrillIndex(value: Int) =
        context.dataStore.edit { it[DRILL_INDEX] = value.toString() }

    suspend fun getDrillCorrect(): Int =
        context.dataStore.data.first()[DRILL_CORRECT]?.toIntOrNull() ?: 0

    suspend fun setDrillCorrect(value: Int) =
        context.dataStore.edit { it[DRILL_CORRECT] = value.toString() }

    suspend fun getDrillWrong(): Int =
        context.dataStore.data.first()[DRILL_WRONG]?.toIntOrNull() ?: 0

    suspend fun setDrillWrong(value: Int) =
        context.dataStore.edit { it[DRILL_WRONG] = value.toString() }

    suspend fun getDrillSeed(): Long? =
        context.dataStore.data.first()[DRILL_SEED]?.toLongOrNull()

    suspend fun setDrillSeed(value: Long) =
        context.dataStore.edit { it[DRILL_SEED] = value.toString() }
}

package com.germanverbmaster.android.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.data.local.dao.DailyAccuracy
import com.germanverbmaster.android.data.local.dao.TaskTypeStat
import com.germanverbmaster.android.data.repository.PracticeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class AnalyticsUiState(
    val taskTypeStats: List<TaskTypeStat> = emptyList(),
    val dailyAccuracy: List<DailyAccuracy> = emptyList(),
    val accuracyToday: Float = 0f,
    val totalToday: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val practiceRepository: PracticeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsUiState())
    val state: StateFlow<AnalyticsUiState> = _state.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val since = Instant.now().minusSeconds(86400 * 7).toString() // Last 7 days
            
            combine(
                practiceRepository.observeTaskTypeStats(),
                flow { emit(practiceRepository.accuracyToday()) }
            ) { stats, today ->
                val daily = practiceRepository.getDailyAccuracy(since)
                
                _state.update { it.copy(
                    taskTypeStats = stats,
                    dailyAccuracy = daily,
                    accuracyToday = today.first,
                    totalToday = today.second,
                    isLoading = false
                ) }
            }.collect()
        }
    }
}

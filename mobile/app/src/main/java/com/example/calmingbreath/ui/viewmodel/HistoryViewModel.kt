package com.example.calmingbreath.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calmingbreath.data.ExerciseSessionDao
import com.example.calmingbreath.data.ExerciseSessionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HistoryUiState(
    val isLoading: Boolean = true,
    val items: List<ExerciseSessionEntity> = emptyList(),
)

// История берётся из локальной базы: вход не обязателен, поэтому на бэкенд не опираемся.
class HistoryViewModel(
    dao: ExerciseSessionDao,
) : ViewModel() {

    val state: StateFlow<HistoryUiState> = dao.getHistory()
        .map { HistoryUiState(isLoading = false, items = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())
}

class HistoryViewModelFactory(
    private val dao: ExerciseSessionDao,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryViewModel(dao) as T
    }
}

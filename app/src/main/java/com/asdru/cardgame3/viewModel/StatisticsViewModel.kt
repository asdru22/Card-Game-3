package com.asdru.cardgame3.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.asdru.cardgame3.data.entity.CharacterStats
import com.asdru.cardgame3.data.repository.CharacterStatsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StatisticsViewModel(
  private val repository: CharacterStatsRepository
) : ViewModel() {

  val pickRates: StateFlow<List<CharacterStats>> = repository.allStats
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )
}

class StatisticsViewModelFactory(private val repository: CharacterStatsRepository) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return StatisticsViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}

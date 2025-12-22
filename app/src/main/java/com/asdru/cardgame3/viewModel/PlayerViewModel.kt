package com.asdru.cardgame3.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.asdru.cardgame3.data.entity.Player
import com.asdru.cardgame3.data.repository.PlayerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayerViewModel(private val repository: PlayerRepository) : ViewModel() {

    val players: StateFlow<List<Player>> = repository.allPlayers
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addPlayer(name: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val success = repository.addPlayer(name)
            if (success) {
                onSuccess()
            } else {
                onError()
            }
        }
    }
}

class PlayerViewModelFactory(private val repository: PlayerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.asdru.cardgame3.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.asdru.cardgame3.data.repository.CharacterStatsRepository
import com.asdru.cardgame3.data.repository.PlayerRepository

class BattleViewModelFactory(
    private val playerRepository: PlayerRepository,
    private val characterStatsRepository: CharacterStatsRepository,
    private val resourceResolver: (Int) -> String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BattleViewModel::class.java)) {
            return BattleViewModel(
                playerRepository = playerRepository,
                characterStatsRepository = characterStatsRepository,
                resourceResolver = resourceResolver
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.asdru.cardgame3.domain.usecase

import com.asdru.cardgame3.game.state.GameState
import com.asdru.cardgame3.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class ObserveGameStateUseCase(
  private val repository: GameRepository
) {
  operator fun invoke(gameId: String): Flow<GameState> {
    return repository.observeGameState(gameId)
  }
}
package com.asdru.cardgame3.domain.usecase

import com.asdru.cardgame3.game.state.GameEvent
import com.asdru.cardgame3.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class ObserveGameEventsUseCase(
  private val repository: GameRepository
) {
  operator fun invoke(gameId: String): Flow<GameEvent> {
    return repository.observeGameEvents(gameId)
  }
}
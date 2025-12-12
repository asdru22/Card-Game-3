package com.asdru.cardgame3.domain.usecase

import com.asdru.cardgame3.game.state.GameAction
import com.asdru.cardgame3.repository.GameRepository

class SubmitActionUseCase(
  private val repository: GameRepository
) {
  suspend operator fun invoke(
    gameId: String,
    action: GameAction
  ): Result<Unit> {
    return repository.submitAction(gameId, action)
  }
}
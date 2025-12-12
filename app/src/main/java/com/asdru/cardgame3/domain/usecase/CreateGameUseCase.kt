package com.asdru.cardgame3.domain.usecase

import com.asdru.cardgame3.repository.GameMode
import com.asdru.cardgame3.repository.GameRepository


class CreateGameUseCase(
  private val repository: GameRepository
) {
  suspend operator fun invoke(
    player1Name: String,
    player2Name: String,
    mode: GameMode
  ): Result<String> {
    // Validate inputs
    if (player1Name.isBlank()) {
      return Result.failure(Exception("Player 1 name cannot be empty"))
    }
    if (player2Name.isBlank() && mode == GameMode.LOCAL) {
      return Result.failure(Exception("Player 2 name cannot be empty"))
    }

    return repository.createGame(player1Name, player2Name, mode)
  }
}
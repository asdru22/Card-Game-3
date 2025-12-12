package com.asdru.cardgame3.domain.usecase

import com.asdru.cardgame3.game.state.GameState
import com.asdru.cardgame3.repository.GameRepository

class StartGameUseCase(
  private val repository: GameRepository
) {
  suspend operator fun invoke(
    gameId: String,
    player1Team: List<String>,
    player2Team: List<String>
  ): Result<GameState> {
    // Validate team sizes
    if (player1Team.size != 3) {
      return Result.failure(Exception("Player 1 must select exactly 3 characters"))
    }
    if (player2Team.size != 3) {
      return Result.failure(Exception("Player 2 must select exactly 3 characters"))
    }

    // Validate no duplicates within teams
    if (player1Team.toSet().size != 3) {
      return Result.failure(Exception("Player 1 has duplicate characters"))
    }
    if (player2Team.toSet().size != 3) {
      return Result.failure(Exception("Player 2 has duplicate characters"))
    }

    return repository.startGame(gameId, player1Team, player2Team)
  }
}
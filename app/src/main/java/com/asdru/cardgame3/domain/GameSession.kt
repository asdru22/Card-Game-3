package com.asdru.cardgame3.domain

import com.asdru.cardgame3.game.state.GameState
import com.asdru.cardgame3.repository.GameMode


data class GameSession(
  val gameId: String,
  val mode: GameMode,
  val player1Id: String,
  val player2Id: String,
  val currentPlayerId: String,
  val state: GameState? = null,
  val createdAt: Long = System.currentTimeMillis()
) {
  val isMyTurn: Boolean
    get() {
      val state = this.state ?: return false
      return when (currentPlayerId) {
        player1Id if state.isLeftTeamTurn -> true
        player2Id if !state.isLeftTeamTurn -> true
        else -> false
      }
    }

  val isGameOver: Boolean
    get() = state?.winner != null

  val isLocalGame: Boolean
    get() = mode == GameMode.LOCAL
}
package com.asdru.cardgame3.repository

import com.asdru.cardgame3.game.state.GameAction
import com.asdru.cardgame3.game.state.GameEvent
import com.asdru.cardgame3.game.state.GameState
import kotlinx.coroutines.flow.Flow


interface GameRepository {

  suspend fun createGame(
    player1Name: String,
    player2Name: String,
    mode: GameMode
  ): Result<String>


  suspend fun joinGame(gameId: String, playerName: String): Result<Unit>

  suspend fun startGame(
    gameId: String,
    player1Team: List<String>,
    player2Team: List<String>
  ): Result<GameState>


  suspend fun submitAction(gameId: String, action: GameAction): Result<Unit>


  fun observeGameState(gameId: String): Flow<GameState>


  fun observeGameEvents(gameId: String): Flow<GameEvent>


  suspend fun getGameHistory(gameId: String): Result<List<GameAction>>

  suspend fun endGame(gameId: String): Result<Unit>
}

enum class GameMode {
  LOCAL,           // Same device
  ONLINE_RANDOM,   // Matchmaking
  ONLINE_PRIVATE   // Private lobby
}
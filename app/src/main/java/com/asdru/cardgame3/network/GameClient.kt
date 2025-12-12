package com.asdru.cardgame3.network

import com.asdru.cardgame3.game.state.*
import kotlinx.coroutines.flow.Flow


interface GameClient {
  suspend fun connect(): Result<Unit>

  suspend fun disconnect()


  suspend fun sendAction(action: GameAction): Result<Unit>


  fun observeGameState(): Flow<GameState>


  fun observeGameEvents(): Flow<GameEvent>


  suspend fun createLobby(playerId: String): Result<LobbyInfo>


  suspend fun joinLobby(lobbyId: String, playerId: String): Result<LobbyInfo>


  suspend fun leaveLobby(): Result<Unit>


  suspend fun startGame(teamSetup: TeamSetup): Result<GameState>


  fun observeConnectionStatus(): Flow<ConnectionStatus>
}

data class LobbyInfo(
  val lobbyId: String,
  val hostPlayerId: String,
  val players: List<PlayerInfo>,
  val maxPlayers: Int = 2,
  val status: LobbyStatus
)

data class PlayerInfo(
  val playerId: String,
  val playerName: String,
  val isReady: Boolean = false
)

enum class LobbyStatus {
  WAITING,
  READY,
  IN_GAME,
  FINISHED
}

data class TeamSetup(
  val player1Team: List<String>,
  val player2Team: List<String>
)

enum class ConnectionStatus {
  DISCONNECTED,
  CONNECTING,
  CONNECTED,
  ERROR
}
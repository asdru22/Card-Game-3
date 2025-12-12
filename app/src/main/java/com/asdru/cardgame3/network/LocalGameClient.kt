package com.asdru.cardgame3.network

import com.asdru.cardgame3.game.engine.GameEngine
import com.asdru.cardgame3.game.state.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID


class LocalGameClient(
  private val scope: CoroutineScope
) : GameClient {

  private var gameEngine: GameEngine? = null
  private val connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
  private val gameStateFlow = MutableStateFlow<GameState?>(null)
  private val gameEventsFlow = MutableSharedFlow<GameEvent>()

  override suspend fun connect(): Result<Unit> {
    connectionStatus.value = ConnectionStatus.CONNECTED
    return Result.success(Unit)
  }

  override suspend fun disconnect() {
    connectionStatus.value = ConnectionStatus.DISCONNECTED
    gameEngine = null
  }

  override suspend fun sendAction(action: GameAction): Result<Unit> {
    val engine = gameEngine ?: return Result.failure(
      IllegalStateException("Game not started")
    )

    return try {
      when (val result = engine.processAction(action)) {
        is GameActionResult.Success -> {
          gameStateFlow.value = result.newState
          result.events.forEach { gameEventsFlow.emit(it) }
          Result.success(Unit)
        }

        is GameActionResult.Failure -> {
          Result.failure(Exception(result.reason))
        }
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  override fun observeGameState(): Flow<GameState> {
    return gameStateFlow.filterNotNull()
  }

  override fun observeGameEvents(): Flow<GameEvent> {
    return gameEventsFlow
  }

  override suspend fun createLobby(playerId: String): Result<LobbyInfo> {
    val lobbyInfo = LobbyInfo(
      lobbyId = UUID.randomUUID().toString(),
      hostPlayerId = playerId,
      players = listOf(
        PlayerInfo(playerId, "Player 1", true)
      ),
      status = LobbyStatus.WAITING
    )
    return Result.success(lobbyInfo)
  }

  override suspend fun joinLobby(lobbyId: String, playerId: String): Result<LobbyInfo> {
    return Result.failure(UnsupportedOperationException("Local mode doesn't support joining"))
  }

  override suspend fun leaveLobby(): Result<Unit> {
    return Result.success(Unit)
  }

  override suspend fun startGame(teamSetup: TeamSetup): Result<GameState> {
    val gameId = UUID.randomUUID().toString()
    val engine = GameEngine(gameId, scope)

    val leftTeam = TeamState(
      teamId = "left",
      name = "Player 1",
      entities = emptyList(),
      rage = 0f
    )

    val rightTeam = TeamState(
      teamId = "right",
      name = "Player 2",
      entities = emptyList(),
      rage = 0f
    )

    val initialState = engine.initializeGame(leftTeam, rightTeam)

    // Set up event forwarding
    scope.launch {
      engine.events.collect { event ->
        gameEventsFlow.emit(event)
      }
    }

    scope.launch {
      engine.stateChanges.collect { state ->
        gameStateFlow.value = state
      }
    }

    gameEngine = engine
    return Result.success(initialState)
  }

  override fun observeConnectionStatus(): Flow<ConnectionStatus> {
    return connectionStatus.asStateFlow()
  }
}
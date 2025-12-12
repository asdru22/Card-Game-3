package com.asdru.cardgame3.game.engine

import com.asdru.cardgame3.game.state.AbilityType
import com.asdru.cardgame3.game.state.ActionFailureCode
import com.asdru.cardgame3.game.state.EntityState
import com.asdru.cardgame3.game.state.GameAction
import com.asdru.cardgame3.game.state.GameActionResult
import com.asdru.cardgame3.game.state.GameEvent
import com.asdru.cardgame3.game.state.GameState
import com.asdru.cardgame3.game.state.TeamState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GameEngine(
  private val gameId: String,
  private val scope: CoroutineScope
) {
  private val _events = MutableSharedFlow<GameEvent>()
  val events: SharedFlow<GameEvent> = _events.asSharedFlow()

  private val _stateChanges = MutableSharedFlow<GameState>()
  val stateChanges: SharedFlow<GameState> = _stateChanges.asSharedFlow()

  private var currentState: GameState? = null

  suspend fun initializeGame(leftTeam: TeamState, rightTeam: TeamState): GameState {
    val state = GameState(
      gameId = gameId,
      leftTeam = leftTeam,
      rightTeam = rightTeam,
      isLeftTeamTurn = listOf(true, false).random(),
      turnNumber = 1,
      actionsTaken = emptyList()
    )
    currentState = state
    _stateChanges.emit(state)
    return state
  }


  suspend fun processAction(action: GameAction): GameActionResult {
    val state = currentState ?: return GameActionResult.Failure(
      "Game not initialized",
      ActionFailureCode.GAME_OVER
    )

    if (state.winner != null) {
      return GameActionResult.Failure(
        "Game already ended",
        ActionFailureCode.GAME_OVER
      )
    }

    return when (action) {
      is GameAction.DragInteraction -> processDragInteraction(state, action)
      is GameAction.UltimateAbility -> processUltimateAbility(state, action)
      is GameAction.EndTurn -> processEndTurn(state, action)
      is GameAction.RestartGame -> processRestart(state, action)
      is GameAction.ViewEntityInfo -> {
        _events.emit(GameEvent.AbilityUsed(
          action.entityId,
          AbilityType.ACTIVE,
          null,
          action.timestamp
        ))
        GameActionResult.Success(state, emptyList())
      }
    }
  }

  private suspend fun processDragInteraction(
    state: GameState,
    action: GameAction.DragInteraction
  ): GameActionResult {
    // Validate action
    val validation = validateAction(state, action.sourceEntityId, action.playerId)
    if (validation != null) return validation

    val events = mutableListOf<GameEvent>()

    // Determine if same team or different team
    val sourceTeam = findEntityTeam(state, action.sourceEntityId)
    val targetTeam = findEntityTeam(state, action.targetEntityId)

    if (sourceTeam == null || targetTeam == null) {
      return GameActionResult.Failure(
        "Entity not found",
        ActionFailureCode.INVALID_TARGET
      )
    }

    val isSameTeam = sourceTeam == targetTeam


    val newState = state.copy(
      actionsTaken = state.actionsTaken + action.sourceEntityId
    )

    currentState = newState
    _stateChanges.emit(newState)

    return GameActionResult.Success(newState, events)
  }

  private suspend fun processUltimateAbility(
    state: GameState,
    action: GameAction.UltimateAbility
  ): GameActionResult {
    val team = if (action.teamId == state.leftTeam.teamId) {
      state.leftTeam
    } else {
      state.rightTeam
    }

    if (team.rage < team.maxRage) {
      return GameActionResult.Failure(
        "Insufficient rage",
        ActionFailureCode.INSUFFICIENT_RAGE
      )
    }

    val events = mutableListOf<GameEvent>()

    // Reset rage and execute ultimate
    val updatedTeam = team.copy(rage = 0f)
    val newState = if (action.teamId == state.leftTeam.teamId) {
      state.copy(leftTeam = updatedTeam)
    } else {
      state.copy(rightTeam = updatedTeam)
    }

    events.add(GameEvent.RageChanged(action.teamId, 0f))
    events.add(GameEvent.AbilityUsed(
      action.casterEntityId,
      AbilityType.ULTIMATE,
      null
    ))

    currentState = newState
    _stateChanges.emit(newState)

    return GameActionResult.Success(newState, events)
  }

  private suspend fun processEndTurn(
    state: GameState,
    action: GameAction.EndTurn
  ): GameActionResult {
    val events = mutableListOf<GameEvent>()

    val newState = state.copy(
      isLeftTeamTurn = !state.isLeftTeamTurn,
      turnNumber = state.turnNumber + 1,
      actionsTaken = emptyList()
    )

    val newTurnTeamId = if (newState.isLeftTeamTurn) {
      newState.leftTeam.teamId
    } else {
      newState.rightTeam.teamId
    }

    events.add(GameEvent.TurnChanged(newTurnTeamId, newState.turnNumber))

    currentState = newState
    _stateChanges.emit(newState)

    return GameActionResult.Success(newState, events)
  }

  private fun processRestart(
    state: GameState,
    action: GameAction.RestartGame
  ): GameActionResult {

    return GameActionResult.Success(state, emptyList())
  }

  private fun validateAction(
    state: GameState,
    entityId: String,
    playerId: String
  ): GameActionResult.Failure? {
    // Check if entity exists
    val entity = findEntity(state, entityId) ?: return GameActionResult.Failure(
      "Entity not found",
      ActionFailureCode.INVALID_TARGET
    )

    // Check if entity is alive
    if (!entity.isAlive) {
      return GameActionResult.Failure(
        "Entity is dead",
        ActionFailureCode.ENTITY_DEAD
      )
    }

    // Check if already acted
    if (entityId in state.actionsTaken) {
      return GameActionResult.Failure(
        "Entity already acted this turn",
        ActionFailureCode.ENTITY_ALREADY_ACTED
      )
    }

    // Check if it's the correct player's turn
    val entityTeam = findEntityTeam(state, entityId)
    val isLeftTeam = entityTeam == "left"
    if (isLeftTeam != state.isLeftTeamTurn) {
      return GameActionResult.Failure(
        "Not this team's turn",
        ActionFailureCode.NOT_PLAYER_TURN
      )
    }

    return null
  }

  private fun findEntity(state: GameState, entityId: String): EntityState? {
    return state.leftTeam.entities.find { it.entityId == entityId }
      ?: state.rightTeam.entities.find { it.entityId == entityId }
  }

  private fun findEntityTeam(state: GameState, entityId: String): String? {
    return when {
      state.leftTeam.entities.any { it.entityId == entityId } -> "left"
      state.rightTeam.entities.any { it.entityId == entityId } -> "right"
      else -> null
    }
  }

  fun getCurrentState(): GameState? = currentState
}
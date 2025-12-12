package com.asdru.cardgame3.game.state

sealed class GameAction {
    abstract val playerId: String
    abstract val timestamp: Long
    
    data class DragInteraction(
        override val playerId: String,
        val sourceEntityId: String,
        val targetEntityId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameAction()
    
    data class UltimateAbility(
        override val playerId: String,
        val teamId: String,
        val casterEntityId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameAction()
    
    data class ViewEntityInfo(
        override val playerId: String,
        val entityId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameAction()
    
    data class EndTurn(
        override val playerId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameAction()
    
    data class RestartGame(
        override val playerId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameAction()
}


sealed class GameActionResult {
    data class Success(
        val newState: GameState,
        val events: List<GameEvent>
    ) : GameActionResult()
    
    data class Failure(
        val reason: String,
        val code: ActionFailureCode
    ) : GameActionResult()
}

enum class ActionFailureCode {
    NOT_PLAYER_TURN,
    ENTITY_ALREADY_ACTED,
    ENTITY_DEAD,
    ENTITY_STUNNED,
    INVALID_TARGET,
    INSUFFICIENT_RAGE,
    GAME_OVER
}
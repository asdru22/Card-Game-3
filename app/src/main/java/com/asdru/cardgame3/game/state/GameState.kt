package com.asdru.cardgame3.game.state

data class GameState(
    val gameId: String,
    val leftTeam: TeamState,
    val rightTeam: TeamState,
    val isLeftTeamTurn: Boolean,
    val turnNumber: Int,
    val actionsTaken: List<String>,
    val winner: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class TeamState(
    val teamId: String,
    val name: String,
    val entities: List<EntityState>,
    val rage: Float,
    val maxRage: Float = 100f
)

data class EntityState(
    val entityId: String,
    val entityClass: String,
    val health: Float,
    val maxHealth: Float,
    val damage: Float,
    val statusEffects: List<StatusEffectState>,
    val isAlive: Boolean,
    val position: Int
)

data class StatusEffectState(
    val effectClass: String,
    val duration: Int,
    val sourceEntityId: String?
)
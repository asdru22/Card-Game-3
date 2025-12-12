package com.asdru.cardgame3.game.state

sealed class GameEvent {
    abstract val timestamp: Long
    
    data class DamageDealt(
        val sourceId: String,
        val targetId: String,
        val amount: Float,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    data class HealingApplied(
        val sourceId: String,
        val targetId: String,
        val amount: Float,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    data class EffectApplied(
        val sourceId: String,
        val targetId: String,
        val effectClass: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    data class EffectRemoved(
        val targetId: String,
        val effectClass: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    data class EntityDied(
        val entityId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    data class TurnChanged(
        val newTurnTeamId: String,
        val turnNumber: Int,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    data class RageChanged(
        val teamId: String,
        val newRage: Float,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    data class GameEnded(
        val winnerId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    data class AbilityUsed(
        val casterId: String,
        val abilityType: AbilityType,
        val targetId: String?,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
}

enum class AbilityType {
    ACTIVE,
    PASSIVE,
    ULTIMATE
}
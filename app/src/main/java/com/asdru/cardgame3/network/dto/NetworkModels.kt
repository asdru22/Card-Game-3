package com.asdru.cardgame3.network.dto

import com.asdru.cardgame3.game.state.*
import kotlinx.serialization.Serializable

@Serializable
data class GameStateDto(
  val gameId: String,
  val leftTeam: TeamStateDto,
  val rightTeam: TeamStateDto,
  val isLeftTeamTurn: Boolean,
  val turnNumber: Int,
  val actionsTaken: List<String>,
  val winner: String? = null,
  val timestamp: Long
)

@Serializable
data class TeamStateDto(
  val teamId: String,
  val name: String,
  val entities: List<EntityStateDto>,
  val rage: Float,
  val maxRage: Float
)

@Serializable
data class EntityStateDto(
  val entityId: String,
  val entityClass: String,
  val health: Float,
  val maxHealth: Float,
  val damage: Float,
  val statusEffects: List<StatusEffectStateDto>,
  val isAlive: Boolean,
  val position: Int
)

@Serializable
data class StatusEffectStateDto(
  val effectClass: String,
  val duration: Int,
  val sourceEntityId: String?
)

@Serializable
sealed class GameActionDto {
  abstract val playerId: String
  abstract val timestamp: Long

  @Serializable
  data class DragInteractionDto(
    override val playerId: String,
    val sourceEntityId: String,
    val targetEntityId: String,
    override val timestamp: Long
  ) : GameActionDto()

  @Serializable
  data class UltimateAbilityDto(
    override val playerId: String,
    val teamId: String,
    val casterEntityId: String,
    override val timestamp: Long
  ) : GameActionDto()
}

fun GameState.toDto() = GameStateDto(
  gameId = gameId,
  leftTeam = leftTeam.toDto(),
  rightTeam = rightTeam.toDto(),
  isLeftTeamTurn = isLeftTeamTurn,
  turnNumber = turnNumber,
  actionsTaken = actionsTaken,
  winner = winner,
  timestamp = timestamp
)

fun TeamState.toDto() = TeamStateDto(
  teamId = teamId,
  name = name,
  entities = entities.map { it.toDto() },
  rage = rage,
  maxRage = maxRage
)

fun EntityState.toDto() = EntityStateDto(
  entityId = entityId,
  entityClass = entityClass,
  health = health,
  maxHealth = maxHealth,
  damage = damage,
  statusEffects = statusEffects.map { it.toDto() },
  isAlive = isAlive,
  position = position
)

fun StatusEffectState.toDto() = StatusEffectStateDto(
  effectClass = effectClass,
  duration = duration,
  sourceEntityId = sourceEntityId
)
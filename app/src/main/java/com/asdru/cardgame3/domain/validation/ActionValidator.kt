package com.asdru.cardgame3.domain.validation

import com.asdru.cardgame3.game.state.*

object ActionValidator {

  fun validateDragInteraction(
    action: GameAction.DragInteraction,
    currentState: GameState
  ): ValidationResult {
    // Find source entity
    val sourceEntity = findEntity(currentState, action.sourceEntityId)
      ?: return ValidationResult.Invalid("Source entity not found")

    // Check if source is alive
    if (!sourceEntity.isAlive) {
      return ValidationResult.Invalid("Source entity is dead")
    }

    // Check if it's the correct player's turn
    val sourceTeam = findEntityTeam(currentState, action.sourceEntityId)
    val isLeftTeam = sourceTeam == "left"
    if (isLeftTeam != currentState.isLeftTeamTurn) {
      return ValidationResult.Invalid("Not your turn")
    }

    // Check if entity already acted
    if (action.sourceEntityId in currentState.actionsTaken) {
      return ValidationResult.Invalid("Entity has already acted this turn")
    }

    // Find target entity
    val targetEntity = findEntity(currentState, action.targetEntityId)
      ?: return ValidationResult.Invalid("Target entity not found")

    if (!targetEntity.isAlive) {
      return ValidationResult.Invalid("Target entity is dead")
    }

    return ValidationResult.Valid
  }

  fun validateUltimateAbility(
    action: GameAction.UltimateAbility,
    currentState: GameState
  ): ValidationResult {
    val team = if (action.teamId == currentState.leftTeam.teamId) {
      currentState.leftTeam
    } else {
      currentState.rightTeam
    }

    if (team.rage < team.maxRage) {
      return ValidationResult.Invalid("Insufficient rage")
    }

    val caster = findEntity(currentState, action.casterEntityId)
      ?: return ValidationResult.Invalid("Caster not found")

    if (!caster.isAlive) {
      return ValidationResult.Invalid("Caster is dead")
    }

    return ValidationResult.Valid
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
}

sealed class ValidationResult {
  object Valid : ValidationResult()
  data class Invalid(val reason: String) : ValidationResult()
}
package com.asdru.cardgame3.viewModel

import kotlinx.coroutines.delay

object BattleCombatLogic {

  suspend fun executeCardInteraction(
    source: EntityViewModel,
    target: EntityViewModel,
    isSameTeam: Boolean
  ) {
    if (isSameTeam) {
      handleFriendlyInteraction(source, target)
    } else {
      handleHostileInteraction(source, target)
    }
  }

  private suspend fun handleFriendlyInteraction(source: EntityViewModel, target: EntityViewModel) {
    if (source.effectManager.isStunned) return

    source.currentActiveCharges = 0
    val ability = source.entity.passiveAbility

    if (ability.charges > 1) {
      source.currentPassiveCharges++
      source.chargeAnimTrigger++
      delay(300)

      if (source.currentPassiveCharges >= ability.charges) {
        delay(400)
        source.currentPassiveCharges = 0
        source.passiveAnimTrigger++
        delay(150)
        ability.effect(source, target)
        delay(150)
      }
    } else {
      source.passiveAnimTrigger++
      delay(150)
      ability.effect(source, target)
      delay(150)
    }
  }

  private suspend fun handleHostileInteraction(source: EntityViewModel, target: EntityViewModel) {
    source.currentPassiveCharges = 0
    val ability = source.entity.activeAbility

    if (ability.charges > 1) {
      source.currentActiveCharges++
      source.chargeAnimTrigger++
      delay(300)

      if (source.currentActiveCharges >= ability.charges) {
        delay(400)
        source.currentActiveCharges = 0
        ability.effect(source, target)
        delay(200)
      }
    } else {
      ability.effect(source, target)
      delay(200)
    }
  }
}
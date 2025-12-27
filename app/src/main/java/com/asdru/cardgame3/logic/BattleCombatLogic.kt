package com.asdru.cardgame3.logic

import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.viewModel.EntityViewModel
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
    source.team.shop.onEndOfTurn()
  }

  suspend fun performActiveAbility(source: EntityViewModel, target: EntityViewModel) {
    val finalTarget = source.effectManager.modifyActiveTarget(source, target)

    source.currentPassiveCharges = 0
    val ability = source.activeAbility

    if (ability.charges > 1) {
      source.currentActiveCharges++
      source.chargeAnimTrigger++
      delay(300)

      if (source.currentActiveCharges >= ability.charges) {
        delay(400)
        source.currentActiveCharges = 0
        performActive(source,finalTarget,ability)
      }
    } else {
      performActive(source,finalTarget,ability)
    }
  }

  private suspend fun performActive(source: EntityViewModel, target: EntityViewModel,ability: Ability){
    ability.effect(source, target)
    delay(200)
    source.applyTraits {
      it.onUsedActiveAbility(owner = source, target = target)
    }
  }

  suspend fun performPassiveAbility(source: EntityViewModel, target: EntityViewModel) {
    val finalTarget = source.effectManager.modifyPassiveTarget(source, target)

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
        performPassive(source, finalTarget, ability)
      }
    } else {
      performPassive(source, finalTarget, ability)
    }
  }

  private suspend fun performPassive(
    source: EntityViewModel,
    target: EntityViewModel,
    ability: Ability
  ) {
    source.passiveAnimTrigger++
    delay(150)
    ability.effect(source, target)
    delay(150)
    source.applyTraits {
      it.onUsedPassiveAbility(owner = source, target = target)
    }
  }

  private suspend fun handleFriendlyInteraction(source: EntityViewModel, target: EntityViewModel) {
    performPassiveAbility(source, target)
  }

  private suspend fun handleHostileInteraction(source: EntityViewModel, target: EntityViewModel) {
    performActiveAbility(source, target)
  }
}
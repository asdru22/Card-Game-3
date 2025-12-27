package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.data.DamageData
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.game.effect.StatusEffect
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.collections.remove

interface Trait : Translatable {
  val id: String get() = this::class.java.simpleName
  val maxCharges: Int get() = 0
  fun modifyOutgoingDamage(owner: EntityViewModel, target: EntityViewModel, amount: Float): Float =
    amount

  fun modifyIncomingDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float): Float =
    amount

  fun modifyHeal(owner: EntityViewModel, source: EntityViewModel?, amount: Float): Float = amount
  fun onStartTurn(owner: EntityViewModel) {}
  suspend fun onEndTurn(owner: EntityViewModel) {}
  suspend fun onDidDealDamage(
    owner: EntityViewModel,
    target: EntityViewModel,
    amount: Float,
    overkill: Float = 0f
  ) {
  }

  suspend fun onUsedActiveAbility(owner: EntityViewModel, target: EntityViewModel) {}
  suspend fun onUsedPassiveAbility(owner: EntityViewModel, target: EntityViewModel) {}

  suspend fun onDidReceiveDamage(
    owner: EntityViewModel,
    source: EntityViewModel?,
    amount: Float,
    damageData: DamageData? = null
  ) {
  }

  suspend fun onDeath(owner: EntityViewModel) {}
  fun modifyIncomingEffect(
    owner: EntityViewModel,
    effect: StatusEffect,
    source: EntityViewModel?
  ): StatusEffect? = effect

  fun onStartTurnDead(owner: EntityViewModel) {}
  suspend fun onEntityDeath(owner: EntityViewModel, entity: EntityViewModel) {}

  companion object {
    fun getTrait(
      owner: EntityViewModel,
      id: String
    ): Trait? {
      return owner.traits.find { it.id == id }
    }

    fun <T : Trait> hasTrait(
      owner: EntityViewModel,
      traitClass: Class<T>
    ): Boolean {
      return owner.traits.any { it.id == traitClass.simpleName }
    }
  }

  fun increaseCharge(owner: EntityViewModel): Int {
    val currentCharges = owner.traitCharges[id] ?: 0
    if (currentCharges < maxCharges) {
      val newCharges = currentCharges + 1
      owner.traitCharges[id] = newCharges
      return newCharges
    }
    return 0
  }

  fun getCharge(owner: EntityViewModel): Int {
    return owner.traitCharges[id] ?: 0
  }

  fun resetCharge(owner: EntityViewModel) {
    owner.traitCharges[id] = 0
  }
}
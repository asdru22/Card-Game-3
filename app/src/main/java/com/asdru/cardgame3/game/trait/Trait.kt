package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.game.effect.StatusEffect
import com.asdru.cardgame3.viewModel.EntityViewModel

interface Trait : Translatable {
  val id: String get() = this::class.java.simpleName
  fun modifyOutgoingDamage(owner: EntityViewModel, target: EntityViewModel, amount: Float): Float =
    amount

  fun modifyIncomingDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float): Float =
    amount

  fun modifyHeal(owner: EntityViewModel, amount: Float): Float = amount
  fun onStartTurn(owner: EntityViewModel) {}
  suspend fun onEndTurn(owner: EntityViewModel) {}
  suspend fun onDidDealDamage(
    owner: EntityViewModel,
    target: EntityViewModel,
    amount: Float,
    overkill: Float = 0f
  ) {
  }

  fun onDidReceiveDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float) {}
  suspend fun onDeath(owner: EntityViewModel) {}
  fun modifyIncomingEffect(
    owner: EntityViewModel,
    effect: StatusEffect,
    source: EntityViewModel?
  ): StatusEffect? = effect

  fun onStartTurnDead(owner: EntityViewModel) {}
  val maxCharges: Int get() = 0
}
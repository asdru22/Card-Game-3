package com.asdru.cardgame3.trait

import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel

interface Trait : Translatable {
  fun modifyOutgoingDamage(owner: EntityViewModel, target: EntityViewModel, amount: Float): Float =
    amount
  fun modifyIncomingDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float): Float =
    amount
  fun modifyHeal(owner: EntityViewModel, amount: Float): Float = amount
  fun onStartTurn(owner: EntityViewModel) {}
  suspend fun onEndTurn(owner: EntityViewModel) {}
  suspend fun onDidDealDamage(owner: EntityViewModel, target: EntityViewModel, amount: Float, overkill: Float = 0f) {}
  fun onDidReceiveDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float) {}
  suspend fun onDeath(owner: EntityViewModel) {}
}
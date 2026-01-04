package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.DamageData
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.viewModel.EntityViewModel

class Revving : Trait {
  override val maxCharges: Int = MAX_CHARGES
  override val nameRes: Int = R.string.trait_revving
  override val descriptionRes: Int = R.string.trait_revving_desc
  override val formatArgs: List<Any> = listOf(
    MAX_CHARGES,
    BLOCKED_DAMAGE_MULT
  )

  override suspend fun onUsedActiveAbility(
    owner: EntityViewModel,
    target: EntityViewModel
  ) {
    increaseCharge(owner)
  }

  override suspend fun onUsedPassiveAbility(
    owner: EntityViewModel,
    target: EntityViewModel
  ) {
    resetCharge(owner)
  }

  override suspend fun onDamageBlocked(
    owner: EntityViewModel,
    target: EntityViewModel,
    amount: Float
  ) {
    if (!charged(owner)) return
    owner.applyDamage(
      target,
      amount * BLOCKED_DAMAGE_MULT / 100f,
      damageData = DamageData(ignoreReceiverEffects = true)
    )
  }

  companion object {
    private const val BLOCKED_DAMAGE_MULT = 200f
    private const val MAX_CHARGES = 3
  }
}

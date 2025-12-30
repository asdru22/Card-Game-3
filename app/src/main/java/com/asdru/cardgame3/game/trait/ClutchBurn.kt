package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.DamageData
import com.asdru.cardgame3.game.effect.Defiance
import com.asdru.cardgame3.game.effect.Stunned
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlinx.coroutines.delay

class ClutchBurn : Trait {
  override val maxCharges: Int = MAX_CHARGES
  override val nameRes: Int = R.string.trait_clutch_burn
  override val descriptionRes: Int = R.string.trait_clutch_burn_desc
  override val formatArgs: List<Any> = listOf(
    MAX_CHARGES,
    Stunned.Spec,
    STUNNED_DURATION
  )

  override suspend fun onUsedPassiveAbility(
    owner: EntityViewModel,
    target: EntityViewModel
  ) {
    increaseCharge(owner)
    if (!charged(owner)) return
    delay(200)
    resetCharge(owner)
    owner.addEffect(Stunned(STUNNED_DURATION), owner)
  }

  companion object {
    private const val MAX_CHARGES = 2
    private const val STUNNED_DURATION = 2
  }
}

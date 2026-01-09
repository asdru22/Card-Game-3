package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class Vicious : Trait {
  override val nameRes: Int = R.string.trait_vicious
  override val descriptionRes: Int = R.string.trait_vicious_desc
  override val formatArgs: List<Any> = listOf(DAMAGE_INCREASE)
  override fun modifyOutgoingDamage(
    owner: EntityViewModel,
    target: EntityViewModel,
    amount: Float
  ): Float {
    return if (target.effectManager.effects.any { !it.isPositive }) {
      owner.damage * (1 + DAMAGE_INCREASE / 100f)
    } else {
      amount
    }
  }

  companion object {
    const val DAMAGE_INCREASE = 40f
  }
}
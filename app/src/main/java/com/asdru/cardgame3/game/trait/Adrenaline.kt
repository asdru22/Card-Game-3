package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class Adrenaline : Trait {
  override val nameRes: Int = R.string.trait_adrenaline
  override val descriptionRes: Int = R.string.trait_adrenaline_desc
  override val formatArgs: List<Any> = listOf(DAMAGE, THRESHOLD)
  override fun modifyOutgoingDamage(
    owner: EntityViewModel,
    target: EntityViewModel,
    amount: Float
  ): Float {
    val healthPercentage = owner.health / owner.maxHealth * 100f
    println(healthPercentage)
    if (healthPercentage > THRESHOLD) return amount
    return amount * (1 + DAMAGE / 100f)
  }

  companion object {
    const val THRESHOLD = 25f
    const val DAMAGE = 30f

  }
}
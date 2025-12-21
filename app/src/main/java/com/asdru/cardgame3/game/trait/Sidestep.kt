package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.random.Random

class Sidestep(val dodgeChance: Float) : Trait {
  override val nameRes: Int = R.string.trait_sidestep
  override val descriptionRes: Int = R.string.trait_sidestep_desc
  override val formatArgs: List<Any> = listOf(dodgeChance)

  override fun modifyIncomingDamage(
    owner: EntityViewModel,
    source: EntityViewModel?,
    amount: Float
  ): Float {
    if (Random.nextFloat() < (dodgeChance / 100)) {
      owner.popupManager.add(R.string.game_dodge)
      return 0f
    }
    return amount
  }
}
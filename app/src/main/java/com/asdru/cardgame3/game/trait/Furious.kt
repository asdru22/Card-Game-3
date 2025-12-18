package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class Furious : Trait {
  override val nameRes: Int = R.string.trait_furious
  override val descriptionRes: Int = R.string.trait_furious_desc

  override fun modifyOutgoingDamage(
    owner: EntityViewModel,
    target: EntityViewModel,
    amount: Float
  ): Float {
    return owner.damage * (1 + owner.team.rage / 100f)
  }
}
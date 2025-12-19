package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class Forsaken : Trait {
  override val nameRes: Int = R.string.trait_forsaken
  override val descriptionRes: Int = R.string.trait_forsaken_desc

  override fun modifyHeal(
    owner: EntityViewModel,
    source: EntityViewModel?,
    amount: Float
  ): Float {
    if (owner != source) {
      owner.popupManager.add(R.string.game_forsaken)
      return 0f
    }
    return amount
  }
}
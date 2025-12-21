package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class Ironclad : Trait {
  override val nameRes: Int = R.string.trait_ironclad
  override val descriptionRes: Int = R.string.trait_ironclad_desc
  override val formatArgs: List<Any> = listOf(DAMAGE_IGNORED)

  override fun modifyIncomingDamage(
    owner: EntityViewModel,
    source: EntityViewModel?,
    amount: Float
  ): Float {
    if (amount <= DAMAGE_IGNORED) {
      owner.popupManager.add(R.string.game_ironclad)
      return 0f
    }
    return amount
  }

  companion object {
    const val DAMAGE_IGNORED = 5f
  }
}
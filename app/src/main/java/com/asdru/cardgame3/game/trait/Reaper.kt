package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.helper.heal
import com.asdru.cardgame3.viewModel.EntityViewModel

class Reaper : Trait {
  override val nameRes: Int = R.string.trait_reaper
  override val descriptionRes: Int = R.string.trait_reaper_desc
  override val formatArgs: List<Any> = listOf(HEAL_AMOUNT)

  override suspend fun onEntityDeath(
    owner: EntityViewModel,
    entity: EntityViewModel
  ) {
    if (owner == entity) return
    owner.heal(HEAL_AMOUNT, owner)
  }

  companion object {
    const val HEAL_AMOUNT = 21f
  }
}
package com.asdru.cardgame3.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class QuickDraw : Trait {
  override val nameRes: Int = R.string.trait_quick_draw
  override val descriptionRes: Int = R.string.trait_quick_draw_desc
  override val formatArgs: List<Any> = listOf(DAMAGE)

  override fun onDidReceiveDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float) {
    owner.team?.increaseRage(DAMAGE)
  }

  companion object {
    const val DAMAGE = 9f
  }
}
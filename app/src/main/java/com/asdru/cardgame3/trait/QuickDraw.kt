package com.asdru.cardgame3.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class QuickDraw : Trait {
  override val nameRes: Int = R.string.trait_quick_draw
  override val descriptionRes: Int = R.string.trait_quick_draw_desc
  override val formatArgs: List<Any> = listOf(DAMAGE)

  override suspend fun onEndTurn(owner: EntityViewModel) {
    owner.applyDamage(owner.getRandomEnemy(), DAMAGE)
  }

  companion object {
    const val DAMAGE = 9f
  }
}
package com.asdru.cardgame3.game.trait

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.applyDamage

class QuickDraw : Trait {
  override val nameRes: Int = R.string.trait_quick_draw
  override val descriptionRes: Int = R.string.trait_quick_draw_desc
  override val formatArgs: List<Any> = listOf(DAMAGE)

  override suspend fun onEndTurn(owner: EntityViewModel) {
    val target = owner.team.getRandomTargetableEnemy()
    if (target != null) {
      owner.popupManager.add(R.string.game_quick_draw, Color.White)

      owner.applyDamage(target, DAMAGE)
    }
  }

  companion object {
    const val DAMAGE = 9f
  }
}
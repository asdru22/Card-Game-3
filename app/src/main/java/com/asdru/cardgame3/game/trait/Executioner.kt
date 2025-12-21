package com.asdru.cardgame3.game.trait

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.helper.receiveDamage
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlinx.coroutines.delay

class Executioner : Trait {
  override val nameRes: Int = R.string.trait_executioner
  override val descriptionRes: Int = R.string.trait_executioner_desc
  override val formatArgs: List<Any> = listOf(THRESHOLD)

  override suspend fun onDidDealDamage(
    owner: EntityViewModel,
    target: EntityViewModel,
    amount: Float,
    overkill: Float
  ) {
    if (target.health > THRESHOLD) return
    target.popupManager.add(R.string.game_execute, Color.White)
    target.kill()
  }

  companion object {
    const val THRESHOLD = 6f
  }
}
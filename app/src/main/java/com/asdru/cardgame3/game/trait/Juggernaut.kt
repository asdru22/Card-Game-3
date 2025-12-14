package com.asdru.cardgame3.game.trait

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class Juggernaut : Trait {
  override val nameRes: Int = R.string.trait_juggernaut
  override val descriptionRes: Int = R.string.trait_juggernaut_desc
  override val formatArgs: List<Any> = listOf(MAX_DAMAGE_RECEIVED)

  override fun modifyIncomingDamage(
    owner: EntityViewModel,
    source: EntityViewModel?,
    amount: Float
  ): Float {
    if (amount > MAX_DAMAGE_RECEIVED) {
      owner.popupManager.add(R.string.game_juggernaut, Color.White)
      return MAX_DAMAGE_RECEIVED
    }
    return amount
  }

  companion object {
    const val MAX_DAMAGE_RECEIVED = 25f
  }
}
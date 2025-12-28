package com.asdru.cardgame3.game.trait

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class Juggernaut(
  val maxDamageAllowed: Float
) : Trait {
  override val nameRes: Int = R.string.trait_juggernaut
  override val descriptionRes: Int = R.string.trait_juggernaut_desc
  override val formatArgs: List<Any> = listOf(maxDamageAllowed)

  override fun modifyIncomingDamage(
    owner: EntityViewModel,
    source: EntityViewModel?,
    amount: Float
  ): Float {
    if (amount > maxDamageAllowed) {
      owner.popupManager.add(R.string.game_juggernaut, Color.White)
      return maxDamageAllowed
    }
    return amount
  }
}
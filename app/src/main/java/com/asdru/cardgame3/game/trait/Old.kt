package com.asdru.cardgame3.game.trait

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.viewModel.EntityViewModel

class Old : Trait {
  override val nameRes: Int = R.string.trait_old
  override val descriptionRes: Int = R.string.trait_old_desc
  override val formatArgs: List<Any> = listOf(DAMAGE_AMPLIFICATION)

  override fun modifyIncomingDamage(
    owner: EntityViewModel,
    source: EntityViewModel?,
    amount: Float
  ): Float {
    source?.run {
      if (damageType == DamageType.Magic) {
        owner.popupManager.add(R.string.game_old, Color.White)
        return 0f
      } else {
        return amount * (1 + DAMAGE_AMPLIFICATION / 100f)
      }
    }
    return amount
  }

  companion object {
    const val DAMAGE_AMPLIFICATION = 35f
  }
}
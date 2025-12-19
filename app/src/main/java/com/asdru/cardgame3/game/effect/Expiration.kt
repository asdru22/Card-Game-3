package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel

class Expiration(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {
  override fun onExpire(target: EntityViewModel) {
    target.health = 0f
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_expiration
    override val nameRes = R.string.effect_expiration
    override val descriptionRes = R.string.effect_expiration_desc
    override val isPositive = false
  }
}
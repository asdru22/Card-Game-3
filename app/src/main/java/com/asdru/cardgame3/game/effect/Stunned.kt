package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable

class Stunned(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {
  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_stunned
    override val nameRes = R.string.effect_stunned
    override val descriptionRes = R.string.effect_stunned_desc
    override val isPositive = false
  }
}
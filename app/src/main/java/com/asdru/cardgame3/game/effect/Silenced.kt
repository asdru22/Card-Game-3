package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable

class Silenced(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
) {
  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_silenced
    override val nameRes = R.string.effect_silenced
    override val descriptionRes = R.string.effect_silenced_desc
    override val isPositive = false
  }
}
package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class Overheal(
  duration: Int,
) : StatusEffect(
  nameRes = R.string.effect_overheal,
  descriptionRes = R.string.effect_overheal_desc,
  iconRes = R.drawable.icon_health,
  initialDuration = duration,
  initialMultiplier = 2,
  isPositive = true
) {
    override fun overheal(): Float {
        return 50f
    }
}

package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable

class Watched(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive
) {
  override fun modifyDamage(currentDamage: Float): Float {
    return currentDamage * DAMAGE_REDUCTION /100
  }


  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_watched
    override val nameRes = R.string.effect_watched
    override val descriptionRes = R.string.effect_watched_desc
    override val isPositive = false
    override val formatArgs = listOf(DAMAGE_REDUCTION)

    private const val DAMAGE_REDUCTION = 5f

  }
}

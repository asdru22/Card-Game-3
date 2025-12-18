package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel

class Weakness(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {

  override fun modifyDamage(
    currentDamage: Float,
    owner: EntityViewModel?,
    target: EntityViewModel?
  ): Float {
    return currentDamage * (DAMAGE_DECREASE / 100f)
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_weakness
    override val formatArgs = listOf(DAMAGE_DECREASE)
    override val nameRes = R.string.effect_weakness
    override val descriptionRes = R.string.effect_weakness_desc
    override val isPositive = false
    private const val DAMAGE_DECREASE = 70f
  }
}
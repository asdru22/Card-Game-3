package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel

class Sick(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {

  override suspend fun modifyIncomingHealing(
    owner: EntityViewModel,
    currentHealing: Float,
    source: EntityViewModel?
  ): Float {
    return currentHealing * HEALING_DECREASE / 100f
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_sick
    override val formatArgs = listOf(HEALING_DECREASE)
    override val nameRes = R.string.effect_sick
    override val descriptionRes = R.string.effect_sick_desc
    override val isPositive = false
    private const val HEALING_DECREASE = 50f
  }
}
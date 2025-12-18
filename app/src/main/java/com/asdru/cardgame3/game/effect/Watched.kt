package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel

class Watched(duration: Int) : StatusEffect(
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
    return currentDamage * (1 - (DAMAGE_REDUCTION / 100))
  }


  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_watched
    override val nameRes = R.string.effect_watched
    override val formatArgs = listOf(DAMAGE_REDUCTION)
    override val descriptionRes = R.string.effect_watched_desc
    override val isPositive = false

    private const val DAMAGE_REDUCTION = 5f

  }
}

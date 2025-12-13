package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel

class Defiance(
  duration: Int,
) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  formatArgs = formatArgs,
  isPositive = isPositive
) {

  override suspend fun modifyIncomingDamage(
    owner: EntityViewModel,
    currentDamage: Float,
    source: EntityViewModel?
  ): Float {
    val maxAllowedDamage = (owner.health - 1f).coerceAtLeast(0f)
    return currentDamage.coerceAtMost(maxAllowedDamage)
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_defiance

    override val nameRes = R.string.effect_defiance
    override val descriptionRes = R.string.effect_defiance_desc
    override val isPositive = true
  }
}
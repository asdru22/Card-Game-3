package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel

class Judgement(
  duration: Int,
  private val applier: EntityViewModel
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
    if (applier.isAlive && source == applier) {
      return currentDamage * (1 + DAMAGE_MULTIPLIER / 100f)
    }
    return currentDamage
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_judgement
    override val formatArgs = listOf(DAMAGE_MULTIPLIER)
    override val nameRes = R.string.effect_judgement
    override val descriptionRes = R.string.effect_judgement_desc
    override val isPositive = false

    private const val DAMAGE_MULTIPLIER = 30f
  }
}
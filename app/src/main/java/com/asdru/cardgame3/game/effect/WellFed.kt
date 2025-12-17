package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.helper.heal
import com.asdru.cardgame3.viewModel.EntityViewModel

class WellFed(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {

  override fun modifyDamage(currentDamage: Float): Float {
    return currentDamage * (1f + DAMAGE_INCREASE / 100f)
  }

  override suspend fun onEndTurn(target: EntityViewModel) {
    target.heal(HEAL_AMOUNT)
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_well_fed
    override val formatArgs = listOf(DAMAGE_INCREASE, HEAL_AMOUNT)
    override val nameRes = R.string.effect_well_fed
    override val descriptionRes = R.string.effect_well_fed_desc
    override val isPositive = true
    private const val DAMAGE_INCREASE = 10f
    private const val HEAL_AMOUNT = 6f

  }
}
package com.asdru.cardgame3.game.effect

import androidx.lifecycle.viewModelScope
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.helper.heal
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlinx.coroutines.launch

class Overheal(duration: Int, multiplier: Int = 1) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs,
  initialMultiplier = multiplier
) {
  override fun overheal(): Float {
    return OVERHEAL_AMOUNT
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_overheal
    override val formatArgs = listOf(OVERHEAL_AMOUNT)
    override val nameRes = R.string.effect_overheal
    override val descriptionRes = R.string.effect_overheal_desc
    override val isPositive = true
    private const val OVERHEAL_AMOUNT = 25f
  }
}
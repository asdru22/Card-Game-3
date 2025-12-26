package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.helper.receiveDamage
import com.asdru.cardgame3.viewModel.EntityViewModel

class Inspired(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {
  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_inspired
    override val formatArgs = listOf(EXTRA_NOTES)
    override val nameRes = R.string.effect_inspired
    override val descriptionRes = R.string.effect_inspired_desc
    override val isPositive = true
    public fun getExtraNotes(): Int {
      return EXTRA_NOTES
    }
    private const val EXTRA_NOTES = 2
  }
}
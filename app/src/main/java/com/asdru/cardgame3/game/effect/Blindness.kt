package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.random.Random

class Blindness(duration: Int) : StatusEffect(
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
    if (Random.nextFloat() < (MISS_CHANCE / 100)) {
      target?.popupManager?.add(R.string.game_miss)
      return 0f
    }
    return currentDamage
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_blindness
    override val formatArgs = listOf(MISS_CHANCE)
    override val nameRes = R.string.effect_blindness
    override val descriptionRes = R.string.effect_blindness_desc
    override val isPositive = true

    private const val MISS_CHANCE = 50f

  }
}
package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.random.Random

class Blinded(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {

  override suspend fun modifyOutgoingDamage(
    owner: EntityViewModel,
    currentDamage: Float,
    target: EntityViewModel?
  ): Float {
    if (Random.nextFloat() < (MISS_CHANCE / 100)) {
      owner.popupManager.add(R.string.game_miss)
      return 0f
    }
    return currentDamage
  }


  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_blinded
    override val formatArgs = listOf(MISS_CHANCE)
    override val nameRes = R.string.effect_blinded
    override val descriptionRes = R.string.effect_blinded_desc
    override val isPositive = false

    private const val MISS_CHANCE = 70f

  }
}
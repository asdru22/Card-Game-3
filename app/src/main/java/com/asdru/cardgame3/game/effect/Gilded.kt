package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel

class Gilded(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {
  override suspend fun modifyIncomingDamage(
    owner: EntityViewModel,
    currentDamage: Float,
    source: EntityViewModel?
  ): Float {
    owner.team.shop.modifyCoins(COINS_GAINED)
    return currentDamage
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_gilded
    override val formatArgs = listOf(COINS_GAINED)
    override val nameRes = R.string.effect_gilded
    override val descriptionRes = R.string.effect_gilded_desc
    override val isPositive = true

    private const val COINS_GAINED = 4
  }
}
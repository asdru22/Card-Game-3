package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.helper.heal
import com.asdru.cardgame3.viewModel.EntityViewModel

class Vampirism(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {

  override suspend fun postDamageDealt(
    owner: EntityViewModel,
    target: EntityViewModel,
    amount: Float
  ) {
    owner.heal(amount * HEAL_AMOUNT / 100f)
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_vampirism
    override val formatArgs = listOf(HEAL_AMOUNT)
    override val nameRes = R.string.effect_vampirism
    override val descriptionRes = R.string.effect_vampirism_desc
    override val isPositive = true
    private const val HEAL_AMOUNT = 30f
  }
}
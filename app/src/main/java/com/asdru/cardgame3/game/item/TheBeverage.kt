package com.asdru.cardgame3.game.item

import com.asdru.cardgame3.R
import com.asdru.cardgame3.game.effect.StatusEffect

private const val EFFECT_DURATION = 2
private const val EFFECT_NUMBER = 4

object TheBeverage : ShopItem(
  nameRes = R.string.item_the_beverage,
  descriptionRes = R.string.item_the_beverage_desc,
  cost = 40,
  iconRes = R.drawable.item_the_beverage,
  formatArgs = listOf(EFFECT_NUMBER, EFFECT_DURATION),
  onApply = {
    repeat(EFFECT_NUMBER) { _ ->
      val randomEffect = StatusEffect.getRandom(EFFECT_DURATION, null, it)
      randomEffect?.let{effect ->
        it.addEffect(effect)
      }
    }
  }
)
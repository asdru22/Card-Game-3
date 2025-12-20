package com.asdru.cardgame3.game.item

import com.asdru.cardgame3.R
import com.asdru.cardgame3.game.effect.WellFed

private const val EFFECT_DURATION = 2

object Hamburger : ShopItem(
  nameRes = R.string.item_hamburger,
  descriptionRes = R.string.item_hamburger_desc,
  cost = 40,
  iconRes = R.drawable.item_hamburger,
  formatArgs = listOf(WellFed.Spec, EFFECT_DURATION),
  onApply = {
    it.addEffect(WellFed(EFFECT_DURATION), it)
  }
)
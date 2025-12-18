package com.asdru.cardgame3.game.item

import com.asdru.cardgame3.R

private const val RAGE_INCREASE = 20f


object Chili : ShopItem(
  nameRes = R.string.item_chili,
  descriptionRes = R.string.item_chili_desc,
  cost = 30,
  iconRes = R.drawable.item_chili,
  formatArgs = listOf(RAGE_INCREASE),
  onApply = {
    it.team.increaseRage(RAGE_INCREASE)
  }
)
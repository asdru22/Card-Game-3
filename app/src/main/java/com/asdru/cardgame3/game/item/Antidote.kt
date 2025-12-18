package com.asdru.cardgame3.game.item

import com.asdru.cardgame3.R

object Antidote : ShopItem(
  id = "potion_small",
  nameRes = R.string.item_antidote,
  descriptionRes = R.string.item_antidote_desc,
  cost = 40,
  iconRes = R.drawable.item_antidote,
  onApply = {
    it.effectManager.clearNegative(it)
  }
)
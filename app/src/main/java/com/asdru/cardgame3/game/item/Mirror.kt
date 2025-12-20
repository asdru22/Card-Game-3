package com.asdru.cardgame3.game.item

import com.asdru.cardgame3.R

object Mirror : ShopItem(
  nameRes = R.string.item_mirror,
  descriptionRes = R.string.item_mirror_desc,
  cost = 40,
  iconRes = R.drawable.item_mirror,
  onApply = {
    it.entity.passiveAbility.effect(it, it)
  }
)
package com.asdru.cardgame3.game.item

import com.asdru.cardgame3.R
import com.asdru.cardgame3.helper.heal

private const val HEAL_AMOUNT = 15f

object HealingPotion : ShopItem(
  nameRes = R.string.item_healing_potion,
  descriptionRes = R.string.item_healing_potion_desc,
  cost = 30,
  iconRes = R.drawable.item_healing_potion,
  formatArgs = listOf(HEAL_AMOUNT),
  onApply = {
    it.heal(HEAL_AMOUNT)
  }
)
package com.asdru.cardgame3.helper

import com.asdru.cardgame3.game.item.ShopItem

object ShopDataProvider {

  fun getShopItems(): List<ShopItem> {
    return ShopItem::class.sealedSubclasses
      .mapNotNull { it.objectInstance }
  }
}
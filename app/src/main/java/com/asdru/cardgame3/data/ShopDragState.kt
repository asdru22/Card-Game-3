package com.asdru.cardgame3.data

import com.asdru.cardgame3.game.item.ShopItem

data class ShopDragState(
  val item: ShopItem,
  val teamIsLeft: Boolean,
  val start: androidx.compose.ui.geometry.Offset,
  val current: androidx.compose.ui.geometry.Offset
)
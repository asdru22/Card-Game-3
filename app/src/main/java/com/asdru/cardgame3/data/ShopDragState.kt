package com.asdru.cardgame3.data

data class ShopDragState(
    val item: ShopItem,
    val teamIsLeft: Boolean,
    val start: androidx.compose.ui.geometry.Offset,
    val current: androidx.compose.ui.geometry.Offset
)
package com.asdru.cardgame3.data

import com.asdru.cardgame3.viewModel.EntityViewModel

data class ShopItem(
    val name: String,
    val cost: Int,
    val iconRes: Int,
    val onApply: (EntityViewModel) -> Unit
)
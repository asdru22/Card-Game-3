package com.asdru.cardgame3.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.asdru.cardgame3.viewModel.EntityViewModel

data class ShopItem(
  val id: String,
  @get:DrawableRes val iconRes: Int,
  val cost: Int,
  @get:StringRes override val nameRes: Int,
  @get:StringRes override val descriptionRes: Int,
  override val formatArgs: List<Any> = emptyList(),
  val onApply: (EntityViewModel) -> Unit
) : Translatable
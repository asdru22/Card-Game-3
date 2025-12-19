package com.asdru.cardgame3.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color

sealed class Entity(
  @field:StringRes val name: Int,
  @field:DrawableRes val iconRes: Int,
  val initialStats: Stats,
  val color: Color,
  val traits: List<Trait> = emptyList()
)
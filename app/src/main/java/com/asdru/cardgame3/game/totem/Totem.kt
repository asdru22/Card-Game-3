package com.asdru.cardgame3.game.totem

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Totem(
  @param:StringRes val name: Int,
  @param:DrawableRes val iconRes: Int,
  val maxHealth: Float
)

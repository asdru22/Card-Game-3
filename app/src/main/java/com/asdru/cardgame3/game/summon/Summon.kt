package com.asdru.cardgame3.game.summon

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.Stats

sealed class Entity(
  @field:StringRes val name: Int,
  @field:DrawableRes val iconRes: Int,
  val initialStats: Stats,
  val color: Color,
  val ability: Ability,
)
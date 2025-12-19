package com.asdru.cardgame3.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color

class Summon(
  val ability: SummonAbility,
  @StringRes name: Int,
  @DrawableRes iconRes: Int,
  initialStats: Stats,
  color: Color,
  traits: List<Trait> = emptyList()
) : Entity(
  name = name,
  iconRes = iconRes,
  initialStats = initialStats,
  color = color,
  traits = traits
)
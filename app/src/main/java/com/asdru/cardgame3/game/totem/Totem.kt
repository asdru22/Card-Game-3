package com.asdru.cardgame3.game.totem

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.data.TotemAbility

sealed class Totem(
  @param:StringRes val name: Int,
  @param:DrawableRes val iconRes: Int,
  val initialStats: Stats,
  val activeAbility: TotemAbility,
  val passiveAbility: TotemAbility
)

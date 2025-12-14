package com.asdru.cardgame3.game.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.trait.Trait

sealed class Entity(
  @field:StringRes val name: Int,
  @field:DrawableRes val iconRes: Int,
  val damageType: DamageType,
  val initialStats: Stats,
  val color: Color,
  val radarStats: RadarStats,
  val passiveAbility: Ability,
  val activeAbility: Ability,
  val ultimateAbility: Ability,
  val traits: List<Trait> = emptyList()
)
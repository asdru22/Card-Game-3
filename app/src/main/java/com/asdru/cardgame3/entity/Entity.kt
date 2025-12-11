package com.asdru.cardgame3.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.entityFeatures.Ability
import com.asdru.cardgame3.entityFeatures.DamageType
import com.asdru.cardgame3.entityFeatures.Stats
import com.asdru.cardgame3.trait.Trait

sealed class Entity(
  @field:StringRes val name: Int,
  @field:DrawableRes val iconRes: Int,
  val damageType : DamageType,
  val initialStats: Stats,
  val color: Color,
  val passiveAbility: Ability,
  val activeAbility: Ability,
  val ultimateAbility: Ability,
  val traits: List<Trait> = emptyList()
)
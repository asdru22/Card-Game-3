package com.asdru.cardgame3.game.totem

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.TotemAbility
import com.asdru.cardgame3.game.effect.Sick
import com.asdru.cardgame3.game.effect.Vampirism
import com.asdru.cardgame3.helper.receiveDamage

private const val ACTIVE_DURATION = 2
private const val ACTIVE_DAMAGE = 5f

private const val PASSIVE_DURATION = 1

object Bat : Totem(
  name = R.string.totem_bat,
  iconRes = R.drawable.totem_bat,
  initialHealth = 200f,
  cost = 50,
  activeAbility = TotemAbility(
    nameRes = R.string.tability_viral_charge,
    descriptionRes = R.string.tability_viral_charge_desc,
    formatArgs = listOf(ACTIVE_DAMAGE, Sick.Spec, ACTIVE_DURATION)
  ) { sourceTotem, target ->
    target.receiveDamage(ACTIVE_DAMAGE)
    target.addEffect(Sick(ACTIVE_DURATION))
  },
  passiveAbility = TotemAbility(
    nameRes = R.string.tability_leech,
    descriptionRes = R.string.tability_leech_desc,
    formatArgs = listOf(Vampirism.Spec, PASSIVE_DURATION)
  ) { sourceTotem, target ->
    target.addEffect(Vampirism(PASSIVE_DURATION))
  }
)
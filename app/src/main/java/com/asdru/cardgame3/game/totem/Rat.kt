package com.asdru.cardgame3.game.totem

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.TotemAbility
import com.asdru.cardgame3.helper.receiveDamage

private const val ACTIVE_DAMAGE = 10f
private const val PASSIVE_INCREASE = 7f

object Rat : Totem(
  name = R.string.totem_rat,
  iconRes = R.drawable.totem_rat,
  initialHealth = 180f,
  cost = 45,
  activeAbility = TotemAbility(
    nameRes = R.string.tability_feral_bite,
    descriptionRes = R.string.tability_feral_bite_desc,
    formatArgs = listOf(ACTIVE_DAMAGE)
  ) { sourceTotem, target ->
    target.receiveDamage(ACTIVE_DAMAGE * (1 + target.team.enemyTeam.rage / 100))
  },
  passiveAbility = TotemAbility(
    nameRes = R.string.tability_bite,
    descriptionRes = R.string.tability_bite_desc,
    formatArgs = listOf(PASSIVE_INCREASE)
  ) { sourceTotem, target ->
    target.team.increaseRage(PASSIVE_INCREASE)
  }
)
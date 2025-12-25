package com.asdru.cardgame3.game.totem

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.TotemAbility

private const val ACTIVE_DURATION = 2
private const val ACTIVE_DAMAGE = 3f
private const val PASSIVE_DURATION = 1

object Flask : Totem(
  name = R.string.totem_flask,
  iconRes = R.drawable.totem_flask,
  initialHealth = 105f,
  cost = 60,
  activeAbility = TotemAbility(
    nameRes = R.string.tability_dispel,
    descriptionRes = R.string.tability_dispel_desc,
  ) { sourceTotem, target ->
    target.effectManager.clearPositive(target, ignoreMultipliers = false)
  },
  passiveAbility = TotemAbility(
    nameRes = R.string.tability_purify,
    descriptionRes = R.string.tability_purify_desc,
  ) { sourceTotem, target ->
    target.effectManager.clearNegative(target, ignoreMultipliers = false)
  }
)
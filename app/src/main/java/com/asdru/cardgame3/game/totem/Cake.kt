package com.asdru.cardgame3.game.totem

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.TotemAbility
import com.asdru.cardgame3.game.effect.Burning
import com.asdru.cardgame3.game.effect.WellFed

private const val ACTIVE_DURATION = 1
private const val PASSIVE_DURATION = 1

object Cake : Totem(
  name = R.string.totem_cake,
  iconRes = R.drawable.totem_cake,
  initialHealth = 190f,
  cost = 47,
  activeAbility = TotemAbility(
    nameRes = R.string.tability_candle_fire,
    descriptionRes = R.string.tability_candle_fire_desc,
    formatArgs = listOf(Burning.Spec, ACTIVE_DURATION)
  ) { sourceTotem, target ->
    target.addEffect(Burning(ACTIVE_DURATION), null)
  },
  passiveAbility = TotemAbility(
    nameRes = R.string.tability_yum,
    descriptionRes = R.string.tability_yum_desc,
    formatArgs = listOf(WellFed.Spec, PASSIVE_DURATION)
  ) { sourceTotem, target ->
    target.addEffect(WellFed(PASSIVE_DURATION), null)
  }
)
package com.asdru.cardgame3.game.totem

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.TotemAbility
import com.asdru.cardgame3.helper.heal
import com.asdru.cardgame3.helper.receiveDamage
import kotlinx.coroutines.delay

private const val DAMAGE_AMOUNT = 3f
private const val ACTIVE_REPEATS = 3
private const val HEAL_AMOUNT = 7f

object Cat : Totem(
  name = R.string.totem_cat,
  iconRes = R.drawable.totem_cat,
  initialHealth = 160f,
  cost = 50,
  activeAbility = TotemAbility(
    nameRes = R.string.tability_scratch,
    descriptionRes = R.string.tability_scratch_desc,
    formatArgs = listOf(ACTIVE_REPEATS, DAMAGE_AMOUNT)
  ) { sourceTotem, target ->
    repeat(ACTIVE_REPEATS) {
      target.receiveDamage(DAMAGE_AMOUNT)
      delay(200)
    }
  },
  passiveAbility = TotemAbility(
    nameRes = R.string.tability_purr,
    descriptionRes = R.string.tability_purr_desc,
    formatArgs = listOf(HEAL_AMOUNT)
  ) { sourceTotem, target ->
    target.heal(HEAL_AMOUNT)
  }
)
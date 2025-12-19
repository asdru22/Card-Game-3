package com.asdru.cardgame3.character

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.Character
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats

class Archer : Character(
  name = R.string.entity_archer,
  iconRes = R.drawable.entity_archer,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF0893FF),
  damageType = DamageType.Ranged,
  radarStats = RadarStats(0.8f, 0.3f, 0f, 0.5f, 0.3f),
  activeAbility = Ability(
    nameRes = R.string.ability_arrow_rain,
    descriptionRes = R.string.ability_arrow_rain_desc,
    formatArgs = listOf(ACTIVE_REPEATS)
  ) { source, target ->
    source.combatManager.applyDamage(target, 10f)
    source.addSummon()
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_cover,
    descriptionRes = R.string.ability_cover_desc,
    formatArgs = listOf(43, PASSIVE_DURATION)
  ) { source, target ->
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_rain_fire,
    descriptionRes = R.string.ability_rain_fire_desc,
    formatArgs = listOf(ULTIMATE_REPEATS, "POOP", ULTIMATE_BURN_DURATION)
  ) { source, randomEnemy ->

  }
) {
  private companion object {
    const val MAX_HEALTH = 110f
    const val DAMAGE = 9f
    const val ACTIVE_REPEATS = 2
    const val PASSIVE_DURATION = 2
    const val ULTIMATE_REPEATS = 6
    const val ULTIMATE_BURN_DURATION = 3
  }
}
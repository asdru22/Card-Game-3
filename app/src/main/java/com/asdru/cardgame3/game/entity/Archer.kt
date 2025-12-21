package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Burning
import com.asdru.cardgame3.game.effect.PainLink
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.helper.applyDamageToTargets

class Archer : Entity(
  name = R.string.entity_archer,
  iconRes = R.drawable.entity_archer,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF0893FF),
  damageType = DamageType.Ranged,
  radarStats = RadarStats(0.8f, 0.3f, 0f, 0.5f, 0.3f),
  activeAbility = Ability(
    nameRes = R.string.ability_arrow_rain,
    descriptionRes = R.string.ability_arrow_rain_desc,
    formatArgs = listOf(ACTIVE_REPEATS,ACTIVE_CHARGES),
    charges = ACTIVE_CHARGES
  ) { source, target ->
    source.applyDamageToTargets(
      target.team.getAliveMembers(),
      repeats = ACTIVE_REPEATS,
      delayTime = 200L
    )
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_cover,
    descriptionRes = R.string.ability_cover_desc,
    formatArgs = listOf(PainLink.Spec, PASSIVE_DURATION)
  ) { source, target ->
    source.addEffect(PainLink(PASSIVE_DURATION, target), source = source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_rain_fire,
    descriptionRes = R.string.ability_rain_fire_desc,
    formatArgs = listOf(ULTIMATE_REPEATS, Burning.Spec, ULTIMATE_BURN_DURATION)
  ) { source, randomEnemy ->
    source.applyDamage(
      randomEnemy,
      repeats = ULTIMATE_REPEATS,
      delayTime = 150L,
      effects = listOf(Burning(ULTIMATE_BURN_DURATION))
    )
  }
) {
  private companion object {
    const val MAX_HEALTH = 105f
    const val DAMAGE = 9f
    const val ACTIVE_CHARGES = 2
    const val ACTIVE_REPEATS = 2
    const val PASSIVE_DURATION = 2
    const val ULTIMATE_REPEATS = 6
    const val ULTIMATE_BURN_DURATION = 3
  }
}
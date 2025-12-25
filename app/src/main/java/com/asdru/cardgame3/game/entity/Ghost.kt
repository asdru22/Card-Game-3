package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Expiration
import com.asdru.cardgame3.game.effect.Vanish
import com.asdru.cardgame3.game.trait.Forsaken
import com.asdru.cardgame3.game.trait.Reaper
import com.asdru.cardgame3.game.trait.Undead
import com.asdru.cardgame3.helper.applyDamageToTargets

class Ghost : Entity(
  name = R.string.entity_ghost,
  iconRes = R.drawable.entity_ghost,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF82A9B9),
  damageType = DamageType.Magic,
  traits = listOf(Forsaken(), Reaper(), Undead()),
  radarStats = RadarStats(0.5f, 0.7f, 0.5f, 0.7f, 0.6f),
  activeAbility = Ability(
    nameRes = R.string.ability_spook,
    descriptionRes = R.string.ability_spook_desc,
  ) { source, _ ->
    source.applyDamageToTargets(source.team.getTargetableEnemies())
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_dissolve,
    descriptionRes = R.string.ability_dissolve_desc,
    charges = PASSIVE_CHARGES,
    formatArgs = listOf(
      Vanish.Spec,
      PASSIVE_DURATION,
      PASSIVE_CHARGES
    )
  ) { source, target ->
    target.addEffect(Vanish(PASSIVE_DURATION), source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_death_mark,
    descriptionRes = R.string.ability_death_mark_desc,
    formatArgs = listOf(
      Expiration.Spec,
      ULTIMATE_EFFECT_DURATION
    )
  ) { source, _ ->
    val weakestEnemy = source.team.getTargetableEnemies().minByOrNull { it.health }
    weakestEnemy?.addEffect(Expiration(ULTIMATE_EFFECT_DURATION), source)
  }
) {
  private companion object {
    const val MAX_HEALTH = 140f
    const val DAMAGE = 9f
    const val PASSIVE_DURATION = 1
    const val PASSIVE_CHARGES = 2
    const val ULTIMATE_EFFECT_DURATION = 3
  }
}
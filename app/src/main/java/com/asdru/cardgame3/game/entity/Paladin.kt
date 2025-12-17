package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Protection
import com.asdru.cardgame3.game.effect.SpikedShield
import com.asdru.cardgame3.game.effect.Taunt
import com.asdru.cardgame3.game.trait.Spite
import com.asdru.cardgame3.helper.applyDamage

class Paladin : Entity(
  name = R.string.entity_paladin,
  iconRes = R.drawable.entity_paladin,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF8BC34A),
  damageType = DamageType.Melee,
  traits = listOf(Spite()),
  radarStats = RadarStats(0.3f, 0.8f, 0.6f, 0.6f, 0.5f),
  activeAbility = Ability(
    nameRes = R.string.ability_challenge,
    descriptionRes = R.string.ability_challenge_desc,
    formatArgs = listOf(
      Taunt.Spec,
      ACTIVE_DURATION
    )
  ) { source, target ->
    source.applyDamage(target, effects = listOf(Taunt(ACTIVE_DURATION)))
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_guard,
    descriptionRes = R.string.ability_guard_desc,
    formatArgs = listOf(
      Protection.Spec,
      PASSIVE_DURATION
    )
  ) { source, target ->
    target.addEffect(Protection(PASSIVE_DURATION), source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_martyr,
    descriptionRes = R.string.ability_martyr_desc,
    formatArgs = listOf(
      SpikedShield.Spec,
      ULTIMATE_SHIELD_DURATION,
      Taunt.Spec,
      ULTIMATE_TAUNT_DURATION
    )
  ) { source, _ ->
    source.team.getTargetableEnemies().forEach { enemy ->
      enemy.addEffect(Taunt(ULTIMATE_TAUNT_DURATION), source)
    }
    source.addEffect(SpikedShield(ULTIMATE_SHIELD_DURATION), source)
  }
) {
  private companion object {
    const val MAX_HEALTH = 230f
    const val DAMAGE = 12f
    const val ACTIVE_DURATION = 2
    const val PASSIVE_DURATION = 4
    const val ULTIMATE_TAUNT_DURATION = 2
    const val ULTIMATE_SHIELD_DURATION = 3
  }
}
package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Silenced
import com.asdru.cardgame3.game.effect.StatusEffect
import com.asdru.cardgame3.game.effect.WellFed
import com.asdru.cardgame3.helper.applyDamage

class MrKettle : Entity(
  name = R.string.entity_mr_kettle,
  iconRes = R.drawable.entity_mr_kettle,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFFFB700),
  damageType = DamageType.Ranged,
  radarStats = RadarStats(0.4f, 0.5f, 0.8f, 0.8f, 0.4f),
  activeAbility = Ability(
    nameRes = R.string.ability_heckle,
    descriptionRes = R.string.ability_heckle_desc,
    formatArgs = listOf(
      Silenced.Spec,
      ACTIVE_DURATION
    )
  ) { source, target ->
    source.applyDamage(target, effects = listOf(Silenced(ACTIVE_DURATION)))
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_chefs_surprise,
    descriptionRes = R.string.ability_chefs_surprise_desc,
    formatArgs = listOf(
      PASSIVE_EFFECT_NUMBER,
      PASSIVE_EFFECT_DURATION
    )
  ) { source, target ->
    repeat(PASSIVE_EFFECT_NUMBER) {
      val randomBuff = StatusEffect.getRandomPositive(
        duration = PASSIVE_EFFECT_DURATION,
        applier = source,
        target = target
      )
      if (randomBuff != null) {
        target.addEffect(randomBuff, source)
      }
    }
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_feast,
    descriptionRes = R.string.ability_feast_desc,
    formatArgs = listOf(
      WellFed.Spec,
      ULTIMATE_EFFECT_DURATION,
    )
  ) { source, _ ->
    source.team.getAliveMembers()
      .forEach { it.addEffect(WellFed(ULTIMATE_EFFECT_DURATION), source = source) }
  }
) {
  companion object {
    const val MAX_HEALTH = 180f
    const val DAMAGE = 12f
    const val ACTIVE_DURATION = 2
    const val PASSIVE_EFFECT_NUMBER = 2
    const val PASSIVE_EFFECT_DURATION = 1
    const val ULTIMATE_EFFECT_DURATION = 2
  }
}
package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Strength
import com.asdru.cardgame3.game.effect.Vanish
import com.asdru.cardgame3.game.trait.Sidestep
import com.asdru.cardgame3.helper.applyDamage

class Jester : Entity(
  name = R.string.entity_jester,
  iconRes = R.drawable.entity_jester,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFBBFC79),
  traits = listOf(Sidestep(5f)),
  damageType = DamageType.Melee,
  radarStats = RadarStats(0.7f, 0.5f, 0.3f, 0.0f, 0.2f),
  activeAbility = Ability(
    nameRes = R.string.ability_slash,
    descriptionRes = R.string.ability_slash_desc,
    formatArgs = listOf(
      ACTIVE_REPEATS
    )
  ) { source, target ->
    source.applyDamage(target, repeats = ACTIVE_REPEATS, delayTime = 300)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_warriors_blessing,
    descriptionRes = R.string.ability_warriors_blessing_desc,
    formatArgs = listOf(
      Strength.Spec,
      PASSIVE_DURATION
    )
  ) { source, target ->
    target.addEffect(Strength(PASSIVE_DURATION), source = source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_vanish,
    descriptionRes = R.string.ability_vanish_desc,
    formatArgs = listOf(
      Vanish.Spec,
      ULTIMATE_DURATION
    )
  ) { source, _ ->
    source.addEffect(Vanish(ULTIMATE_DURATION), source = source)
  }
) {
  private companion object {
    const val MAX_HEALTH = 120f
    const val DAMAGE = 12f
    const val ACTIVE_REPEATS = 3
    const val PASSIVE_DURATION = 3
    const val ULTIMATE_DURATION = 2
  }
}
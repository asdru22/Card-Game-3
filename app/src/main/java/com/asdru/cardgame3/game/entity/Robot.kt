package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Electrified
import com.asdru.cardgame3.game.effect.Overloaded
import com.asdru.cardgame3.game.effect.Stunned
import com.asdru.cardgame3.game.trait.Meltdown
import com.asdru.cardgame3.viewModel.applyDamage

class Robot : Entity(
  name = R.string.entity_robot,
  iconRes = R.drawable.entity_robot,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF44F7FD),
  damageType = DamageType.Magic,
  traits = listOf(Meltdown()),
  radarStats = RadarStats(0.6f, 0.2f, 0.3f, 0.6f, 0.4f),
  activeAbility = Ability(
    nameRes = R.string.ability_shock_attack,
    descriptionRes = R.string.ability_shock_attack_desc,
    formatArgs = listOf(Electrified.Spec, ACTIVE_DURATION)
  ) { source, target ->
    source.applyDamage(target, effects = listOf(Electrified(ACTIVE_DURATION, source)))
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_overload,
    descriptionRes = R.string.ability_overload_desc,
    formatArgs = listOf(Overloaded.Spec, PASSIVE_DURATION)
  ) { source, target ->
    target.addEffect(Overloaded(PASSIVE_DURATION), source = source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_shutdown,
    descriptionRes = R.string.ability_shutdown_desc,
    formatArgs = listOf(Stunned.Spec, ULTIMATE_STUNNED_DURATION)
  ) { source, randomEnemy ->
    randomEnemy.team.getAliveMembers()
      .filter { it.effectManager.effects.any { effect -> effect is Electrified } }
      .forEach { it.addEffect(Stunned(ULTIMATE_STUNNED_DURATION), source) }
  }
) {
  private companion object {
    const val MAX_HEALTH = 180f
    const val DAMAGE = 0f
    const val ACTIVE_DURATION = 3
    const val PASSIVE_DURATION = 3
    const val ULTIMATE_STUNNED_DURATION = 3
  }
}
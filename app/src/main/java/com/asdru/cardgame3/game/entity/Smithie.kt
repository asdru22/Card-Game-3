package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Defiance
import com.asdru.cardgame3.game.effect.Stunned
import com.asdru.cardgame3.game.trait.Firewall
import com.asdru.cardgame3.game.trait.Juggernaut
import com.asdru.cardgame3.viewModel.applyDamage
import com.asdru.cardgame3.viewModel.applyDamageToTargets
import com.asdru.cardgame3.viewModel.receiveDamage

class Smithie : Entity(
  name = R.string.entity_smithie,
  iconRes = R.drawable.entity_smithie,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF107AD7),
  damageType = DamageType.Melee,
  traits = listOf(Juggernaut(), Firewall()),
  radarStats = RadarStats(0.6f, 0.9f, 0.4f, 0.7f, 0.4f),
  activeAbility = Ability(
    nameRes = R.string.ability_slam,
    descriptionRes = R.string.ability_slam_desc,
    charges = ACTIVE_CHARGE,
    formatArgs = listOf(
      Stunned.Spec,
      ACTIVE_DURATION,
      ACTIVE_CHARGE
    )
  ) { source, target ->
    source.applyDamage(target, effects = listOf(Stunned(ACTIVE_DURATION)))
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_distribution,
    descriptionRes = R.string.ability_distribution_desc,
    charges = PASSIVE_CHARGE,
    formatArgs = listOf(
      PASSIVE_HEALTH_PERCENTAGE,
      PASSIVE_CHARGE
    )
  ) { source, target ->
    val damageAmount = target.maxHealth * PASSIVE_HEALTH_PERCENTAGE / 100f
    target.receiveDamage(damageAmount)
    source.applyDamageToTargets(
      source.team.getAliveEnemies(),
      damageAmount,
      playAttackAnimation = false
    )
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_salvation,
    descriptionRes = R.string.ability_salvation_desc,
    formatArgs = listOf(
      Defiance.Spec,
      ULTIMATE_DURATION
    )
  ) { source, _ ->
    val lowestHealthEntity = source.team.getAliveMembers().minBy { it.health }
    lowestHealthEntity.addEffect(Defiance(ULTIMATE_DURATION), source)
  }
) {
  private companion object {
    const val MAX_HEALTH = 175f
    const val DAMAGE = 41f
    const val ACTIVE_DURATION = 1
    const val ACTIVE_CHARGE = 3
    const val PASSIVE_CHARGE = 2
    const val PASSIVE_HEALTH_PERCENTAGE = 7
    const val ULTIMATE_DURATION = 2

  }
}
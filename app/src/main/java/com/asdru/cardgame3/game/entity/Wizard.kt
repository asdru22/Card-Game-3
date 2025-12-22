package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.trait.Overkill
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.helper.heal
import com.asdru.cardgame3.helper.withTemporaryDamage
import com.asdru.cardgame3.logic.BattleCombatLogic

class Wizard : Entity(
  name = R.string.entity_wizard,
  iconRes = R.drawable.entity_wizard,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF9C27B0),
  damageType = DamageType.Magic,
  traits = listOf(Overkill()),
  radarStats = RadarStats(0.7f, 0.1f, 0.5f, 0.2f, 0.3f),
  activeAbility = Ability(
    nameRes = R.string.ability_zap,
    descriptionRes = R.string.ability_zap_desc,
    formatArgs = listOf(
      ACTIVE_CHARGES
    ),
    charges = ACTIVE_CHARGES,
  ) { source, target ->
    source.applyDamage(target)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_override,
    descriptionRes = R.string.ability_override_desc,
    formatArgs = listOf(
      PASSIVE_DAMAGE_PERCENTAGE
    )
  ) { _, target ->
    if (!target.isAlive) return@Ability
    val enemies = target.team.getTargetableEnemies()

    if (enemies.isNotEmpty()) {
      val randomEnemy = enemies.random()
      val reducedDamage = target.damage * PASSIVE_DAMAGE_PERCENTAGE / 100f

      target.withTemporaryDamage(reducedDamage) {
        BattleCombatLogic.performActiveAbility(target, randomEnemy)
      }
    }

  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_blessing,
    descriptionRes = R.string.ability_blessing_desc,
    formatArgs = listOf(
      ULTIMATE_HEAL_AMOUNT
    )
  ) { source, _ ->
    source.team.getAliveMembers().forEach {
      it.heal(ULTIMATE_HEAL_AMOUNT, source)
      it.effectManager.clearNegative(it, ignoreMultipliers = false)
    }
  }
) {
  private companion object {
    const val MAX_HEALTH = 150f
    const val DAMAGE = 32f
    const val ACTIVE_CHARGES = 2
    const val PASSIVE_DAMAGE_PERCENTAGE = 50
    const val ULTIMATE_HEAL_AMOUNT = 18f
  }
}
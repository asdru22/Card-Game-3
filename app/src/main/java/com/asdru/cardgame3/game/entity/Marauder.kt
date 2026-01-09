package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Burning
import com.asdru.cardgame3.game.effect.PainLink
import com.asdru.cardgame3.game.effect.Taunt
import com.asdru.cardgame3.game.trait.Vicious
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.helper.applyDamageToTargets
import com.asdru.cardgame3.logic.BattleCombatLogic

class Marauder : Entity(
  name = R.string.entity_marauder,
  iconRes = R.drawable.entity_marauder,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE, damageMultiplier = DMG_MULT),
  color = Color(0xFF82B98C),
  damageType = DamageType.Ranged,
  traits = listOf(Vicious()),
  radarStats = RadarStats(0.7f, 0.2f, 0f, 0.7f, 0.4f),
  activeAbility = Ability(
    nameRes = R.string.ability_boomerang,
    descriptionRes = R.string.ability_boomerang_desc,
    formatArgs = listOf(DMG_MULT, ACTIVE_REPEATS)
  ) { source, target ->
    source.applyDamage(target, repeats = ACTIVE_REPEATS)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_scapegoat,
    descriptionRes = R.string.ability_scapegoat_desc,
    formatArgs = listOf(Taunt.Spec, PASSIVE_DURATION, PASSIVE_CHARGE),
    charges = PASSIVE_CHARGE
  ) { source, target ->
    val enemies = source.team.getAliveEnemies()
    enemies.forEach { it.addEffect(Taunt(PASSIVE_DURATION), source = target) }
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_mutiny,
    descriptionRes = R.string.ability_mutiny_desc,
  ) { source, randomEnemy ->
    val enemies = source.team.getAliveEnemies().filter { !it.effectManager.isStunned }
    if (enemies.size >= 2) {
      val combatants = enemies.shuffled()
      combatants.forEachIndexed { index, attacker ->
        val targetIndex = (index + 1) % combatants.size
        val target = combatants[targetIndex]
        attacker.activeAbility.effect(attacker, target)
      }
    }
  }
) {
  private companion object {
    const val MAX_HEALTH = 130f
    const val DAMAGE = 11f
    const val DMG_MULT = 100f
    const val ACTIVE_REPEATS = 2
    const val PASSIVE_DURATION = 2
    const val PASSIVE_CHARGE = 2
  }
}
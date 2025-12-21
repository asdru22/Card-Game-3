package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Judgement
import com.asdru.cardgame3.game.effect.StatusEffect
import com.asdru.cardgame3.game.trait.Executioner
import com.asdru.cardgame3.helper.applyDamage

class TheMagnet : Entity(
  name = R.string.entity_the_magnet,
  iconRes = R.drawable.entity_the_magnet,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF416C0E),
  damageType = DamageType.Melee,
  traits = listOf(Executioner()),
  radarStats = RadarStats(0.6f, 0.2f, 0.1f, 0.8f, 0.5f),
  activeAbility = Ability(
    nameRes = R.string.ability_code_review,
    descriptionRes = R.string.ability_code_review_desc,
    formatArgs = listOf(
      Judgement.Spec,
      ACTIVE_DURATION
    )
  ) { source, target ->
    source.applyDamage(target, effects = listOf(Judgement(ACTIVE_DURATION, source)))
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_copy_my_code,
    descriptionRes = R.string.ability_copy_my_code_desc,
  ) { source, target ->

    val randomEnemy = source.team.getRandomTargetableEnemy()
    randomEnemy?.let {
      source.entity.activeAbility.effect(target, randomEnemy)
    }
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_refactoring,
    descriptionRes = R.string.ability_refactoring_desc,
    formatArgs = listOf(
      ULTIMATE_EFFECT_DURATION,
    )
  ) { source, _ ->
    val enemies = source.team.getAliveEnemies()

    enemies.forEach { enemy ->
      // remove all positive effects and get the count of how many were removed
      val buffsRemovedCount = enemy.effectManager.clearPositive(enemy)

      // for each removed buff, add a random debuff
      repeat(buffsRemovedCount) {
        val randomDebuff = StatusEffect.getRandomNegative(
          duration = ULTIMATE_EFFECT_DURATION,
          applier = source,
          target = enemy
        )

        if (randomDebuff != null) {
          enemy.addEffect(randomDebuff, source)
        }
      }
    }
  }
) {
  private companion object {
    const val MAX_HEALTH = 190f
    const val DAMAGE = 20f
    const val ACTIVE_DURATION = 2
    const val ULTIMATE_EFFECT_DURATION = 2
  }
}
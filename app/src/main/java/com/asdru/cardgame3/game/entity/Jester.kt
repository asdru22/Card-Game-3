package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Fuse
import com.asdru.cardgame3.game.effect.Stunned
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.helper.heal
import com.asdru.cardgame3.helper.receiveDamage
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlinx.coroutines.delay

class Jester : Entity(
  name = R.string.entity_jester,
  iconRes = R.drawable.entity_jester,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFBBFC79),
  damageType = DamageType.Ranged,
  radarStats = RadarStats(0.7f, 0.2f, 0.0f, 0.8f, 0.8f),
  activeAbility = Ability(
    nameRes = R.string.ability_surprise_gift,
    descriptionRes = R.string.ability_surprise_gift_desc,
    formatArgs = listOf(
      ACTIVE_A_DAMAGE_PERCENT,
      Fuse.Spec,
      FUSE_DURATION
    ),
  ) { source, target ->
    source.applyDamage(target, amount = DAMAGE * ACTIVE_A_DAMAGE_PERCENT / 100f)
    target.addEffect(Fuse(FUSE_DURATION), source = source)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_sleight_of_hand,
    descriptionRes = R.string.ability_sleight_of_hand_desc,
    formatArgs = listOf(
      PASSIVE_HEAL
    ),
  ) { source, _ ->
    source.heal(PASSIVE_HEAL)
    source.swapAbilities()
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_the_punchline,
    descriptionRes = R.string.ability_the_punchline_desc,
    formatArgs = listOf(
      Fuse.Spec,
      ULTIMATE_A_DAMAGE_PERCENT
    ),
  ) { source, _ ->
    source.team.getAliveEnemies().filter { it.effectManager.hasEffect<Fuse>() }.forEach {
      it.run {
        effectManager.removeEffect<Fuse>(owner = this, ignoreMultipliers = true)
        receiveDamage(Fuse.DAMAGE_AMOUNT)
        team.getOtherAliveMembers(this).forEach {
          it.receiveDamage(Fuse.DAMAGE_AMOUNT * ULTIMATE_A_DAMAGE_PERCENT / 100f)
        }
      }
      delay(100)
    }
  },
  alternateActiveAbilities = listOf(
    Ability(
      nameRes = R.string.ability_juggling_act,
      descriptionRes = R.string.ability_juggling_act_desc,
      formatArgs = listOf(
        ACTIVE_B_KNIVES,
        ACTIVE_B_EXTRA_DAMAGE
      ),
    ) { source, _ ->
      val enemies = source.team.getAliveEnemies()
      if (enemies.isNotEmpty()) {
        val hits = mutableMapOf<EntityViewModel, Int>()
        repeat(ACTIVE_B_KNIVES) {
          val target = enemies.random()
          hits[target] = (hits[target] ?: 0) + 1
          source.applyDamage(
            target,
          )
          delay(100)
        }

        hits.forEach { (enemy, count) ->
          if (count >= 2) {
            enemy.receiveDamage(ACTIVE_B_EXTRA_DAMAGE)
          }
        }
      }
    }
  ),
  alternateUltimateAbilities = listOf(
    Ability(
      nameRes = R.string.ability_curtain_call,
      descriptionRes = R.string.ability_curtain_call_desc,
      formatArgs = listOf(
        ULTIMATE_B_KNIVES,
        ULTIMATE_B_HITS_FOR_STUN,
        Stunned.Spec,
        ULTIMATE_B_STUN_DURATION
      ),
      charges = 1
    ) { source, _ ->
      val enemies = source.team.getAliveEnemies()
      if (enemies.isNotEmpty()) {
        val hits = mutableMapOf<EntityViewModel, Int>()
        repeat(ULTIMATE_B_KNIVES) {
          val target = enemies.random()
          hits[target] = (hits[target] ?: 0) + 1
          source.applyDamage(target)
          delay(100)
        }

        hits.forEach { (enemy, count) ->
          if (count >= ULTIMATE_B_HITS_FOR_STUN) {
            enemy.addEffect(Stunned(ULTIMATE_B_STUN_DURATION), source = source)
          }
        }
      }
    }
  )
) {
  private companion object {
    const val MAX_HEALTH = 120f
    const val DAMAGE = 13f

    const val ACTIVE_A_DAMAGE_PERCENT = 20f
    const val FUSE_DURATION = 2
    const val ACTIVE_B_KNIVES = 2
    const val ACTIVE_B_EXTRA_DAMAGE = 7f
    const val PASSIVE_HEAL = 11f
    const val ULTIMATE_A_DAMAGE_PERCENT = 30f
    const val ULTIMATE_B_KNIVES = 4
    const val ULTIMATE_B_HITS_FOR_STUN = 2
    const val ULTIMATE_B_STUN_DURATION = 2
  }
}
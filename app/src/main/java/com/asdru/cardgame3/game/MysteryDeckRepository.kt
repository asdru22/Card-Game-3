package com.asdru.cardgame3.game

import androidx.compose.ui.util.fastForEachReversed
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.MysteryCard
import com.asdru.cardgame3.helper.heal
import com.asdru.cardgame3.helper.receiveDamage
import com.asdru.cardgame3.logic.BattleCombatLogic.performActiveAbility
import kotlin.random.Random

object MysteryDeckRepository {

  private val cards = listOf(
    MysteryCard(
      descriptionRes = R.string.mc_desc_increase_rage,
      formatArgs = listOf(10f),
      weight = 50,
      onApply = { team -> team.increaseRage(10f) }
    ),
    MysteryCard(
      descriptionRes = R.string.mc_desc_heal,
      formatArgs = listOf(5f),
      weight = 50,
      onApply = { team ->
        team.aliveEntities.forEach { it.heal(5f, it) }
      }
    ),
    MysteryCard(
      descriptionRes = R.string.mc_desc_remove_effects,
      weight = 8,
      onApply = { team ->
        val target = team.aliveEntities.maxByOrNull { it.effectManager.effects.size }
        target?.effectManager?.clearAll(target, false)
      }
    ),
    MysteryCard(
      descriptionRes = R.string.mc_desc_remove_coins_e,
      formatArgs = listOf(10),
      weight = 15,
      onApply = { team ->
        team.enemyTeam.shop.removeCoins(10)
      }
    ),
    MysteryCard(
      descriptionRes = R.string.mc_desc_decrease_rage_e,
      formatArgs = listOf(13f),
      weight = 40,
      onApply = { team ->
        team.enemyTeam.decreaseRage(13f)
      }
    ),
    MysteryCard(
      descriptionRes = R.string.mc_desc_deal_damage_e,
      formatArgs = listOf(6f),
      weight = 40,
      onApply = { team ->
        team.enemyTeam.getTargetableEnemies().forEach { it.receiveDamage(6f) }
      }
    ),
    MysteryCard(
      descriptionRes = R.string.mc_desc_use_active,
      weight = 30,
      onApply = { team ->
        team.getAliveMembers().randomOrNull()?.let { attacker ->
          team.getTargetableEnemies().randomOrNull()?.let { target ->
            performActiveAbility(attacker, target)
          }
        }
      }
    ),
    MysteryCard(
      descriptionRes = R.string.mc_desc_increase_effect_duration,
      formatArgs = listOf(1),
      weight = 25,
      onApply = { team ->
        team.getAliveMembers().forEach {
          it.effectManager.effects.forEach { effect -> effect.duration += 1 }
        }
      }
    ),
    MysteryCard(
      descriptionRes = R.string.mc_desc_increase_effect_duration_e,
      formatArgs = listOf(1),
      weight = 25,
      onApply = { team ->
        team.getAliveEnemies().forEach {
          it.effectManager.effects.forEach { effect -> effect.duration += 1 }
        }
      }
    )
  )

  fun drawCard(): MysteryCard {
    val totalWeight = cards.sumOf { it.weight }
    var randomValue = Random.Default.nextInt(totalWeight)
    cards.forEach {
      randomValue -= it.weight
      if (randomValue < 0) {
        return it
      }
    }
    return cards.last()
  }
}
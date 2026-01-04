package com.asdru.cardgame3.game

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.MysteryCard
import com.asdru.cardgame3.helper.heal
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
      weight = 30,
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
      weight = 10,
      onApply = { team ->
        team.enemyTeam.decreaseRage(13f)
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
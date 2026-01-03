package com.asdru.cardgame3.game

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.MysteryCard
import com.asdru.cardgame3.helper.heal
import kotlin.random.Random

object MysteryDeckRepository {

  private val cards = listOf(
    MysteryCard(
      descriptionRes = R.string.card_desc_rage_10,
      weight = 50,
      onApply = { team -> team.increaseRage(10f) }
    ),
    MysteryCard(
      descriptionRes = R.string.card_desc_heal_10,
      weight = 30,
      onApply = { team ->
        team.aliveEntities.forEach { it.heal(10f, it) }
      }
    ),
    MysteryCard(
      descriptionRes = R.string.card_desc_cleanse,
      weight = 20,
      onApply = { team ->
        team.aliveEntities.forEach {
          it.effectManager.clearNegative(it, true)
        }
      }
    )
  )

  fun drawCard(): MysteryCard {
    val totalWeight = cards.sumOf { it.weight }
    var randomValue = Random.Default.nextInt(totalWeight)
    for (card in cards) {
      randomValue -= card.weight
      if (randomValue < 0) {
        return card
      }
    }
    return cards.last()
  }
}
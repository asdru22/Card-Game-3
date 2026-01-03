package com.asdru.cardgame3.game.centerdeck

import com.asdru.cardgame3.helper.heal
import com.asdru.cardgame3.viewModel.TeamViewModel
import kotlin.random.Random

object CenterDeckRepository {

  private val cards = listOf(
    CenterCard(
      description = "+10 Rage",
      onApply = { team -> team.increaseRage(10f) }
    ),
    CenterCard(
      description = "Heal all members by 10",
      onApply = { team ->
        team.aliveEntities.forEach { it.heal(10f, it) }
      }
    ),
    CenterCard(
      description = "Remove negative effects from all members",
      onApply = { team ->
        team.aliveEntities.forEach {
          it.effectManager.clearNegative(it, true)
        }
      }
    )
  )

  fun drawCard(): CenterCard {
    return cards.random()
  }
}

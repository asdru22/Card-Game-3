package com.asdru.cardgame3.game.trait

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class Relentless(
  private var lastTarget: EntityViewModel? = null,
  private var stacks: Int = 0
) : Trait {
  override val nameRes: Int = R.string.trait_relentless
  override val descriptionRes: Int = R.string.trait_relentless_desc
  override val formatArgs: List<Any> = listOf(DAMAGE_INCREASE, DAMAGE_INCREASE * MAX_STACKS)
  override fun modifyOutgoingDamage(
    owner: EntityViewModel,
    target: EntityViewModel,
    amount: Float
  ): Float {

    if (lastTarget != target) {
      lastTarget = target
      stacks = 0
      return amount
    }

    if (stacks < MAX_STACKS) stacks++
    target.popupManager.add(R.string.game_relentless)
    return amount * (1 + DAMAGE_INCREASE * stacks / 100f)
  }

  companion object {
    const val MAX_STACKS = 4
    const val DAMAGE_INCREASE = 7f

  }
}
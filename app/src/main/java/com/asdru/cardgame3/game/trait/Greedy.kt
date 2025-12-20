package com.asdru.cardgame3.game.trait

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class Greedy : Trait {
  override val nameRes: Int = R.string.trait_greedy
  override val descriptionRes: Int = R.string.trait_greedy_desc
  override val formatArgs: List<Any> = listOf(COINS_GAINED)

  override suspend fun onEndTurn(owner: EntityViewModel) {
    owner.team.shop.addCoins(COINS_GAINED)
    owner.popupManager.add(R.string.game_greedy, Color.White)
  }

  companion object {
    const val COINS_GAINED = 3
  }
}
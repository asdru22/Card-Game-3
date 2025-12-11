package com.asdru.cardgame3

import androidx.compose.runtime.Composable
import com.asdru.cardgame3.view.BattleScreen
import com.asdru.cardgame3.viewModel.BattleViewModel

class MainView(
  private val battleViewModel: BattleViewModel
) {
  @Composable
  fun Content() {
    BattleScreen(battleViewModel)
  }
}
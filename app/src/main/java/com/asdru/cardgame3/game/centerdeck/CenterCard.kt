package com.asdru.cardgame3.game.centerdeck

import com.asdru.cardgame3.viewModel.TeamViewModel

data class CenterCard(
  val description: String,
  val onApply: suspend (TeamViewModel) -> Unit
)

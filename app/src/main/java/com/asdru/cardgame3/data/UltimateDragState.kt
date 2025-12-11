package com.asdru.cardgame3.data

import androidx.compose.ui.geometry.Offset
import com.asdru.cardgame3.viewModel.TeamViewModel

data class UltimateDragState(
  val team: TeamViewModel,
  val start: Offset,
  val current: Offset
)
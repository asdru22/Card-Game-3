package com.asdru.cardgame3.data

import androidx.compose.ui.geometry.Offset
import com.asdru.cardgame3.viewModel.TotemViewModel

data class TotemDragState(
  val source: TotemViewModel,
  val start: Offset,
  val current: Offset
)

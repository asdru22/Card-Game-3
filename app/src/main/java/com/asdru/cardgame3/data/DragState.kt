package com.asdru.cardgame3.data

import androidx.compose.ui.geometry.Offset
import com.asdru.cardgame3.viewModel.EntityViewModel

data class DragState(
  val source: EntityViewModel,
  val start: Offset,
  val current: Offset
)

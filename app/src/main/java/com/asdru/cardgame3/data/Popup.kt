package com.asdru.cardgame3.data

import androidx.compose.ui.graphics.Color

data class Popup(
  val id: Long,
  val text: String? = null,
  val textRes: Int? = null,
  val color: Color = Color.White,
  val xOffset: Float = 0f,
  val yOffset: Float = 0f,
  val isStatus: Boolean = false
)
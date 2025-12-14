package com.asdru.cardgame3.manager

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.data.Popup
import kotlin.random.Random

class EntityPopupManager {
  val popups = mutableStateListOf<Popup>()
  private var popupIdCounter = 0L

  // Counter to track stacking order
  private var stackIndex = 0

  fun add(text: String, color: Color = Color.Red, isStatus: Boolean = true) {
    val id = popupIdCounter++
    val (x, y) = getStackingOffsets()

    popups.add(Popup(
      id = id,
      text = text,
      color = color,
      xOffset = x,
      yOffset = y,
      isStatus = isStatus
    ))
  }

  fun add(textRes: Int, color: Color = Color.White) {
    val id = popupIdCounter++
    val (x, y) = getStackingOffsets()

    popups.add(Popup(
      id = id,
      textRes = textRes,
      color = color,
      xOffset = x,
      yOffset = y,
      isStatus = true
    ))
  }

  fun add(amount: Float, color: Color = Color.Red) {
    val sign = if (color == Color.Green) "+" else "-"
    add("$sign${amount.toInt()}", color, isStatus = false)
  }

  private fun getStackingOffsets(): Pair<Float, Float> {
    // 1. Vertical Stacking:
    // Move up by 45 pixels for each new popup in the stack.
    // Reset after 3 popups so they don't go too high.
    val verticalStep = -45f
    val yOffset = (stackIndex % 3) * verticalStep

    // 2. Horizontal Jitter:
    // Keep them mostly centered but add tiny random jitter for flavor
    val xOffset = Random.nextInt(-10, 11).toFloat()

    stackIndex++
    return Pair(xOffset, yOffset)
  }
}
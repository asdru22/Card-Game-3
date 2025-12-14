package com.asdru.cardgame3.manager

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.data.Popup
import kotlin.random.Random

class EntityPopupManager {
    val popups = mutableStateListOf<Popup>()
    private var popupIdCounter = 0L
    private var popupPlacementIndex = 0
    private val popupSlots = listOf(0f, 30f, -30f, 50f, -50f)

    fun add(text: String, color: Color = Color.Red, isStatus: Boolean = true) {
        val id = popupIdCounter++
        val xOffset = getXOffset()
        popups.add(Popup(id = id, text = text, color = color, xOffset = xOffset, isStatus = isStatus))
    }

    fun add(textRes: Int, color: Color = Color.White) {
        val id = popupIdCounter++
        val xOffset = getXOffset()
        popups.add(Popup(id = id, textRes = textRes, color = color, xOffset = xOffset, isStatus = true))
    }

    fun add(amount: Float, color: Color = Color.Red) {
        val sign = if (color == Color.Green) "+" else "-"
        add("$sign${amount.toInt()}", color, isStatus = false)
    }

    private fun getXOffset(): Float {
        val baseOffset = popupSlots[popupPlacementIndex % popupSlots.size]
        popupPlacementIndex++
        val jitter = Random.nextInt(-5, 6).toFloat()
        return baseOffset + jitter
    }
}
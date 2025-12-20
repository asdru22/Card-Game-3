package com.asdru.cardgame3.helper

import com.asdru.cardgame3.game.totem.TestTotem
import com.asdru.cardgame3.game.totem.Totem

object TotemDataProvider {
    fun getAvailableTotems(): List<Totem> {
        return listOf(
            TestTotem()
        )
    }
}

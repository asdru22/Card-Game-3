package com.asdru.cardgame3.helper

import com.asdru.cardgame3.game.totem.Totem

object TotemDataProvider {
    fun getAvailableTotems(): List<Totem> {
        return Totem::class.sealedSubclasses.mapNotNull { it.objectInstance }
    }
}

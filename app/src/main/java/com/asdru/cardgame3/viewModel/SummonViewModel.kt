package com.asdru.cardgame3.viewModel

import com.asdru.cardgame3.data.Summon
import com.asdru.cardgame3.data.SummonAbility

class SummonViewModel(
    val summon: Summon,
    val owner: CharacterViewModel
) : EntityViewModel(summon) {
    val summonAbility: SummonAbility
        get() = summon.ability
}
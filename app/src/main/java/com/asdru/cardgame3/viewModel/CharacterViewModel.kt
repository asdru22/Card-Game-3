package com.asdru.cardgame3.viewModel

import com.asdru.cardgame3.data.Character
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.Summon

class CharacterViewModel(
    val character: Character
) : EntityViewModel(character) {
    val statusEffectManager = StatusEffectManager()

    val activeAbility: Ability
        get() = character.activeAbility

    val passiveAbility: Ability
        get() = character.passiveAbility

    val ultimateAbility: Ability
        get() = character.ultimateAbility

    fun addSummon(summon: Summon) {
        val summonViewModel = SummonViewModel(summon, this)
        team.addEntity(summonViewModel)
    }
}
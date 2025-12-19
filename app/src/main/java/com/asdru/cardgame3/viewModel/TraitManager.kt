package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.mutableStateListOf
import com.asdru.cardgame3.data.StatusEffect
import com.asdru.cardgame3.data.Trait

class TraitManager(
    initialTraits: List<Trait> = emptyList()
) {
    val traits = mutableStateListOf<Trait>().apply { addAll(initialTraits) }

    fun addTrait(trait: Trait) {
        traits.add(trait)
    }

    fun removeTrait(trait: Trait) {
        traits.remove(trait)
    }

    fun modifyOutgoingDamage(owner: EntityViewModel, target: EntityViewModel, amount: Float): Float {
        var currentAmount = amount
        traits.forEach { trait ->
            currentAmount = trait.modifyOutgoingDamage(owner, target, currentAmount)
        }
        return currentAmount
    }

    fun modifyIncomingDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float): Float {
        var currentAmount = amount
        traits.forEach { trait ->
            currentAmount = trait.modifyIncomingDamage(owner, source, currentAmount)
        }
        return currentAmount
    }

    fun modifyHeal(owner: EntityViewModel, amount: Float): Float {
        var currentAmount = amount
        traits.forEach { trait ->
            currentAmount = trait.modifyHeal(owner, currentAmount)
        }
        return currentAmount
    }

    fun onStartTurn(owner: EntityViewModel) {
        traits.forEach { it.onStartTurn(owner) }
    }

    suspend fun onEndTurn(owner: EntityViewModel) {
        traits.forEach { it.onEndTurn(owner) }
    }

    suspend fun onDidDealDamage(owner: EntityViewModel, target: EntityViewModel, amount: Float, overkill: Float) {
        traits.forEach { it.onDidDealDamage(owner, target, amount, overkill) }
    }

    fun onDidReceiveDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float) {
        traits.forEach { it.onDidReceiveDamage(owner, source, amount) }
    }

    suspend fun onDeath(owner: EntityViewModel) {
        traits.forEach { it.onDeath(owner) }
    }

    fun modifyIncomingEffect(owner: EntityViewModel, effect: StatusEffect, source: EntityViewModel?): StatusEffect? {
        var currentEffect: StatusEffect? = effect
        for (trait in traits) {
            if (currentEffect == null) break
            currentEffect = trait.modifyIncomingEffect(owner, currentEffect, source)
        }
        return currentEffect
    }
}

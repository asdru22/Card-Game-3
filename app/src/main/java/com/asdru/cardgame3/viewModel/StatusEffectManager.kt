package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.mutableStateListOf
import com.asdru.cardgame3.data.StatusEffect

class StatusEffectManager {
    val effects = mutableStateListOf<StatusEffect>()

    fun addEffect(effect: StatusEffect, owner: CharacterViewModel) {
        // Simple logic for now, just add. In real game, might check for existing stackable effects.
        effects.add(effect)
        effect.onApply(owner)
    }

    fun removeEffect(effect: StatusEffect, owner: CharacterViewModel) {
        if (effects.remove(effect)) {
            effect.onVanish(owner)
        }
    }

    fun hasEffect(predicate: (StatusEffect) -> Boolean): Boolean {
        return effects.any(predicate)
    }

    suspend fun onStartTurn(owner: CharacterViewModel) {
        val iterator = effects.iterator()
        while (iterator.hasNext()) {
            val effect = iterator.next()
            effect.onStartTurn(owner)
            // tick returns true if duration <= 0
            if (effect.tick()) {
                iterator.remove()
                effect.onVanish(owner)
            }
        }
    }

    suspend fun onEndTurn(owner: CharacterViewModel) {
         effects.forEach { it.onEndTurn(owner) }
    }

    // This is NOT suspend in StatusEffect base class
    fun modifyDamage(currentDamage: Float, owner: CharacterViewModel?, target: EntityViewModel?): Float {
        var damage = currentDamage
        effects.forEach { 
            damage = it.modifyDamage(damage, owner, target)
        }
        return damage
    }

    // These ARE suspend in StatusEffect base class
    suspend fun modifyIncomingDamage(owner: CharacterViewModel, currentDamage: Float, source: EntityViewModel?): Float {
        var damage = currentDamage
        effects.forEach { 
            damage = it.modifyIncomingDamage(owner, damage, source)
        }
        return damage
    }

    suspend fun modifyOutgoingDamage(owner: CharacterViewModel, currentDamage: Float, target: EntityViewModel?): Float {
        var damage = currentDamage
        effects.forEach { 
            damage = it.modifyOutgoingDamage(owner, damage, target)
        }
        return damage
    }

    suspend fun modifyIncomingHealing(owner: CharacterViewModel, currentHealing: Float, source: EntityViewModel?): Float {
        var healing = currentHealing
        effects.forEach { 
            healing = it.modifyIncomingHealing(owner, healing, source)
        }
        return healing
    }
    
    fun modifyActiveTarget(owner: CharacterViewModel, target: EntityViewModel): EntityViewModel {
        var newTarget = target
        effects.forEach {
            newTarget = it.modifyActiveTarget(owner, newTarget)
        }
        return newTarget
    }

    fun modifyPassiveTarget(owner: CharacterViewModel, target: EntityViewModel): EntityViewModel {
         var newTarget = target
         effects.forEach {
             newTarget = it.modifyPassiveTarget(owner, newTarget)
         }
         return newTarget
    }
}

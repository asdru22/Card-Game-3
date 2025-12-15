package com.asdru.cardgame3.manager

import androidx.compose.runtime.mutableStateListOf
import com.asdru.cardgame3.game.effect.StatusEffect
import com.asdru.cardgame3.game.effect.Stunned
import com.asdru.cardgame3.viewModel.EntityViewModel

class EntityEffectManager(
  @PublishedApi internal val onEffectsChanged: () -> Unit
) {
  val effects = mutableStateListOf<StatusEffect>()

  val isStunned: Boolean
    get() = effects.any { it is Stunned }

  fun addEffect(effect: StatusEffect, source: EntityViewModel?, owner: EntityViewModel) {
    var currentEffect: StatusEffect? = effect

    // Apply traits that might modify incoming effects (Resistances, etc.)
    for (trait in owner.traits) {
      if (currentEffect == null) break
      currentEffect = trait.modifyIncomingEffect(owner, currentEffect, source)
    }

    if (currentEffect == null) return

    val finalEffect = currentEffect
    val existingEffect = effects.find { it::class == finalEffect::class }

    if (existingEffect != null) {
      existingEffect.duration = finalEffect.duration
      existingEffect.source = source
    } else {
      finalEffect.source = source
      effects.add(finalEffect)
      finalEffect.onApply(owner)
      onEffectsChanged()
    }
  }

  fun modifyActiveTarget(owner: EntityViewModel, target: EntityViewModel): EntityViewModel {
    var currentTarget = target
    effects.forEach { effect ->
      currentTarget = effect.modifyActiveTarget(owner, currentTarget)
    }
    return currentTarget
  }

  fun modifyPassiveTarget(owner: EntityViewModel, target: EntityViewModel): EntityViewModel {
    var currentTarget = target
    effects.forEach { effect ->
      currentTarget = effect.modifyPassiveTarget(owner, currentTarget)
    }
    return currentTarget
  }

  fun removeEffect(effect: StatusEffect, owner: EntityViewModel) {
    effect.onVanish(owner)
    effects.remove(effect)
    onEffectsChanged()
  }

  inline fun <reified T : StatusEffect> removeEffect(owner: EntityViewModel) {
    val iterator = effects.iterator()
    while (iterator.hasNext()) {
      val effect = iterator.next()
      if (effect is T) {
        effect.onVanish(owner)
        iterator.remove()
      }
    }
    onEffectsChanged()
  }

  fun clearAll(owner: EntityViewModel): Int {
    return clearNegative(owner) + clearPositive(owner)
  }

  fun clearNegative(owner: EntityViewModel): Int {
    val effectsToRemove = effects.filter { !it.isPositive }
    effectsToRemove.forEach { effect ->
      removeEffect(effect, owner)
    }
    return effectsToRemove.size
  }

  fun clearPositive(owner: EntityViewModel): Int {
    val effectsToRemove = effects.filter { it.isPositive }
    effectsToRemove.forEach { effect ->
      removeEffect(effect, owner)
    }
    return effectsToRemove.size
  }
}
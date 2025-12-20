package com.asdru.cardgame3.helper

import androidx.compose.runtime.mutableStateListOf
import com.asdru.cardgame3.game.effect.Silenced
import com.asdru.cardgame3.game.effect.StatusEffect
import com.asdru.cardgame3.game.effect.Stunned
import com.asdru.cardgame3.viewModel.EntityViewModel

class EntityEffectManager(
  @PublishedApi internal val onEffectsChanged: () -> Unit
) {
  val effects = mutableStateListOf<StatusEffect>()

  val isStunned: Boolean
    get() = effects.any { it is Stunned }

  val isSilenced: Boolean
    get() = effects.any { it is Silenced }

  fun addEffect(effect: StatusEffect, source: EntityViewModel?, owner: EntityViewModel) {
    var currentEffect: StatusEffect? = effect

    for (trait in owner.traits) {
      if (currentEffect == null) break
      currentEffect = trait.modifyIncomingEffect(owner, currentEffect, source)
    }

    if (currentEffect == null) return

    source?.team?.let { team ->
      team.totalEffectsApplied++
    }

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

  suspend fun expireEffect(effect: StatusEffect, owner: EntityViewModel) {
    effect.onExpire(owner)
    removeEffect(effect, owner)
  }

  fun getRandomPositiveEffect(): StatusEffect? {
    return effects.filter { it.isPositive }.randomOrNull()
  }

  fun removeEffect(effect: StatusEffect, owner: EntityViewModel): StatusEffect? {
    if (effects.contains(effect)) {
      effect.onRemove(owner)
      effects.remove(effect)
      onEffectsChanged()
      return effect
    }
    return null
  }

  inline fun <reified T : StatusEffect> removeEffect(owner: EntityViewModel) {
    val iterator = effects.iterator()
    while (iterator.hasNext()) {
      val effect = iterator.next()
      if (effect is T) {
        effect.onRemove(owner)
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

  inline fun <reified T : StatusEffect> hasEffect(): Boolean {
    return effects.any { it is T }
  }
}
package com.asdru.cardgame3.game.effect

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

sealed class StatusEffect(
  @field:StringRes override val nameRes: Int,
  @field:StringRes override val descriptionRes: Int,
  @field:DrawableRes val iconRes: Int,
  initialDuration: Int,
  override val isPositive: Boolean,
  override val formatArgs: List<Any> = emptyList()
) : Translatable {
  var duration by mutableIntStateOf(initialDuration)
  var source: EntityViewModel? = null

  open fun onApply(target: EntityViewModel) {}
  open fun onVanish(target: EntityViewModel) {}

  open suspend fun onStartTurn(target: EntityViewModel) {}

  open fun modifyDamage(currentDamage: Float): Float = currentDamage
  open fun modifyActiveTarget(owner: EntityViewModel, target: EntityViewModel): EntityViewModel {
    return target
  }
  open fun modifyPassiveTarget(owner: EntityViewModel, target: EntityViewModel): EntityViewModel {
    return target
  }

  open suspend fun modifyIncomingDamage(
    owner: EntityViewModel,
    currentDamage: Float,
    source: EntityViewModel?
  ): Float = currentDamage

  fun tick(): Boolean {
    duration--
    return duration <= 0
  }

  companion object {

    fun getRandomPositive(duration: Int, applier: EntityViewModel? = null): StatusEffect? {
      return getRandomEffect(duration, applier, checkPositive = true)
    }

    fun getRandomNegative(duration: Int, applier: EntityViewModel? = null): StatusEffect? {
      return getRandomEffect(duration, applier, checkPositive = false)
    }

    private fun getRandomEffect(
      duration: Int,
      applier: EntityViewModel?,
      checkPositive: Boolean
    ): StatusEffect? {
      val subclasses = StatusEffect::class.sealedSubclasses
      if (subclasses.isEmpty()) return null

      // Shuffle to ensure randomness
      val shuffledClasses = subclasses.shuffled()

      for (kClass in shuffledClasses) {
        try {
          val constructor = kClass.primaryConstructor ?: continue
          val params = constructor.parameters

          // Attempt to match constructor signature
          val instance = when {
            // Case 1: Standard constructor (duration: Int)
            params.size == 1 && params[0].type.classifier == Int::class -> {
              constructor.call(duration)
            }

            // Case 2: Constructor with Applier (duration: Int, applier: EntityViewModel)
            // Only attempts this if an applier was actually provided to the function
            params.size == 2 &&
                params[0].type.classifier == Int::class &&
                params[1].type.classifier == EntityViewModel::class -> {
              if (applier != null) constructor.call(duration, applier) else null
            }

            // Fallback for no-arg constructors
            params.isEmpty() -> {
              kClass.createInstance().apply { this.duration = duration }
            }

            else -> null
          }

          if (instance != null && instance.isPositive == checkPositive) {
            return instance
          }

        } catch (_: Exception) {
          // If a specific class fails to instantiate (e.g. missing applier), skip it and try the next one
          continue
        }
      }
      return null
    }
  }
}
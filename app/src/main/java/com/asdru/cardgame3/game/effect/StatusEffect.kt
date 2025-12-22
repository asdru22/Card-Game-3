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
  initialMultiplier: Int = 1,
  override val isPositive: Boolean,
  override val formatArgs: List<Any> = emptyList()
) : Translatable {
  var duration by mutableIntStateOf(initialDuration)
  var multiplier by mutableIntStateOf(initialMultiplier)
  var source: EntityViewModel? = null

  open fun overheal(): Float = 0f

  open fun onApply(target: EntityViewModel) {}
  open fun onRemove(target: EntityViewModel) {}
  open suspend fun onExpire(target: EntityViewModel) {}

  open suspend fun onStartTurn(target: EntityViewModel) {}
  open suspend fun onEndTurn(target: EntityViewModel) {}

  open fun modifyDamage(
    currentDamage: Float,
    owner: EntityViewModel?,
    target: EntityViewModel?
  ): Float = currentDamage

  open fun modifyActiveTarget(owner: EntityViewModel, target: EntityViewModel): EntityViewModel {
    return target
  }

  open suspend fun postDamageDealt(
    owner: EntityViewModel,
    target: EntityViewModel,
    amount: Float
  ) {
  }

  open fun modifyPassiveTarget(owner: EntityViewModel, target: EntityViewModel): EntityViewModel {
    return target
  }

  open suspend fun modifyIncomingDamage(
    owner: EntityViewModel,
    currentDamage: Float,
    source: EntityViewModel?
  ): Float = currentDamage

  open suspend fun modifyOutgoingDamage(
    owner: EntityViewModel,
    currentDamage: Float,
    target: EntityViewModel?
  ): Float = currentDamage

  open suspend fun modifyIncomingHealing(
    owner: EntityViewModel,
    currentHealing: Float,
    source: EntityViewModel?
  ): Float = currentHealing

  fun tick(): Boolean {
    duration--
    return duration <= 0
  }

  companion object {

    fun getRandom(
      duration: Int,
      applier: EntityViewModel? = null,
      target: EntityViewModel
    ): StatusEffect? {
      return getRandomEffect(duration, applier, target, checkPositive = null)
    }

    fun getRandomPositive(
      duration: Int,
      applier: EntityViewModel? = null,
      target: EntityViewModel
    ): StatusEffect? {
      return getRandomEffect(duration, applier, target, checkPositive = true)
    }

    fun getRandomNegative(
      duration: Int,
      applier: EntityViewModel? = null,
      target: EntityViewModel
    ): StatusEffect? {
      return getRandomEffect(duration, applier, target, checkPositive = false)
    }

    private fun getRandomEffect(
      duration: Int,
      applier: EntityViewModel?,
      target: EntityViewModel,
      checkPositive: Boolean?
    ): StatusEffect? {
      val subclasses = StatusEffect::class.sealedSubclasses
      if (subclasses.isEmpty()) return null

      val shuffledClasses = subclasses.shuffled()

      for (kClass in shuffledClasses) {

        val alreadyHasEffect = target.effectManager.effects.any { it::class == kClass }

        if (alreadyHasEffect) {
          continue
        }

        try {
          val constructor = kClass.primaryConstructor ?: continue
          val params = constructor.parameters

          val instance = when {
            params.size == 1 && params[0].type.classifier == Int::class -> {
              constructor.call(duration)
            }

            params.size == 2 &&
                params[0].type.classifier == Int::class &&
                params[1].type.classifier == EntityViewModel::class -> {
              if (applier != null) constructor.call(duration, applier) else null
            }

            params.isEmpty() -> {
              kClass.createInstance().apply { this.duration = duration }
            }

            else -> null
          }

          if (instance != null) {
            val polarityMatches = checkPositive == null || instance.isPositive == checkPositive

            if (polarityMatches) {
              return instance
            }
          }

        } catch (_: Exception) {
          continue
        }
      }
      return null
    }
  }
}
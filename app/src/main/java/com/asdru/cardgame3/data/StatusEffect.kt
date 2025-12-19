package com.asdru.cardgame3.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.asdru.cardgame3.viewModel.CharacterViewModel
import com.asdru.cardgame3.viewModel.EntityViewModel

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

  open fun onApply(target: CharacterViewModel) {}
  open fun onVanish(target: CharacterViewModel) {}

  open suspend fun onStartTurn(target: CharacterViewModel) {}
  open suspend fun onEndTurn(target: CharacterViewModel) {}

  open fun modifyDamage(currentDamage: Float, owner: CharacterViewModel?, target: EntityViewModel?): Float = currentDamage
  open fun modifyActiveTarget(owner: CharacterViewModel, target: EntityViewModel): EntityViewModel {
    return target
  }

  open fun modifyPassiveTarget(owner: CharacterViewModel, target: EntityViewModel): EntityViewModel {
    return target
  }

  open suspend fun modifyIncomingDamage(
    owner: CharacterViewModel,
    currentDamage: Float,
    source: EntityViewModel?
  ): Float = currentDamage

  open suspend fun modifyOutgoingDamage(
    owner: CharacterViewModel,
    currentDamage: Float,
    target: EntityViewModel?
  ): Float = currentDamage

  open suspend fun modifyIncomingHealing(
    owner: CharacterViewModel,
    currentHealing: Float,
    source: EntityViewModel?
  ): Float = currentHealing

  fun tick(): Boolean {
    duration--
    return duration <= 0
  }

}
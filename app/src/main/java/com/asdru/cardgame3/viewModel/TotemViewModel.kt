package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.asdru.cardgame3.data.TotemAbility
import com.asdru.cardgame3.game.totem.Totem

class TotemViewModel(
  val totem: Totem
) : ViewModel() {
  var currentHealth by mutableFloatStateOf(totem.initialHealth)
    internal set

  val maxHealth: Float = totem.initialHealth
  val activeAbility: TotemAbility = totem.activeAbility
  val passiveAbility: TotemAbility = totem.passiveAbility

  val isAlive: Boolean get() = currentHealth > 0

  fun takeDamage(amount: Float) {
    currentHealth = (currentHealth - amount).coerceAtLeast(0f)
  }
}
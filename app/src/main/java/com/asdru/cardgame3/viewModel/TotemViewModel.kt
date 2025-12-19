package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.asdru.cardgame3.game.totem.Totem
import com.asdru.cardgame3.data.TotemAbility

class TotemViewModel(
  val totem: Totem
) : ViewModel() {
  var currentHealth by mutableFloatStateOf(totem.initialStats.maxHealth)
    internal set

  val maxHealth: Float = totem.initialStats.maxHealth
  val damage: Float = totem.initialStats.damage
  val activeAbility: TotemAbility = totem.activeAbility
  val passiveAbility: TotemAbility = totem.passiveAbility

  val isAlive: Boolean get() = currentHealth > 0

  fun takeDamage(amount: Float) {
    currentHealth = (currentHealth - amount).coerceAtLeast(0f)
  }
}
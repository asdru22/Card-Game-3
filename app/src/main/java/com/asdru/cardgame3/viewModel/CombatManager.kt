package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import com.asdru.cardgame3.data.Stats
import kotlin.math.max
import kotlin.math.min

class CombatManager(
  val stats: Stats,
  initialHealth: Float = stats.maxHealth
) {
  var health by mutableFloatStateOf(initialHealth)
    private set

  val maxHealth: Float
    get() = stats.maxHealth

  val isDead: Boolean
    get() = health <= 0

  fun applyDamage(
    target: EntityViewModel,
    amount: Float,
    source: EntityViewModel? = null,
  ) {
    target.combatManager.receiveDamage(amount, source)
  }

  fun receiveDamage(amount: Float, source: EntityViewModel?) {
    val finalDamage = amount // Placeholder for defense calculation
    health = max(0f, health - finalDamage)

    // Notify traits or other listeners here if needed (via TraitManager if accessible)
  }

  fun heal(amount: Float) {
    health = min(maxHealth, health + amount)
  }
}

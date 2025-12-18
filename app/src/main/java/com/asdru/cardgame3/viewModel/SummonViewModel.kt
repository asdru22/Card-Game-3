package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.asdru.cardgame3.game.summon.Summon

class SummonViewModel(
  val summon: Summon,
  val owner: EntityViewModel,
  val target: EntityViewModel?
) : ViewModel() {

  var health by mutableFloatStateOf(summon.initialStats.maxHealth)
  var maxHealth by mutableFloatStateOf(summon.initialStats.maxHealth)

  var damage by mutableFloatStateOf(summon.initialStats.damage)

  val isAlive: Boolean get() = health > 0
  val name: Int = summon.name
  val iconRes: Int = summon.iconRes
  val color = summon.color

  val isLeftTeam: Boolean get() = owner.isLeftTeam

  fun takeDamage(amount: Float) {
    health = (health - amount).coerceAtLeast(0f)
  }

  fun heal(amount: Float) {
    if (health > 0) {
      health = (health + amount).coerceAtMost(maxHealth)
    }
  }
}
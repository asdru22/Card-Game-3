package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class Undead : Trait {
  override val maxCharges: Int = MAX_CHARGES

  override val nameRes: Int = R.string.trait_undead
  override val descriptionRes: Int = R.string.trait_undead_desc
  override val formatArgs: List<Any> = listOf(HEALTH_REVIVE_PERCENTAGE,MAX_CHARGES)


  override suspend fun onDeath(owner: EntityViewModel) {
    owner.traitCharges[id] = 0
  }

  override fun onStartTurnDead(owner: EntityViewModel) {
    val currentCharges = owner.traitCharges[id] ?: 0
    if (currentCharges < maxCharges) {
      val newCharges = currentCharges + 1
      owner.traitCharges[id] = newCharges
      
      if (newCharges >= maxCharges) {
        owner.health = owner.maxHealth * HEALTH_REVIVE_PERCENTAGE / 100
        owner.traitCharges.remove(id)
      }
    }
  }
  
  companion object {
    private const val HEALTH_REVIVE_PERCENTAGE = 30f
    private const val MAX_CHARGES = 3
  }
}

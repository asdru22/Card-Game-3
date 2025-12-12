package com.asdru.cardgame3.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class Meltdown : Trait {
  override val nameRes: Int = R.string.trait_meltdown
  override val descriptionRes: Int = R.string.trait_meltdown_desc
  override val formatArgs: List<Any> = listOf(DEATH_DAMAGE)

  override suspend fun onDeath(owner: EntityViewModel) {
    val enemies = owner.team.getAliveEnemies()

    if (enemies.isNotEmpty()) {
      owner.applyDamageToTargets(
        enemies,
        DEATH_DAMAGE,
      )
    }
  }

  companion object {
    const val DEATH_DAMAGE = 23f
  }
}
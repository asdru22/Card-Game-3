package com.asdru.cardgame3.game.trait

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.applyDamageToTargets

class Meltdown : Trait {
  override val nameRes: Int = R.string.trait_meltdown
  override val descriptionRes: Int = R.string.trait_meltdown_desc
  override val formatArgs: List<Any> = listOf(DEATH_DAMAGE)

  override suspend fun onDeath(owner: EntityViewModel) {
    val enemies = owner.team.getAliveEnemies()

    if (enemies.isNotEmpty()) {
      owner.popupManager.add(R.string.game_meltdown, Color.White)

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
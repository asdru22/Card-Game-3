package com.asdru.cardgame3.game.trait

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.receiveDamage

class Overkill : Trait {
  override val nameRes: Int = R.string.trait_overkill
  override val descriptionRes: Int = R.string.trait_overkill_desc

  override suspend fun onDidDealDamage(owner: EntityViewModel, target: EntityViewModel, amount: Float, overkill: Float) {
    if (overkill > 0f) {
      val aliveTeammates = target.team.getAliveMembers()
      
      if (aliveTeammates.isNotEmpty()) {
        owner.popupManager.add(R.string.game_overkill, Color.White)

        val damagePerTeammate = overkill / aliveTeammates.size
        
        aliveTeammates.forEach { teammate ->
          teammate.receiveDamage(damagePerTeammate, source = owner)
        }
      }
    }
  }
}
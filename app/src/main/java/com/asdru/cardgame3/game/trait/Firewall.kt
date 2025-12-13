package com.asdru.cardgame3.game.trait

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.game.effect.StatusEffect
import com.asdru.cardgame3.viewModel.EntityViewModel

class Firewall : Trait {
  override val nameRes: Int = R.string.trait_firewall
  override val descriptionRes: Int = R.string.trait_firewall_desc

  override fun modifyIncomingEffect(
    owner: EntityViewModel,
    effect: StatusEffect,
    source: EntityViewModel?
  ): StatusEffect? {
    owner.addPopup(R.string.game_firewall, Color.White)
    return null
  }
}
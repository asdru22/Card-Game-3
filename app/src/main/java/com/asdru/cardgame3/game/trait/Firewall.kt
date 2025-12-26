package com.asdru.cardgame3.game.trait

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
    owner.popupManager.add(R.string.game_firewall)
    return null
  }
}
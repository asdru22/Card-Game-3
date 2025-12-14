package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.applyDamageToTargets

class Bursting(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {
  override suspend fun modifyIncomingDamage(
    owner: EntityViewModel,
    currentDamage: Float,
    source: EntityViewModel?
  ): Float {
    owner.effectManager.removeEffect<Bursting>(owner)

    owner.applyDamageToTargets(
      source?.team?.getAliveMembers() ?: emptyList(),
      BURSTING_DAMAGE,
      playAttackAnimation = false
    )
    return currentDamage
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_bursting
    override val formatArgs = listOf(BURSTING_DAMAGE)
    override val nameRes = R.string.effect_bursting
    override val descriptionRes = R.string.effect_bursting_desc

    override val isPositive = true
    private const val BURSTING_DAMAGE = 15f
  }
}
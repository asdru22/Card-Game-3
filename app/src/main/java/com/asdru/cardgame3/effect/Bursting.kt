package com.asdru.cardgame3.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel

class Bursting(duration: Int) : com.asdru.cardgame3.effect.StatusEffect(
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
    owner.applyDamageToTargets(
      source?.team?.getAliveMembers() ?: emptyList(),
      BURSTING_DAMAGE,
      playAttackAnimation = false
    )
    owner.removeEffect<Bursting>()
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
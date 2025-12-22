package com.asdru.cardgame3.game.effect

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel

import kotlin.math.min

class Shielded(
  duration: Int,
  private var shieldHealth: Float = 10f
) : StatusEffect(
  nameRes = Spec.nameRes,
  descriptionRes = Spec.descriptionRes,
  iconRes = Spec.iconRes,
  initialDuration = duration,
  isPositive = Spec.isPositive,
  formatArgs = listOf(shieldHealth)
) {

  override val formatArgs: List<Any>
    get() = listOf(shieldHealth.toInt())

  override suspend fun modifyIncomingDamage(
    owner: EntityViewModel,
    currentDamage: Float,
    source: EntityViewModel?
  ): Float {
    val absorbedAmount = min(currentDamage, shieldHealth)
    shieldHealth -= absorbedAmount

    owner.popupManager.add("-${absorbedAmount.toInt()}", color = Color.White, isStatus = false)

    if (shieldHealth == 0f) {
      owner.effectManager.removeEffect(this, owner, true)
    }

    return currentDamage - absorbedAmount
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_shielded
    override val formatArgs = listOf("?")
    override val nameRes = R.string.effect_shielded
    override val descriptionRes = R.string.effect_shielded_desc
    override val isPositive = true
  }
}
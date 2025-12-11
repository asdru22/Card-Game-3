package com.asdru.cardgame3.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel

class Strength(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {
  private var ownerDamageType: DamageType? = null

  override fun onApply(target: EntityViewModel) {
    ownerDamageType = target.damageType
  }

  override fun modifyDamage(currentDamage: Float): Float {
    return if (ownerDamageType == DamageType.Melee) {
      currentDamage * ((100 + DAMAGE_INCREASE) / 100)
    } else {
      currentDamage
    }
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_strength
    override val formatArgs = listOf(DAMAGE_INCREASE)
    override val nameRes = R.string.effect_strength
    override val descriptionRes = R.string.effect_strength_desc

    override val isPositive = true
    private const val DAMAGE_INCREASE = 15f
  }
}
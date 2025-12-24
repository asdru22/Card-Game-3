package com.asdru.cardgame3.game.effect

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.helper.receiveDamage
import com.asdru.cardgame3.viewModel.EntityViewModel

class Fuse(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs,
) {
  override suspend fun onExpire(target: EntityViewModel) {
    target.receiveDamage(DAMAGE_AMOUNT)
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_fuse
    override val nameRes = R.string.effect_fuse
    override val descriptionRes = R.string.effect_fuse_desc
    override val isPositive = false
    public const val DAMAGE_AMOUNT = 18f
  }
}
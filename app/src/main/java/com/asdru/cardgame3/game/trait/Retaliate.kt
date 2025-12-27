package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.DamageData
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.viewModel.EntityViewModel

class Retaliate : Trait {
  override val nameRes: Int = R.string.trait_retaliate
  override val descriptionRes: Int = R.string.trait_retaliate_desc
  override val formatArgs: List<Any> = listOf(DAMAGE_DEALT)

  override suspend fun onDidReceiveDamage(
    owner: EntityViewModel,
    source: EntityViewModel?,
    amount: Float,
    damageData: DamageData?
  ) {
    if (damageData?.isRetaliation == true) return
    source?.let {
      owner.applyDamage(
        target = source,
        amount = DAMAGE_DEALT,
        damageData = DamageData(isRetaliation = true)
      )
    }
  }

  companion object {
    const val DAMAGE_DEALT = 9f
  }
}
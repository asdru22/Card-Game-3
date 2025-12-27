package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.game.effect.Defiance
import com.asdru.cardgame3.viewModel.EntityViewModel

class Resilience(
  private var damageReceived: Float = 0f
) : Trait {
  override val maxCharges: Int = MAX_CHARGES
  override val nameRes: Int = R.string.trait_resilience
  override val descriptionRes: Int = R.string.trait_resilience_desc
  override val formatArgs: List<Any> =
    listOf(
      DAMAGE_RECEIVED_LIMIT,
      MAX_CHARGES,
      ACTIVE_ABILITY_DMG,
      Defiance.Spec,
      PASSIVE_EFFECT_DURATION
    )

  override fun onStartTurn(owner: EntityViewModel) {
    if (damageReceived > DAMAGE_RECEIVED_LIMIT) return
    increaseCharge(owner)
  }

  override suspend fun onEndTurn(owner: EntityViewModel) {
    damageReceived = 0f
  }

  override suspend fun onUsedPassiveAbility(
    owner: EntityViewModel,
    target: EntityViewModel
  ) {
    if (getCharge(owner) < maxCharges) return
    target.addEffect(Defiance(PASSIVE_EFFECT_DURATION), owner)
    resetCharge(owner)
  }

  override suspend fun onDidReceiveDamage(
    owner: EntityViewModel,
    source: EntityViewModel?,
    amount: Float,
    damageData: com.asdru.cardgame3.data.DamageData?
  ) {
    damageReceived += amount
  }

  companion object {
    public fun getActiveExtraDmg(): Float {
      return ACTIVE_ABILITY_DMG
    }

    private const val DAMAGE_RECEIVED_LIMIT = 30f
    private const val MAX_CHARGES = 3
    private const val ACTIVE_ABILITY_DMG = 2f
    private const val PASSIVE_EFFECT_DURATION = 1
  }
}

package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageData
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Overloaded
import com.asdru.cardgame3.game.trait.Adrenaline
import com.asdru.cardgame3.helper.applyDamage

class Berserker : Entity(
  name = R.string.entity_berserker,
  iconRes = R.drawable.entity_berserker,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFFF0C0C),
  traits = listOf(Adrenaline()),
  damageType = DamageType.Melee,
  radarStats = RadarStats(0.8f, 0.1f, 0.3f, 0.0f, 0.4f),
  activeAbility = Ability(
    nameRes = R.string.ability_rampage,
    descriptionRes = R.string.ability_rampage_desc,
    formatArgs = listOf(
      ACTIVE_REPEATS, ACTIVE_DECAY
    )
  ) { source, target ->
    source.applyDamage(
      target,
      repeats = ACTIVE_REPEATS,
      delayTime = 200,
      damageData = DamageData(
        damageDecay = -ACTIVE_DECAY
      )
    )
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_battle_cry,
    descriptionRes = R.string.ability_battle_cry_desc,
    charges = PASSIVE_CHARGES,
    formatArgs = listOf(
      PASSIVE_RAGE_INCREASE,
      PASSIVE_CHARGES
    )
  ) { source, target ->
    target.effectManager.clearNegative(target)
    target.team.increaseRage(PASSIVE_RAGE_INCREASE)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_carnage,
    descriptionRes = R.string.ability_carnage_desc,
    formatArgs = listOf(
      Overloaded.Spec,
      ULTIMATE_DURATION,
      -ULTIMATE_DECAY,
      ULTIMATE_RAGE_GAIN
    )
  ) { source, _ ->
    source.addEffect(Overloaded(ULTIMATE_DURATION))
    val strongestEnemy = source.team.getTargetableEnemies().maxByOrNull { it.health }
    strongestEnemy?.let {
      source.applyDamage(
        strongestEnemy,
        repeats = ACTIVE_REPEATS + 1,
        delayTime = 200,
        damageData = DamageData(
          damageDecay = ULTIMATE_DECAY,
          ownRageIncrease = ULTIMATE_RAGE_GAIN
        )
      )
    }
  }
) {
  private companion object {
    const val MAX_HEALTH = 180f
    const val DAMAGE = 8f
    const val ACTIVE_REPEATS = 4
    const val ACTIVE_DECAY = 20f
    const val PASSIVE_CHARGES = 2
    const val PASSIVE_RAGE_INCREASE = 12f
    const val ULTIMATE_DURATION = 1
    const val ULTIMATE_DECAY = 10f
    const val ULTIMATE_RAGE_GAIN = 4f

  }
}
package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Sick
import com.asdru.cardgame3.viewModel.applyDamage
import com.asdru.cardgame3.viewModel.applyDamageToTargets
import com.asdru.cardgame3.viewModel.heal

class WitchDoctor : Entity(
  name = R.string.entity_witch_doctor,
  iconRes = R.drawable.entity_witch_doctor,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFA27E3A),
  damageType = DamageType.Melee,
  radarStats = RadarStats(0.5f, 0.5f, 0.5f, 0.7f, 0.4f),
  activeAbility = Ability(
    nameRes = R.string.ability_shots,
    descriptionRes = R.string.ability_shots_desc,
    formatArgs = listOf(
      ACTIVE_REPEATS,
      RAGE_DECREASE
    )
  ) { source, target ->
    source.applyDamage(
      target,
      repeats = ACTIVE_REPEATS,
      delayTime = 200,
      rageDecrease = RAGE_DECREASE
    )
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_vitamins,
    descriptionRes = R.string.ability_vitamins_desc,
    formatArgs = listOf(
      PASSIVE_RAGE_INCREASE,
      PASSIVE_HEAL_AMOUNT
    )
  ) { source, target ->
    target.team.increaseRage(PASSIVE_RAGE_INCREASE)
    target.heal(PASSIVE_HEAL_AMOUNT, source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_the_cure,
    descriptionRes = R.string.ability_the_cure_desc,
    formatArgs = listOf(
      Sick.Spec,
      ULTIMATE_DURATION,
      ULTIMATE_DAMAGE_AMOUNT
    )
  ) { source, _ ->
    source.applyDamageToTargets(
      source.team.getTargetableEnemies(),
      amount = ULTIMATE_DAMAGE_AMOUNT,
      effects = listOf(Sick(ULTIMATE_DURATION))
    )
  }
) {
  private companion object {
    const val MAX_HEALTH = 160f
    const val DAMAGE = 5f
    const val ACTIVE_REPEATS = 5
    const val RAGE_DECREASE = 0.7f

    const val PASSIVE_RAGE_INCREASE = 3f
    const val PASSIVE_HEAL_AMOUNT = 6f
    const val ULTIMATE_DURATION = 4
    const val ULTIMATE_DAMAGE_AMOUNT = 10f
  }
}
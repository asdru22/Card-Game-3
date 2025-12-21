package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Confusion
import com.asdru.cardgame3.game.effect.Vanish
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.helper.heal
import com.asdru.cardgame3.helper.increaseMaxHealth

class Sorcerer : Entity(
  name = R.string.entity_sorcerer,
  iconRes = R.drawable.entity_sorcerer,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFEE90FF),
  damageType = DamageType.Magic,
  radarStats = RadarStats(0.3f, 0.6f, 0.5f, 0.6f, 0.3f),
  activeAbility = Ability(
    nameRes = R.string.ability_delirium,
    descriptionRes = R.string.ability_delirium_desc,
    formatArgs = listOf(
      Confusion.Spec,
      ACTIVE_DURATION
    )
  ) { source, target ->
    source.applyDamage(
      target,
      effects = listOf(Confusion(ACTIVE_DURATION))
    )
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_fortification,
    descriptionRes = R.string.ability_fortification_desc,
    charges = PASSIVE_CHARGES,
    formatArgs = listOf(
      PASSIVE_HEALTH_INCREASE,
      PASSIVE_HEAL_AMOUNT,
      PASSIVE_CHARGES
    )
  ) { source, target ->
    target.increaseMaxHealth(target.maxHealth * PASSIVE_HEALTH_INCREASE / 100f)
    target.heal(PASSIVE_HEAL_AMOUNT, source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_phase_shift,
    descriptionRes = R.string.ability_phase_shift_desc,
    formatArgs = listOf(
      Vanish.Spec,
      ULTIMATE_DURATION,
      ULTIMATE_HEAL_AMOUNT
    )
  ) { source, _ ->
    source.team.getAliveMembers().forEach {
      it.addEffect(Vanish(ULTIMATE_DURATION), source)
      it.heal(it.maxHealth * ULTIMATE_HEAL_AMOUNT / 100f, source)
    }
  },
) {
  private companion object {
    const val MAX_HEALTH = 180f
    const val DAMAGE = 13f
    const val ACTIVE_DURATION = 2
    const val PASSIVE_HEALTH_INCREASE = 7
    const val PASSIVE_HEAL_AMOUNT = 20f
    const val PASSIVE_CHARGES = 2
    const val ULTIMATE_DURATION = 1
    const val ULTIMATE_HEAL_AMOUNT = 6f
  }
}
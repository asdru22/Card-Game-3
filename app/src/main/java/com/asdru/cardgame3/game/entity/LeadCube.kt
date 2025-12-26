package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Overheal
import com.asdru.cardgame3.game.effect.Shielded
import com.asdru.cardgame3.game.trait.Firewall
import com.asdru.cardgame3.game.trait.Relentless
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.helper.heal
import com.asdru.cardgame3.helper.toRoman

class LeadCube : Entity(
  name = R.string.entity_lead_cube,
  iconRes = R.drawable.entity_lead_cube,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFDAC55D),
  damageType = DamageType.Magic,
  traits = listOf(Relentless(), Firewall()),
  radarStats = RadarStats(0.4f, 0.4f, 0.9f, 0.6f, 0.7f),
  activeAbility = Ability(
    nameRes = R.string.ability_neutralize,
    descriptionRes = R.string.ability_neutralize_desc,
    charges = ACTIVE_CHARGE,
    formatArgs = listOf(ACTIVE_CHARGE)
  ) { source, target ->
    source.applyDamage(target)
    target.resetCharges()
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_density,
    descriptionRes = R.string.ability_density_desc,
    charges = PASSIVE_CHARGE,
    formatArgs = listOf(
      Overheal.Spec,
      PASSIVE_OVERHEAL_MULTIPLIER.toRoman(),
      PASSIVE_DURATION,
      PASSIVE_HEAL,
      PASSIVE_CHARGE
    )
  ) { source, target ->
    target.addEffect(
      Overheal(
        duration = PASSIVE_DURATION,
        multiplier = PASSIVE_OVERHEAL_MULTIPLIER
      ), source
    )
    target.heal(PASSIVE_HEAL, source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_lockdown,
    descriptionRes = R.string.ability_lockdown_desc,
    formatArgs = listOf(
      Shielded.Spec,
      ULTIMATE_SHIELDED_MULTIPLIER,
      ULTIMATE_EFFECT_DURATION
    )
  ) { source, randomEnemy ->
    val shieldHealth = source.maxHealth * ULTIMATE_SHIELDED_MULTIPLIER / 100f
    source.team.getAliveMembers().minByOrNull { it.health }?.let {
      it.addEffect(Shielded(ULTIMATE_EFFECT_DURATION, shieldHealth), source)
    }
  }
) {
  private companion object {
    const val MAX_HEALTH = 190f
    const val DAMAGE = 20f
    const val ACTIVE_CHARGE = 2
    const val PASSIVE_OVERHEAL_MULTIPLIER = 2
    const val PASSIVE_DURATION = 7
    const val PASSIVE_HEAL = 25f
    const val PASSIVE_CHARGE = 2
    const val ULTIMATE_SHIELDED_MULTIPLIER = 30f
    const val ULTIMATE_EFFECT_DURATION = 5
  }
}
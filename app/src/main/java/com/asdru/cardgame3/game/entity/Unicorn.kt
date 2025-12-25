package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Blinded
import com.asdru.cardgame3.game.effect.Gilded
import com.asdru.cardgame3.game.effect.Weakness
import com.asdru.cardgame3.game.trait.Executioner
import com.asdru.cardgame3.game.trait.Furious
import com.asdru.cardgame3.helper.applyDamage

class Unicorn : Entity(
  name = R.string.entity_unicorn,
  iconRes = R.drawable.entity_unicorn,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF0065FF),
  damageType = DamageType.Ranged,
  traits = listOf(Executioner(), Furious()),
  radarStats = RadarStats(0.8f, 0.2f, 0.3f, 0.8f, 0.5f),
  activeAbility = Ability(
    nameRes = R.string.ability_light_beam,
    descriptionRes = R.string.ability_light_beam_desc,
    charges = ACTIVE_CHARGES,
    formatArgs = listOf(
      Blinded.Spec,
      ACTIVE_DURATION,
      ACTIVE_CHARGES
    )
  ) { source, target ->
    source.applyDamage(target, effects = listOf(Blinded(ACTIVE_DURATION)))
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_golden_touch,
    descriptionRes = R.string.ability_golden_touch_desc,
    formatArgs = listOf(
      Gilded.Spec,
      PASSIVE_EFFECT_DURATION
    )
  ) { source, target ->
    target.addEffect(Gilded(PASSIVE_EFFECT_DURATION), source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_horn_of_retribution,
    descriptionRes = R.string.ability_horn_of_retribution_desc,
    formatArgs = listOf(
      Weakness.Spec,
      ULTIMATE_EFFECT_DURATION,
      ULTIMATE_RAGE_GAIN,
      Blinded.Spec,
      ULTIMATE_EXTRA_DAMAGE,
      ULTIMATE_EXTRA_RAGE
    )
  ) { source, _ ->
    val strongest = source.team.getTargetableEnemies().maxByOrNull { it.damage }
    strongest?.let {
      strongest.addEffect(Weakness(ULTIMATE_EFFECT_DURATION), source)
      source.team.increaseRage(ULTIMATE_RAGE_GAIN)
      if (strongest.effectManager.hasEffect<Blinded>()) {
        source.applyDamage(strongest, ULTIMATE_EXTRA_DAMAGE)
        source.team.increaseRage(ULTIMATE_EXTRA_RAGE)
      }
    }
  }
) {
  companion object {
    const val MAX_HEALTH = 160f
    const val DAMAGE = 18f
    const val ACTIVE_DURATION = 2
    const val ACTIVE_CHARGES = 2
    const val PASSIVE_EFFECT_DURATION = 3
    const val ULTIMATE_EFFECT_DURATION = 2
    const val ULTIMATE_RAGE_GAIN = 10f
    const val ULTIMATE_EXTRA_DAMAGE = 15f
    const val ULTIMATE_EXTRA_RAGE = 10f
  }
}
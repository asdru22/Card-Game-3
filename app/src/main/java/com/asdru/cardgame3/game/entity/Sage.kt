package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Defiance
import com.asdru.cardgame3.game.effect.Inspired
import com.asdru.cardgame3.game.trait.Artist
import com.asdru.cardgame3.game.trait.Old
import com.asdru.cardgame3.game.trait.Resilience
import com.asdru.cardgame3.game.trait.Trait
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.helper.heal
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Sage : Entity(
  name = R.string.entity_sage,
  iconRes = R.drawable.entity_sage,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE, damageMultiplier = DMG_MULT),
  color = Color(0xFFA85C52),
  damageType = DamageType.Magic,
  traits = listOf(Resilience(), Old()),
  radarStats = RadarStats(0.6f, 0.4f, 0.7f, 0.5f, 0.5f),
  activeAbility = Ability(
    nameRes = R.string.ability_affliction,
    descriptionRes = R.string.ability_affliction_desc,
    formatArgs = listOf(DMG_MULT, EFFECT_EXTRA_DMG),
  ) { source, target ->
    val effects = target.effectManager.effects.size
    var effectMult = EFFECT_EXTRA_DMG
    source.traits.filterIsInstance<Resilience>().firstOrNull()?.let {
      if (it.getCharge(source) == it.maxCharges) {
        effectMult += Resilience.getActiveExtraDmg()
        it.resetCharge(source)
      }
    }
    val damage = source.damage + effects * effectMult
    source.applyDamage(target, damage)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_mend,
    descriptionRes = R.string.ability_mend_desc,
    formatArgs = listOf(HEAL_PERCENTAGE)
  ) { source, target ->
    val heal = target.maxHealth * HEAL_PERCENTAGE / 100f
    target.heal(heal, source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_guidance,
    descriptionRes = R.string.ability_guidance_desc,
    formatArgs = listOf(ULTIMATE_NOTES_GAINED, Inspired.Spec, ULTIMATE_EFFECT_DURATION)
  ) { source, _ ->
    val lowestHealthEntity = source.team.getAliveMembers().minByOrNull { it.health }
    lowestHealthEntity?.let {
      source.team.getAliveMembers().forEach { member ->
        delay(200)
        member.passiveAbility.effect(member, lowestHealthEntity)
      }
    }
  }
) {
  private companion object {
    const val MAX_HEALTH = 120f
    const val DAMAGE = 21f
    const val DMG_MULT = 100f
    const val EFFECT_EXTRA_DMG = 3f
    const val HEAL_PERCENTAGE = 7f
    const val EFFECT_CLEAR_TRESHOLD = 16
    const val ULTIMATE_NOTES_GAINED = 15
    const val ULTIMATE_EFFECT_DURATION = 2
  }
}
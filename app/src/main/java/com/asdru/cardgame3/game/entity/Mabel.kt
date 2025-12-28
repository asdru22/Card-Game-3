package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.PainLink
import com.asdru.cardgame3.game.effect.Protection
import com.asdru.cardgame3.game.effect.SpikedShield
import com.asdru.cardgame3.game.effect.Taunt
import com.asdru.cardgame3.game.trait.Juggernaut
import com.asdru.cardgame3.game.trait.Retaliate
import com.asdru.cardgame3.game.trait.Spite
import com.asdru.cardgame3.game.trait.Ugly
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.helper.changeHealth

class Mabel : Entity(
  name = R.string.entity_mabel,
  iconRes = R.drawable.entity_mabel,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFF17BF6),
  damageType = DamageType.Melee,
  traits = listOf(Ugly(), Juggernaut(32f), Retaliate()),
  radarStats = RadarStats(0.1f, 0.8f, 0.3f, 0.8f, 0.5f),
  activeAbility = Ability(
    nameRes = R.string.ability_bark,
    descriptionRes = R.string.ability_bark_desc,
    formatArgs = listOf(
      Taunt.Spec,
      ACTIVE_DURATION
    )
  ) { source, target ->
    source.applyDamage(target, effects = listOf(Taunt(ACTIVE_DURATION)))
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_good_girl,
    descriptionRes = R.string.ability_good_girl_desc,
    formatArgs = listOf(
      PainLink.Spec,
      PASSIVE_DURATION
    )
  ) { source, target ->
    target.addEffect(PainLink(PASSIVE_DURATION, source), source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_level,
    descriptionRes = R.string.ability_level_desc,
    formatArgs = listOf(
      Protection.Spec,
      ULTIMATE_EFFECT_DURATION
    )
  ) { source, _ ->
    val members = source.team.getAliveMembers()

    val totalHealth = members.map { it.health }.sum()
    val finalHealth = totalHealth / members.size
    members.forEach {
      it.health = 0.1f
      it.changeHealth(finalHealth, false)
      it.addEffect(Protection(ULTIMATE_EFFECT_DURATION), source)
    }
  }
) {
  private companion object {
    const val MAX_HEALTH = 190f
    const val DAMAGE = 0f
    const val ACTIVE_DURATION = 2
    const val PASSIVE_DURATION = 2
    const val ULTIMATE_EFFECT_DURATION = 2

  }
}
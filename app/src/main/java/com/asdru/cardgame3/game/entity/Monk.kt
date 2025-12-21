package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.SpikedShield
import com.asdru.cardgame3.game.trait.Ironclad
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.helper.heal

class Monk : Entity(
  name = R.string.entity_monk,
  iconRes = R.drawable.entity_monk,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFDA855D),
  damageType = DamageType.Magic,
  traits = listOf(Ironclad()),
  radarStats = RadarStats(0.5f, 0.7f, 0.6f, 0.4f, 0.5f),
  activeAbility = Ability(
    nameRes = R.string.ability_syphon,
    descriptionRes = R.string.ability_syphon_desc,
  ) { source, target ->
    val healAmount = source.applyDamage(target) / target.team.getAliveEnemies().size
    source.team.getAliveMembers().forEach { it.heal(healAmount, source) }
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_iron_will,
    descriptionRes = R.string.ability_iron_will_desc,
    formatArgs = listOf(
      SpikedShield.Spec,
      PASSIVE_DURATION
    )
  ) { source, target ->
    target.addEffect(SpikedShield(PASSIVE_DURATION), source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_liberation,
    descriptionRes = R.string.ability_liberation_desc,
    formatArgs = listOf(
      ULTIMATE_DAMAGE_MULTIPLIER
    )
  ) { source, randomEnemy ->
    var effectsCleared = 0
    source.team.getAliveMembers().forEach { effectsCleared += it.effectManager.clearAll(it) }
    source.applyDamage(randomEnemy, effectsCleared * ULTIMATE_DAMAGE_MULTIPLIER)
  }
) {
  private companion object {
    const val MAX_HEALTH = 190f
    const val DAMAGE = 20f
    const val PASSIVE_DURATION = 2
    const val ULTIMATE_DAMAGE_MULTIPLIER = 10f

  }
}
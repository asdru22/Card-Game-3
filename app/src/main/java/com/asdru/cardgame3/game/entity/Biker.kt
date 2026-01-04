package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.trait.ClutchBurn
import com.asdru.cardgame3.game.trait.Forsaken
import com.asdru.cardgame3.game.trait.Revving
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.helper.heal

class Biker : Entity(
  name = R.string.entity_biker,
  iconRes = R.drawable.entity_biker,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF7C7694),
  damageType = DamageType.Melee,
  traits = listOf(Revving(), ClutchBurn(), Forsaken()),
  radarStats = RadarStats(0.95f, 0.25f, 0.1f, 0.1f, 0.65f),
  activeAbility = Ability(
    nameRes = R.string.ability_chain_whip,
    descriptionRes = R.string.ability_chain_whip_desc,
    formatArgs = listOf(DMG_MULT, DMG_MIN),
  ) { source, target ->
    val damage = (target.health * DMG_MULT / 100f).coerceAtLeast(DMG_MIN)
    source.applyDamage(target, damage)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_pit_stop,
    descriptionRes = R.string.ability_pit_stop_desc,
    formatArgs = listOf(OTHER_HEAL, SELF_HEAL)
  ) { source, target ->
    val healAmount = if (source == target) SELF_HEAL else OTHER_HEAL
    target.heal(healAmount, source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_collision_course,
    descriptionRes = R.string.ability_collision_course_desc,
    formatArgs = listOf(ULTIMATE_DMG_SCALE, ULTIMATE_DMG_CAP)
  ) { source, _ ->
    val enemy = source.team.getTargetableEnemies().maxByOrNull { it.health }
    enemy?.let {
      val damage =
        ((enemy.health - source.health) * ULTIMATE_DMG_SCALE / 100f).coerceIn(0f, ULTIMATE_DMG_CAP)
      source.applyDamage(enemy, damage)
    }
  }
) {
  private companion object {
    const val MAX_HEALTH = 95f
    const val DAMAGE = 0f
    const val DMG_MULT = 16f
    const val DMG_MIN = 7f
    const val SELF_HEAL = 21f
    const val OTHER_HEAL = 6f
    const val ULTIMATE_DMG_SCALE = 40f
    const val ULTIMATE_DMG_CAP = 60f
  }
}
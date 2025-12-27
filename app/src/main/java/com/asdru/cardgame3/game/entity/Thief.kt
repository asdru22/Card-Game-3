package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.trait.Sidestep
import com.asdru.cardgame3.helper.applyDamage

class Thief : Entity(
  name = R.string.entity_thief,
  iconRes = R.drawable.entity_thief,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE, damageMultiplier = DMG_MULT),
  color = Color(0xFF009688),
  damageType = DamageType.Melee,
  traits = listOf(Sidestep(7f)),
  radarStats = RadarStats(0.4f, 0.3f, 0.6f, 0.5f, 0.5f),
  activeAbility = Ability(
    nameRes = R.string.ability_pickpocket,
    descriptionRes = R.string.ability_pickpocket_desc,
    formatArgs = listOf(DMG_MULT, ACTIVE_STEAL)
  ) { source, target ->
    source.applyDamage(target)
    val coinsStolen = target.team.shop.removeCoins(ACTIVE_STEAL)
    source.team.shop.addCoins(coinsStolen)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_stash,
    descriptionRes = R.string.ability_stash_desc,
    formatArgs = listOf(PASSIVE_COINS)
  ) { source, target ->
    target.team.shop.addCoins(PASSIVE_COINS)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_heist,
    descriptionRes = R.string.ability_heist_desc,
    formatArgs = listOf(ULTIMATE_COINS_STOLEN)
  ) { source, _ ->
    val enemies = source.team.getTargetableEnemies()
    enemies.forEach { enemy ->
      val effectToSteal = enemy.effectManager.getRandomPositiveEffect()
      if (effectToSteal != null) {
        val stolenEffect = enemy.effectManager.removeEffect(effectToSteal, enemy, true)
        if (stolenEffect != null) {
          source.addEffect(stolenEffect, source)
        }
      } else {
        val coinsStolen = enemy.team.shop.removeCoins(ULTIMATE_COINS_STOLEN)
        source.team.shop.addCoins(coinsStolen)
      }
    }
  }
) {
  private companion object {
    const val MAX_HEALTH = 160f
    const val DAMAGE = 16f
    const val DMG_MULT = 100f
    const val ACTIVE_STEAL = 3
    const val PASSIVE_COINS = 8
    const val ULTIMATE_COINS_STOLEN = 12
  }
}
package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Precision
import com.asdru.cardgame3.game.trait.QuickDraw
import com.asdru.cardgame3.viewModel.applyDamage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Rouge : Entity(
  name = R.string.entity_rouge,
  iconRes = R.drawable.entity_rouge,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFFF5722),
  damageType = DamageType.Ranged,
  traits = listOf(QuickDraw()),
  radarStats = RadarStats(0.9f, 0.1f, 0.4f, 0.0f, 0.3f),
  activeAbility = Ability(
    nameRes = R.string.ability_bullseye,
    descriptionRes = R.string.ability_bullseye_desc
  ) { source, target ->
    source.applyDamage(target)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_take_aim,
    descriptionRes = R.string.ability_take_aim_desc,
    formatArgs = listOf(
      Precision.Spec,
      PASSIVE_DURATION
    )
  ) { source, target ->
    target.addEffect(Precision(PASSIVE_DURATION), source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_barrage,
    descriptionRes = R.string.ability_barrage_desc,
    formatArgs = listOf(
      ULTIMATE_ACTIVE_TIMES
    )
  ) { source, randomEnemy ->
    val rangers = source.team.getAliveMembers().filter { it.damageType == DamageType.Ranged }

    CoroutineScope(Dispatchers.Main).launch {
      repeat(ULTIMATE_ACTIVE_TIMES) {
        rangers.forEach { ranger ->
          delay(200)
          if (ranger.isAlive) {
            val target = randomEnemy.team.getAliveMembers().randomOrNull()
            if (target != null) {
              ranger.entity.activeAbility.effect(ranger, target)
            }
          }
        }
      }
    }
  }
) {
  private companion object {
    const val MAX_HEALTH = 140f
    const val DAMAGE = 22f
    const val PASSIVE_DURATION = 2
    const val ULTIMATE_ACTIVE_TIMES = 2
  }
}
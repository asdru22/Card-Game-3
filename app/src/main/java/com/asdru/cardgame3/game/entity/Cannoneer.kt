package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Bursting
import com.asdru.cardgame3.viewModel.applyDamage
import com.asdru.cardgame3.viewModel.applyDamageToTargets
import kotlinx.coroutines.delay

class Cannoneer : Entity(
  name = R.string.entity_cannoneer,
  iconRes = R.drawable.entity_cannoneer,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF49BE00),
  damageType = DamageType.Ranged,
  activeAbility = Ability(
    nameRes = R.string.ability_scattershot,
    descriptionRes = R.string.ability_scattershot_desc,
    formatArgs = listOf(
      ACTIVE_TARGET_MULTIPLIER,
      ACTIVE_OTHER_MULTIPLIER
    )
  ) { source, target ->
    source.applyDamage(target, DAMAGE * ACTIVE_TARGET_MULTIPLIER / 100)
    source.applyDamageToTargets(
      target.team.getOtherAliveMembers(target),
      DAMAGE * ACTIVE_OTHER_MULTIPLIER / 100f,
      playAttackAnimation = false
    )
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_prime,
    descriptionRes = R.string.ability_prime_desc,
    formatArgs = listOf(
      Bursting.Spec,
      PASSIVE_DURATION
    )
  ) { source, target ->
    target.addEffect(Bursting(PASSIVE_DURATION), source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_carpet_bomb,
    descriptionRes = R.string.ability_carpet_bomb_desc,
    formatArgs = listOf(
      ULTIMATE_TIMES,
      ULTIMATE_DAMAGE
    )
  ) { source, _ ->
    val initialTarget = source.team.getTargetableEnemies().ifEmpty { null }?.random()

    if (initialTarget != null) {
      source.onGetAttackOffset?.invoke(initialTarget)?.let {
        source.attackAnimOffset = it
      }
      delay(200)
    }

    try {
      repeat(ULTIMATE_TIMES) {
        val targets = source.team.getTargetableEnemies()
        if (targets.isNotEmpty()) {
          source.applyDamage(
            targets.random(),
            ULTIMATE_DAMAGE,
            playAttackAnimation = false
          )
        }
        delay(150)
      }
    } finally {
      if (source.attackAnimOffset != null) {
        source.attackAnimOffset = null
        delay(200)
      }
    }
  }
) {
  private companion object {
    const val MAX_HEALTH = 110f
    const val DAMAGE = 30f
    const val ACTIVE_TARGET_MULTIPLIER = 100
    const val ACTIVE_OTHER_MULTIPLIER = 30
    const val PASSIVE_DURATION = 3
    const val ULTIMATE_TIMES = 6
    const val ULTIMATE_DAMAGE = 12f

  }
}
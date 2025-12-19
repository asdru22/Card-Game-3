package com.asdru.cardgame3.helper

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.game.effect.StatusEffect
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlinx.coroutines.delay


suspend fun EntityViewModel.receiveDamage(amount: Float, source: EntityViewModel? = null): Float {
  var actualDamage = amount

  onGetWeather?.invoke()?.let { weather ->
    actualDamage = weather.modifyIncomingDamage(this, source, actualDamage)
  }

  effectManager.effects.toList().forEach { effect ->
    actualDamage = effect.modifyIncomingDamage(this, actualDamage, source)
  }

  applyTraits { trait ->
    actualDamage = trait.modifyIncomingDamage(this, source, actualDamage)
  }

  if (actualDamage > 0) {
    if (source != null) {
      hitAnimTrigger++
    }

    val wasAlive = isAlive
    val overkill = (actualDamage - health).coerceAtLeast(0f)

    health = (health - actualDamage).coerceAtLeast(0f)
    popupManager.add(actualDamage, Color.Red)

    if (wasAlive && !isAlive) {
      applyTraits { it.onDidReceiveDamage(this, source, actualDamage) }
      onEntityDeathTrait(this)
      applyTraits { it.onDeath(this) }
      effectManager.clearAll(this)
      resetCharges()
    } else if (isAlive) {
      applyTraits { it.onDidReceiveDamage(this, source, actualDamage) }
    }

    source?.applyTraits { trait ->
      trait.onDidDealDamage(source, this, actualDamage, overkill)
    }
  }

  return actualDamage
}

private suspend fun onEntityDeathTrait(deadEntity: EntityViewModel) {
  val allEntities = deadEntity.team.run {
    getAliveMembers() + getAliveEnemies()
  }
  allEntities.forEach {
    it.applyTraits { trait -> trait.onEntityDeath(deadEntity, it) }
  }
}

suspend fun EntityViewModel.heal(
  amount: Float,
  source: EntityViewModel? = null,
  repeats: Int = 1,
  delayTime: Long = 400
) {
  repeat(repeats) {
    var actualHeal = amount
    applyTraits {
      actualHeal = it.modifyHeal(this, source, actualHeal)
    }

    effectManager.effects.forEach {
      actualHeal = it.modifyIncomingHealing(this, actualHeal, source)
    }

    val newHealth = (health + actualHeal).coerceAtMost(maxHealth)
    val healDiff = newHealth - health
    this.team.totalHealing += healDiff
    health = newHealth
    if (actualHeal > 0) popupManager.add(actualHeal, Color.Green)
    if (repeats > 1) delay(delayTime)
  }
}

suspend fun EntityViewModel.applyDamage(
  target: EntityViewModel,
  amount: Float = damage,
  repeats: Int = 1,
  delayTime: Long = 400,
  playAttackAnimation: Boolean = true,
  effects: List<StatusEffect> = emptyList(),
  rageDecrease: Float = 0f
): Float {
  var totalDamage = 0f

  try {
    if (playAttackAnimation) {
      onGetAttackOffset?.invoke(target)?.let {
        attackAnimOffset = it
        delay(200)
      }
    }

    repeat(repeats) {
      if (!target.isAlive || !isAlive) return totalDamage

      var calculatedDamage = amount
      applyTraits { trait ->
        calculatedDamage = trait.modifyOutgoingDamage(this, target, calculatedDamage)
      }

      effectManager.effects.forEach {
        calculatedDamage = it.modifyOutgoingDamage(this, calculatedDamage, target)

      }

      totalDamage += target.receiveDamage(calculatedDamage, source = this)

      if (rageDecrease > 0f) {
        target.team.decreaseRage(rageDecrease)
      }

      if (repeats > 1) delay(delayTime)
    }

    if (target.isAlive && isAlive && effects.isNotEmpty()) {
      effects.forEach { effect ->
        target.addEffect(effect, this)
      }
    }
  } finally {
    if (playAttackAnimation && attackAnimOffset != null) {
      attackAnimOffset = null
      delay(200)
    }
  }
  this.team.totalDamageDealt += totalDamage
  return totalDamage
}

suspend fun EntityViewModel.applyDamageToTargets(
  targets: List<EntityViewModel>,
  amount: Float = damage,
  repeats: Int = 1,
  delayTime: Long = 400,
  playAttackAnimation: Boolean = true,
  effects: List<StatusEffect> = emptyList(),
  rageDecrease: Float = 0f
): Float {
  var totalDamage = 0f

  try {
    if (playAttackAnimation && targets.isNotEmpty()) {
      onGetAttackOffset?.invoke(targets.random())?.let {
        attackAnimOffset = it
        delay(200)
      }
    }

    repeat(repeats) {
      if (!isAlive) return totalDamage

      targets.forEach { target ->
        totalDamage += applyDamage(
          target,
          amount,
          repeats = 1,
          delayTime = 0,
          playAttackAnimation = false,
          effects = effects,
          rageDecrease = rageDecrease
        )
      }

      if (repeats > 1) delay(delayTime)
    }
  } finally {
    if (playAttackAnimation && attackAnimOffset != null) {
      attackAnimOffset = null
      delay(200)
    }
  }

  return totalDamage
}

suspend fun EntityViewModel.withTemporaryDamage(tempDamage: Float, block: suspend () -> Unit) {
  val originalDamage = damage
  damage = tempDamage
  try {
    block()
  } finally {
    damage = originalDamage
  }
}

fun EntityViewModel.increaseMaxHealth(amount: Float) {
  maxHealth += amount
}
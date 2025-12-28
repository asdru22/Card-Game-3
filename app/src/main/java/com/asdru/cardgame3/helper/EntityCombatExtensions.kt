package com.asdru.cardgame3.helper

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.data.DamageData
import com.asdru.cardgame3.game.effect.StatusEffect
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlinx.coroutines.delay


suspend fun EntityViewModel.receiveDamage(
  amount: Float,
  source: EntityViewModel? = null,
  damageData: DamageData? = null
): Float {
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
    hitAnimTrigger++

    val wasAlive = isAlive

    // consume overheal first
    var damageRemaining = actualDamage
    if (overhealAmount > 0) {
      val absorbed = damageRemaining.coerceAtMost(overhealAmount)
      overhealAmount -= absorbed
      damageRemaining -= absorbed
    }

    val overkill = (damageRemaining - health).coerceAtLeast(0f)

    health = (health - damageRemaining).coerceAtLeast(0f)
    popupManager.add(actualDamage, Color.Red)
    team.onTeamDamage(actualDamage)

    if (wasAlive && !isAlive) {
      onDeath(source, actualDamage)
    } else if (isAlive) {
      applyTraits { it.onDidReceiveDamage(this, source, actualDamage, damageData) }
    }

    source?.applyTraits { trait ->
      trait.onDidDealDamage(source, this, actualDamage, overkill)
    }
  }

  return actualDamage
}

suspend fun EntityViewModel.onDeath(
  source: EntityViewModel?,
  actualDamage: Float
) {
  onEntityDeathTrait(this)
  applyTraits { it.onDeath(this) }
  effectManager.clearAll(this, ignoreMultipliers = true)
  resetCharges()
}

private suspend fun onEntityDeathTrait(deadEntity: EntityViewModel) {
  val allEntities = deadEntity.team.run {
    getAliveMembers() + getAliveEnemies()
  }
  allEntities.forEach {
    it.applyTraits { trait -> trait.onEntityDeath(it, deadEntity) }
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

    changeHealth(actualHeal, true)

    if (actualHeal > 0) popupManager.add(actualHeal, Color.Green)
    if (repeats > 1) delay(delayTime)
  }
}

fun EntityViewModel.changeHealth(amount: Float, register: Boolean) {
  val overhealAllowed = effectManager.effects.sumOf { it.overheal().toDouble() }.toFloat()

  // First fill health
  val missingHealth = maxHealth - health
  val healToHealth = amount.coerceAtMost(missingHealth)
  health = (health + healToHealth).coerceAtMost(maxHealth)

  if (register) this.team.totalHealing += healToHealth

  val remainingHeal = amount - healToHealth

  // Then fill overheal
  if (remainingHeal > 0 && overhealAllowed > 0) {
    val currentOverheal = overhealAmount
    val spaceForOverheal = overhealAllowed - currentOverheal
    val healToOverheal = remainingHeal.coerceAtMost(spaceForOverheal)
    overhealAmount += healToOverheal
    if (register) this.team.totalHealing += healToOverheal
  }
}

suspend fun EntityViewModel.applyDamage(
  target: EntityViewModel,
  amount: Float = damage,
  repeats: Int = 1,
  delayTime: Long = 400,
  playAttackAnimation: Boolean = true,
  effects: List<StatusEffect> = emptyList(),
  damageData: DamageData? = null
): Float {
  var totalDamage = 0f

  try {
    if (playAttackAnimation) {
      onGetAttackOffset?.invoke(target)?.let {
        attackAnimOffset = it
        delay(200)
      }
    }

    if (target.isAlive && isAlive && effects.isNotEmpty()) {
      effects.forEach { effect ->
        target.addEffect(effect, this)
      }
    }

    repeat(repeats) {
      if (!target.isAlive || !isAlive) return totalDamage

      damageData?.let {
        this.team.increaseRage(it.ownRageIncrease)
      }

      var calculatedDamage =
        (amount * this.entity.initialStats.damageMultiplier / 100f) * (1 + (damageData?.damageDecay
          ?: 0f) * it / 100)
      applyTraits { trait ->
        calculatedDamage = trait.modifyOutgoingDamage(this, target, calculatedDamage)
      }

      effectManager.effects.forEach {
        calculatedDamage = it.modifyOutgoingDamage(this, calculatedDamage, target)

      }


      totalDamage += target.receiveDamage(calculatedDamage, source = this, damageData = damageData)

      effectManager.effects.forEach {
        it.postDamageDealt(this, target, totalDamage)
      }


      damageData?.run {
        target.team.decreaseRage(enemyRageDecrease)
      }


      if (repeats > 1) delay(delayTime)
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
  damageData: DamageData? = null
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
          damageData

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
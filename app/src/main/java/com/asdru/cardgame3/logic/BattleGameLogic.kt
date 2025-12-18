package com.asdru.cardgame3.logic

import androidx.lifecycle.viewModelScope
import com.asdru.cardgame3.game.weather.WeatherEvent
import com.asdru.cardgame3.viewModel.BattlePhase
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.TeamViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class BattleGameLogic(private val vm: BattleViewModel) {
  fun startGame(
    newLeftTeam: TeamViewModel,
    newRightTeam: TeamViewModel,
    weatherEnabled: Boolean,
    turnTimer: Int
  ) {
    vm.leftTeam = newLeftTeam
    vm.rightTeam = newRightTeam
    vm.leftTeam.enemyTeam = vm.rightTeam
    vm.rightTeam.enemyTeam = vm.leftTeam

    setupEntityCallbacks(vm.leftTeam.entities + vm.rightTeam.entities)

    vm.currentWeather = if (weatherEnabled) {
      WeatherEvent.getRandomWeather()?.also { it.onApply(vm) }
    } else null

    val startLeft = Random.nextBoolean()
    vm.roundStarterIsLeft = startLeft

    vm.currentPhase = if (startLeft) BattlePhase.ENTITIES_LEFT else BattlePhase.ENTITIES_RIGHT
    vm.isLeftTeamTurn = startLeft

    vm.isActionPlaying = false
    vm.winner = null
    vm.actionsTaken.clear()
    vm.cardBounds.clear()
    vm.leftTeam.rage = 0f
    vm.rightTeam.rage = 0f
    vm.maxTurnTimeSeconds = turnTimer

    vm.battleTimer.init(turnTimer)
    vm.battleTimer.start(checkPauseConditions = {
      vm.isActionPlaying || vm.showInfoDialog || vm.showExitDialog || vm.showWeatherInfo ||
          (vm.currentPhase == BattlePhase.SUMMONS_LEFT || vm.currentPhase == BattlePhase.SUMMONS_RIGHT)
    })
  }

  private fun setupEntityCallbacks(entities: List<EntityViewModel>) {
    entities.forEach { entity ->
      entity.onGetWeather = { vm.currentWeather }
      entity.onGetAttackOffset = { target ->
        val sourceBounds = vm.cardBounds[entity]
        val targetBounds = vm.cardBounds[target]
        if (sourceBounds != null && targetBounds != null) {
          (targetBounds.center - sourceBounds.center) * 0.7f
        } else null
      }
    }
  }

  fun executeInteraction(source: EntityViewModel, target: EntityViewModel) {
    if (vm.isActionPlaying || vm.winner != null) return
    vm.battleTimer.reset()

    vm.viewModelScope.launch {
      vm.isActionPlaying = true

      val sourceLeft = vm.leftTeam.entities.contains(source)
      val targetLeft = vm.leftTeam.entities.contains(target)

      BattleCombatLogic.executeCardInteraction(
        source,
        target,
        isSameTeam = (sourceLeft == targetLeft)
      )

      val sourceTeam = if (sourceLeft) vm.leftTeam else vm.rightTeam
      sourceTeam.increaseRage(10f)

      checkWinCondition()
      if (vm.winner == null) {
        if (!vm.actionsTaken.contains(source)) vm.actionsTaken.add(source)
        checkTurnAdvance()
      }
      vm.isActionPlaying = false
    }
  }

  fun executeUltimate(team: TeamViewModel, caster: EntityViewModel) {
    if (vm.isActionPlaying || vm.winner != null) return

    vm.viewModelScope.launch {
      vm.isActionPlaying = true
      team.rage = 0f
      val enemies = if (team == vm.leftTeam) vm.rightTeam else vm.leftTeam
      val validTargets = enemies.aliveEntities

      if (validTargets.isNotEmpty()) {
        val randomEnemy = validTargets.random()
        caster.entity.ultimateAbility.effect(caster, randomEnemy)
      }
      checkWinCondition()
      vm.isActionPlaying = false
    }
  }

  fun triggerTimeoutAction() {
    if (vm.isActionPlaying || vm.winner != null) return

    if (vm.currentPhase != BattlePhase.ENTITIES_LEFT && vm.currentPhase != BattlePhase.ENTITIES_RIGHT) return

    val currentTeam = if (vm.currentPhase == BattlePhase.ENTITIES_LEFT) vm.leftTeam else vm.rightTeam
    val capableEntities = currentTeam.entities.filter {
      it.isAlive && !it.effectManager.isStunned && !vm.actionsTaken.contains(it)
    }

    if (capableEntities.isEmpty()) return

    val source = capableEntities.random()
    val useActive = Random.nextBoolean()
    var target: EntityViewModel? = if (useActive) {
      currentTeam.enemyTeam.getRandomTargetableEnemy()
    } else {
      currentTeam.getRandomAliveMember()
    }

    if (target == null) {
      target = if (useActive) {
        currentTeam.entities.randomOrNull()
      } else {
        currentTeam.enemyTeam.entities.randomOrNull()
      }
    }

    if (target != null && target.isAlive) {
      vm.dragState = null
      vm.hoveredTarget = null
      executeInteraction(source, target)
    }
  }


  private suspend fun checkTurnAdvance() {
    val isEntityPhase = vm.currentPhase == BattlePhase.ENTITIES_LEFT || vm.currentPhase == BattlePhase.ENTITIES_RIGHT
    if (!isEntityPhase) return

    val activeTeamEntities = if (vm.currentPhase == BattlePhase.ENTITIES_LEFT) vm.leftTeam.entities else vm.rightTeam.entities
    val capableEntities = activeTeamEntities.filter { it.isAlive && !it.effectManager.isStunned }

    if (vm.actionsTaken.containsAll(capableEntities)) {
      advanceTurn()
    }
  }

  private suspend fun advanceTurn() {
    val current = vm.currentPhase
    val startLeft = vm.roundStarterIsLeft


    val nextPhase = if (startLeft) {
      when (current) {
        BattlePhase.ENTITIES_LEFT -> BattlePhase.ENTITIES_RIGHT
        BattlePhase.ENTITIES_RIGHT -> BattlePhase.SUMMONS_LEFT
        BattlePhase.SUMMONS_LEFT -> BattlePhase.SUMMONS_RIGHT
        BattlePhase.SUMMONS_RIGHT -> BattlePhase.ENTITIES_LEFT
      }
    } else {
      when (current) {
        BattlePhase.ENTITIES_RIGHT -> BattlePhase.ENTITIES_LEFT
        BattlePhase.ENTITIES_LEFT -> BattlePhase.SUMMONS_RIGHT
        BattlePhase.SUMMONS_RIGHT -> BattlePhase.SUMMONS_LEFT
        BattlePhase.SUMMONS_LEFT -> BattlePhase.ENTITIES_RIGHT
      }
    }

    if (current == BattlePhase.ENTITIES_LEFT) processEndOfTurnEffects(vm.leftTeam)
    if (current == BattlePhase.ENTITIES_RIGHT) processEndOfTurnEffects(vm.rightTeam)

    vm.currentPhase = nextPhase
    vm.actionsTaken.clear()

    if (nextPhase == BattlePhase.ENTITIES_LEFT) vm.isLeftTeamTurn = true
    if (nextPhase == BattlePhase.ENTITIES_RIGHT) vm.isLeftTeamTurn = false

    if (nextPhase == BattlePhase.ENTITIES_LEFT || nextPhase == BattlePhase.ENTITIES_RIGHT) {
      vm.battleTimer.reset()
      val nextTeam = if (nextPhase == BattlePhase.ENTITIES_LEFT) vm.leftTeam else vm.rightTeam
      processStartOfTurnEffects(nextTeam)

      checkWinCondition()
      if (vm.winner != null) return

      // Skip turn if whole team is stunned
      val aliveMembers = nextTeam.entities.filter { it.isAlive }
      if (aliveMembers.isNotEmpty() && aliveMembers.all { it.effectManager.isStunned }) {
        delay(500)
        advanceTurn()
      }
    } else {
      processSummonPhase(nextPhase)
    }
  }

  private suspend fun processSummonPhase(phase: BattlePhase) {
    val team = if (phase == BattlePhase.SUMMONS_LEFT) vm.leftTeam else vm.rightTeam

    val activeSummons = team.entities.mapNotNull { it.activeSummon }
      .filter { it.isAlive }

    if (activeSummons.isEmpty()) {
      advanceTurn()
      return
    }

    vm.isActionPlaying = true
    delay(500)

    for (summon in activeSummons) {
      val owner = summon.owner
      val target = summon.target

      if (owner.isAlive && target != null && target.isAlive) {
        summon.summon.ability.effect(owner, target)
        delay(600)
      }
    }

    checkWinCondition()
    vm.isActionPlaying = false

    if (vm.winner == null) {
      advanceTurn()
    }
  }

  private suspend fun processStartOfTurnEffects(team: TeamViewModel) {
    vm.currentWeather?.onStartTurn(vm)
    team.getAliveMembers().forEach { entity ->
      entity.traits.forEach { it.onStartTurn(entity) }
      val activeEffects = entity.effectManager.effects.toList()
      activeEffects.forEach { effect ->
        effect.onStartTurn(entity)
        if (effect.tick()) entity.removeEffect(effect)
      }
    }
  }

  private suspend fun processEndOfTurnEffects(team: TeamViewModel) {
    vm.currentWeather?.onEndTurn(vm)
    team.getAliveMembers().forEach { entity ->
      entity.traits.forEach { it.onEndTurn(entity) }
      val activeEffects = entity.effectManager.effects.toList()
      activeEffects.forEach { effect ->
        effect.onEndTurn(entity)
      }
    }
  }

  // --- Rules & Validators ---

  fun canEntityAct(entity: EntityViewModel): Boolean {
    if (vm.winner != null) return false
    val isLeft = vm.leftTeam.entities.contains(entity)

    val allowedPhase = if (isLeft) BattlePhase.ENTITIES_LEFT else BattlePhase.ENTITIES_RIGHT

    return vm.currentPhase == allowedPhase &&
        !vm.actionsTaken.contains(entity) &&
        entity.isAlive &&
        !vm.isActionPlaying &&
        !entity.effectManager.isStunned
  }

  private suspend fun checkWinCondition() {
    val isLeftAlive = vm.leftTeam.aliveEntities.isNotEmpty()
    val isRightAlive = vm.rightTeam.aliveEntities.isNotEmpty()

    if (!isLeftAlive || !isRightAlive) {
      delay(1000)
      vm.winner = if (!isLeftAlive) vm.rightTeam.name else vm.leftTeam.name
    }
  }
}
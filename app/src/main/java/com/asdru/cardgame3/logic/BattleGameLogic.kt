package com.asdru.cardgame3.logic

import androidx.lifecycle.viewModelScope
import com.asdru.cardgame3.game.weather.WeatherEvent
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.TeamViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class BattleGameLogic(private val vm: BattleViewModel) {

    // --- Initialization ---

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

        vm.isLeftTeamTurn = Random.nextBoolean()
        vm.isActionPlaying = false
        vm.winner = null
        vm.actionsTaken.clear()
        vm.cardBounds.clear()
        vm.leftTeam.rage = 0f
        vm.rightTeam.rage = 0f
        vm.maxTurnTimeSeconds = turnTimer

        vm.battleTimer.init(turnTimer)
        vm.battleTimer.start(checkPauseConditions = { 
            vm.isActionPlaying || vm.showInfoDialog || vm.showExitDialog || vm.showWeatherInfo 
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

    // --- Action Execution ---

    fun executeInteraction(source: EntityViewModel, target: EntityViewModel) {
        if (vm.isActionPlaying || vm.winner != null) return
        vm.battleTimer.reset()

        vm.viewModelScope.launch {
            vm.isActionPlaying = true

            val sourceLeft = vm.leftTeam.entities.contains(source)
            val targetLeft = vm.leftTeam.entities.contains(target)
            
            // Invoke static combat logic
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

        val currentTeam = if (vm.isLeftTeamTurn) vm.leftTeam else vm.rightTeam
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
            // Clear drag state just in case user was holding a card during timeout
            vm.dragState = null
            vm.hoveredTarget = null
            executeInteraction(source, target)
        }
    }

    // --- Turn Management ---

    private suspend fun checkTurnAdvance() {
        val activeTeamEntities = if (vm.isLeftTeamTurn) vm.leftTeam.entities else vm.rightTeam.entities
        val capableEntities = activeTeamEntities.filter { it.isAlive && !it.effectManager.isStunned }
        if (vm.actionsTaken.containsAll(capableEntities)) {
            advanceTurn()
        }
    }

    private suspend fun advanceTurn() {
        val currentTeam = if (vm.isLeftTeamTurn) vm.leftTeam else vm.rightTeam
        processEndOfTurnEffects(currentTeam)

        vm.actionsTaken.clear()
        vm.isLeftTeamTurn = !vm.isLeftTeamTurn
        vm.battleTimer.reset()

        val nextTeam = if (vm.isLeftTeamTurn) vm.leftTeam else vm.rightTeam
        processStartOfTurnEffects(nextTeam)
        checkWinCondition()

        if (vm.winner != null) return

        // If whole team is stunned, skip turn (recursive check)
        val aliveMembers = nextTeam.entities.filter { it.isAlive }
        if (aliveMembers.isNotEmpty() && aliveMembers.all { it.effectManager.isStunned }) {
            delay(500) // Small delay for UX so it doesn't flash instantly
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
        val isRight = vm.rightTeam.entities.contains(entity)
        val isTurn = (isLeft && vm.isLeftTeamTurn) || (isRight && !vm.isLeftTeamTurn)
        return isTurn && 
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
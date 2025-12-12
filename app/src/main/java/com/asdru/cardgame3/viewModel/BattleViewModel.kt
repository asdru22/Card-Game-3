package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asdru.cardgame3.data.DragState
import com.asdru.cardgame3.data.UltimateDragState
import com.asdru.cardgame3.game.effect.Taunt
import com.asdru.cardgame3.game.effect.Vanish
import com.asdru.cardgame3.data.Team
import com.asdru.cardgame3.game.weather.WeatherEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class BattleViewModel(
  initialLeftTeam: TeamViewModel = TeamViewModel(Team("Blue", emptyList())),
  initialRightTeam: TeamViewModel = TeamViewModel(Team("Red", emptyList()))
) : ViewModel() {

  var leftTeam by mutableStateOf(initialLeftTeam)
  var rightTeam by mutableStateOf(initialRightTeam)

  var dragState by mutableStateOf<DragState?>(null)
    private set
  var ultimateDragState by mutableStateOf<UltimateDragState?>(null)
    private set

  var hoveredTarget by mutableStateOf<EntityViewModel?>(null)
    private set
  var showInfoDialog by mutableStateOf(false)
  var selectedEntity by mutableStateOf<EntityViewModel?>(null)

  var currentWeather by mutableStateOf<WeatherEvent?>(null)
    private set
  var showWeatherInfo by mutableStateOf(false)

  var isLeftTeamTurn by mutableStateOf(Random.nextBoolean())
    private set
  var isActionPlaying by mutableStateOf(false)
    private set
  var winner by mutableStateOf<String?>(null)
    private set

  var navigateToSelection by mutableStateOf(false)
    private set

  private val actionsTaken = mutableStateListOf<EntityViewModel>()
  val cardBounds = mutableStateMapOf<EntityViewModel, Rect>()

  fun onRestartClicked() {
    navigateToSelection = true
  }

  fun onNavigatedToSelection() {
    navigateToSelection = false
  }

  fun startGame(newLeftTeam: TeamViewModel, newRightTeam: TeamViewModel, weatherEnabled: Boolean) {
    leftTeam = newLeftTeam
    rightTeam = newRightTeam

    leftTeam.enemyTeam = rightTeam
    rightTeam.enemyTeam = leftTeam

    val allEntities = leftTeam.entities + rightTeam.entities
    allEntities.forEach { entity ->
      entity.onGetWeather = {
        currentWeather
      }

      entity.onGetAttackOffset = { target ->
        val sourceBounds = cardBounds[entity]
        val targetBounds = cardBounds[target]
        if (sourceBounds != null && targetBounds != null) {
          (targetBounds.center - sourceBounds.center) * 0.7f
        } else {
          null
        }
      }
    }

    currentWeather = if (weatherEnabled) {
      WeatherEvent.getRandomWeather()?.also { weather ->
        weather.onApply(this)
      }
    } else {
      null
    }

    isLeftTeamTurn = Random.nextBoolean()
    isActionPlaying = false
    winner = null
    actionsTaken.clear()
    cardBounds.clear()

    leftTeam.rage = 0f
    rightTeam.rage = 0f
  }

  fun increaseRage(team: TeamViewModel, amount: Float) {
    team.increaseRage(amount)
  }

  fun onUltimateDrag(change: Offset) {
    ultimateDragState?.let { current ->
      val newPos = current.current + change
      ultimateDragState = current.copy(current = newPos)

      hoveredTarget = cardBounds.entries.firstOrNull { (entity, rect) ->
        entity.isAlive &&
            !entity.isStunned
            && rect.contains(newPos)
            && current.team.entities.contains(entity)
      }?.key
    }
  }

  fun onUltimateDragEnd() {
    val state = ultimateDragState
    val target = hoveredTarget

    if (
      state != null &&
      target != null &&
      state.team.entities.contains(target)
    ) {
      executeUltimate(state.team, target)
    }

    ultimateDragState = null
    hoveredTarget = null
  }

  private fun executeUltimate(team: TeamViewModel, caster: EntityViewModel) {
    if (isActionPlaying || winner != null) return

    viewModelScope.launch {
      isActionPlaying = true
      team.rage = 0f
      val enemies = if (team == leftTeam) rightTeam else leftTeam
      val validTargets = enemies.aliveEntities

      if (validTargets.isNotEmpty()) {
        val randomEnemy = validTargets.random()
        caster.entity.ultimateAbility.effect(caster, randomEnemy)
      }

      checkWinCondition()
      isActionPlaying = false
    }
  }

  fun onDragStart(char: EntityViewModel, offset: Offset) {
    if (canEntityAct(char)) {
      val cardTopLeft = cardBounds[char]?.topLeft ?: Offset.Zero
      val globalStart = cardTopLeft + offset
      dragState = DragState(char, globalStart, globalStart)
    }
  }

  fun onDrag(change: Offset) {
    dragState?.let { currentDrag ->
      val newCurrent = currentDrag.current + change
      dragState = currentDrag.copy(current = newCurrent)

      hoveredTarget = cardBounds.entries.firstOrNull { (entity, rect) ->
        val isTargetAlive = entity.isAlive
        val isHovering = rect.contains(newCurrent)

        if (!isTargetAlive || !isHovering) return@firstOrNull false

        val isSourceLeft = leftTeam.entities.contains(currentDrag.source)
        val isTargetLeft = leftTeam.entities.contains(entity)
        val isEnemy = isSourceLeft != isTargetLeft

        if (isEnemy && entity.statusEffects.any { it is Vanish }) {
          return@firstOrNull false
        }

        val taunt = currentDrag.source.statusEffects.find { it is Taunt }
        if (taunt != null && taunt.source?.isAlive == true) {
          if (isEnemy && entity != taunt.source) {
            return@firstOrNull false
          }
        }

        true
      }?.key
    }
  }

  fun onDragEnd() {
    val state = dragState
    val target = hoveredTarget

    if (state != null && target != null && target.isAlive && canEntityAct(state.source)) {
      executeInteraction(state.source, target)
    }

    dragState = null
    hoveredTarget = null
  }

  fun onCardPositioned(entity: EntityViewModel, rect: Rect) {
    cardBounds[entity] = rect
  }

  fun onDoubleTap(entity: EntityViewModel) {
    selectedEntity = entity
    showInfoDialog = true
  }

  fun closeInfoDialog() {
    showInfoDialog = false
    selectedEntity = null
  }

  fun onPressStatus(entity: EntityViewModel, isPressed: Boolean) {

  }

  fun getHighlightColor(entity: EntityViewModel): Color {
    val draggingState = dragState
    val ultState = ultimateDragState

    if (draggingState != null && entity == hoveredTarget) {
      val sourceLeft = leftTeam.entities.contains(draggingState.source)
      val targetLeft = leftTeam.entities.contains(entity)
      return if (sourceLeft == targetLeft) Color.Green else Color.Red
    }

    if (ultState != null && entity == hoveredTarget) {
      if (ultState.team.entities.contains(entity)) return Color.Cyan
    }

    return Color.Transparent
  }

  private fun checkWinCondition() {
    val isLeftAlive = leftTeam.aliveEntities.isNotEmpty()
    val isRightAlive = rightTeam.aliveEntities.isNotEmpty()

    if (!isLeftAlive) {
      winner = rightTeam.name
    } else if (!isRightAlive) {
      winner = leftTeam.name
    }
  }

  private suspend fun processStartOfTurnEffects(team: TeamViewModel) {
    currentWeather?.onStartTurn(this)

    team.getAliveMembers().forEach { entity ->
      entity.traits.forEach { trait ->
        trait.onStartTurn(entity)
      }

      val activeEffects = entity.statusEffects.toList()
      activeEffects.forEach { effect ->
        effect.onStartTurn(entity)
        if (effect.tick()) {
          entity.removeEffect(effect)
        }
      }
    }
  }

  fun canEntityAct(entity: EntityViewModel): Boolean {
    if (winner != null) return false
    val isLeft = leftTeam.entities.contains(entity)
    val isRight = rightTeam.entities.contains(entity)
    val isTurn = (isLeft && isLeftTeamTurn) || (isRight && !isLeftTeamTurn)
    return isTurn &&
        !actionsTaken.contains(entity)
        && entity.isAlive
        && !isActionPlaying
        && !entity.isStunned
  }

  fun onUltimateDragStart(team: TeamViewModel, offset: Offset) {
    val isLeft = (team == leftTeam)
    if ((isLeft && isLeftTeamTurn) || (!isLeft && !isLeftTeamTurn)) {
      val hasNonStunnedMember = team.getAliveMembers().any { !it.isStunned }
      if (team.rage >= team.maxRage && !isActionPlaying && winner == null && hasNonStunnedMember) {
        ultimateDragState = UltimateDragState(team, offset, offset)
      }
    }
  }

  private fun executeInteraction(source: EntityViewModel, target: EntityViewModel) {
    if (isActionPlaying || winner != null) return

    viewModelScope.launch {
      isActionPlaying = true
      handleCardInteraction(source, target)

      val sourceTeam = if (leftTeam.entities.contains(source)) leftTeam else rightTeam

      increaseRage(sourceTeam, 10f)

      checkWinCondition()
      if (winner != null) {
        isActionPlaying = false
        return@launch
      }

      if (!actionsTaken.contains(source)) {
        actionsTaken.add(source)
      }

      val activeTeamEntities = if (isLeftTeamTurn) leftTeam.entities else rightTeam.entities

      val capableEntities = activeTeamEntities.filter { it.isAlive && !it.isStunned }

      if (actionsTaken.containsAll(capableEntities)) {
        advanceTurn()
      }

      isActionPlaying = false
    }
  }

  private suspend fun processEndOfTurnEffects(team: TeamViewModel) {
    currentWeather?.onEndTurn(this)

    team.entities.filter { it.isAlive }.forEach { entity ->
      entity.traits.forEach { trait ->
        trait.onEndTurn(entity)
      }
    }
  }

  private suspend fun advanceTurn() {
    val currentTeam = if (isLeftTeamTurn) leftTeam else rightTeam
    processEndOfTurnEffects(currentTeam)

    actionsTaken.clear()
    isLeftTeamTurn = !isLeftTeamTurn

    val nextTeam = if (isLeftTeamTurn) leftTeam else rightTeam
    processStartOfTurnEffects(nextTeam)
    checkWinCondition()

    if (winner != null) return

    val aliveMembers = nextTeam.entities.filter { it.isAlive }
    if (aliveMembers.isNotEmpty() && aliveMembers.all { it.isStunned }) {
      advanceTurn()
    }
  }

  private suspend fun handleCardInteraction(source: EntityViewModel, target: EntityViewModel) {
    val sourceLeft = leftTeam.entities.contains(source)
    val targetLeft = leftTeam.entities.contains(target)

    val onSameTeam = sourceLeft == targetLeft

    if (onSameTeam) {
      if (source.isStunned) return

      source.passiveAnimTrigger++
      delay(150)
      source.entity.passiveAbility.effect(source, target)
      delay(150)
    } else {
      source.entity.activeAbility.effect(source, target)
      delay(200)
    }
  }
}
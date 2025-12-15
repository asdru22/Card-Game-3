package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.asdru.cardgame3.data.Team
import com.asdru.cardgame3.data.UltimateDragState
import com.asdru.cardgame3.game.weather.WeatherEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class BattleViewModel(
  initialLeftTeam: TeamViewModel = TeamViewModel(Team("Blue", emptyList(), true)),
  initialRightTeam: TeamViewModel = TeamViewModel(Team("Red", emptyList(), false))
) : ViewModel() {

  // --- State Variables ---
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

  var showExitDialog by mutableStateOf(false)

  // --- Timer Delegate ---
  var currentTurnTimeSeconds by mutableIntStateOf(0)
  var maxTurnTimeSeconds by mutableIntStateOf(0)
  private val battleTimer = BattleTimer(
    scope = viewModelScope,
    onTick = { currentTurnTimeSeconds = it },
    onTimeout = { triggerTimeoutAction() }
  )

  // --- Navigation & Dialogs ---
  fun onExitClicked() {
    showExitDialog = true
  }

  fun onExitConfirmed() {
    battleTimer.stop()
    showExitDialog = false
    onRestartClicked()
  }

  fun onExitCancelled() {
    showExitDialog = false
  }

  fun onRestartClicked() {
    navigateToSelection = true
  }

  fun onNavigatedToSelection() {
    navigateToSelection = false
  }

  fun onDoubleTap(entity: EntityViewModel) {
    selectedEntity = entity
    showInfoDialog = true
  }

  fun closeInfoDialog() {
    showInfoDialog = false
    selectedEntity = null
  }

  // --- Game Initialization ---
  fun startGame(
    newLeftTeam: TeamViewModel,
    newRightTeam: TeamViewModel,
    weatherEnabled: Boolean,
    turnTimer: Int
  ) {
    leftTeam = newLeftTeam
    rightTeam = newRightTeam
    leftTeam.enemyTeam = rightTeam
    rightTeam.enemyTeam = leftTeam

    setupEntityCallbacks(leftTeam.entities + rightTeam.entities)

    currentWeather = if (weatherEnabled) {
      WeatherEvent.getRandomWeather()?.also { it.onApply(this) }
    } else null

    isLeftTeamTurn = Random.nextBoolean()
    isActionPlaying = false
    winner = null
    actionsTaken.clear()
    cardBounds.clear()
    leftTeam.rage = 0f
    rightTeam.rage = 0f
    maxTurnTimeSeconds = turnTimer

    battleTimer.init(turnTimer)
    battleTimer.start(checkPauseConditions = { isActionPlaying || showInfoDialog || showExitDialog || showWeatherInfo })
  }

  private fun setupEntityCallbacks(entities: List<EntityViewModel>) {
    entities.forEach { entity ->
      entity.onGetWeather = { currentWeather }
      entity.onGetAttackOffset = { target ->
        val sourceBounds = cardBounds[entity]
        val targetBounds = cardBounds[target]
        if (sourceBounds != null && targetBounds != null) {
          (targetBounds.center - sourceBounds.center) * 0.7f
        } else null
      }
    }
  }

  // --- Drag & Drop Logic ---
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

      // Delegate hit-testing logic
      hoveredTarget = BattleTargetingHelper.findValidTarget(
        dragState = currentDrag,
        dragPosition = newCurrent,
        cardBounds = cardBounds,
        leftTeamEntities = leftTeam.entities
      )
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

  // --- Ultimate Drag Logic ---
  fun onUltimateDragStart(team: TeamViewModel, offset: Offset) {
    val isLeft = (team == leftTeam)
    if ((isLeft && isLeftTeamTurn) || (!isLeft && !isLeftTeamTurn)) {
      val memberCanPerformUltimate =
        team.getAliveMembers().any { !it.effectManager.isStunned && !it.effectManager.isSilenced }
      if (team.rage >= team.maxRage &&
        !isActionPlaying &&
        winner == null &&
        memberCanPerformUltimate
      ) {
        ultimateDragState = UltimateDragState(team, offset, offset)
      }
    }
  }

  fun onUltimateDrag(change: Offset) {
    ultimateDragState?.let { current ->
      val newPos = current.current + change
      ultimateDragState = current.copy(current = newPos)
      hoveredTarget =
        BattleTargetingHelper.findUltimateTarget(newPos, current.team.entities, cardBounds)
    }
  }

  fun onUltimateDragEnd() {
    val state = ultimateDragState
    val target = hoveredTarget
    if (state != null && target != null && state.team.entities.contains(target)) {
      executeUltimate(state.team, target)
    }
    ultimateDragState = null
    hoveredTarget = null
  }

  // --- Action Execution ---
  private fun executeInteraction(source: EntityViewModel, target: EntityViewModel) {
    if (isActionPlaying || winner != null) return
    battleTimer.reset()

    viewModelScope.launch {
      isActionPlaying = true

      // Delegate animation logic
      val sourceLeft = leftTeam.entities.contains(source)
      val targetLeft = leftTeam.entities.contains(target)
      BattleCombatLogic.executeCardInteraction(
        source,
        target,
        isSameTeam = (sourceLeft == targetLeft)
      )

      val sourceTeam = if (sourceLeft) leftTeam else rightTeam
      sourceTeam.increaseRage(10f)

      checkWinCondition()
      if (winner == null) {
        if (!actionsTaken.contains(source)) actionsTaken.add(source)
        checkTurnAdvance()
      }
      isActionPlaying = false
    }
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

  private fun triggerTimeoutAction() {
    if (isActionPlaying || winner != null) return

    val currentTeam = if (isLeftTeamTurn) leftTeam else rightTeam
    val capableEntities = currentTeam.entities.filter {
      it.isAlive && !it.effectManager.isStunned && !actionsTaken.contains(it)
    }

    if (capableEntities.isEmpty()) return

    val source = capableEntities.random()
    val useActive = Random.nextBoolean()
    var target: EntityViewModel? =
      if (useActive) currentTeam.enemyTeam.getRandomTargetableEnemy() else currentTeam.getRandomAliveMember()

    if (target == null) {
      target =
        if (useActive) currentTeam.entities.randomOrNull() else currentTeam.enemyTeam.entities.randomOrNull()
    }

    if (target != null && target.isAlive) {
      dragState = null
      hoveredTarget = null
      executeInteraction(source, target)
    }
  }

  // --- Turn Management ---
  private suspend fun checkTurnAdvance() {
    val activeTeamEntities = if (isLeftTeamTurn) leftTeam.entities else rightTeam.entities
    val capableEntities = activeTeamEntities.filter { it.isAlive && !it.effectManager.isStunned }
    if (actionsTaken.containsAll(capableEntities)) {
      advanceTurn()
    }
  }

  private suspend fun advanceTurn() {
    val currentTeam = if (isLeftTeamTurn) leftTeam else rightTeam
    processEndOfTurnEffects(currentTeam)

    actionsTaken.clear()
    isLeftTeamTurn = !isLeftTeamTurn
    battleTimer.reset()

    val nextTeam = if (isLeftTeamTurn) leftTeam else rightTeam
    processStartOfTurnEffects(nextTeam)
    checkWinCondition()

    if (winner != null) return

    // If whole team is stunned, skip turn
    val aliveMembers = nextTeam.entities.filter { it.isAlive }
    if (aliveMembers.isNotEmpty() && aliveMembers.all { it.effectManager.isStunned }) {
      advanceTurn()
    }
  }

  private suspend fun processStartOfTurnEffects(team: TeamViewModel) {
    currentWeather?.onStartTurn(this)
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
    currentWeather?.onEndTurn(this)
    team.getAliveMembers().forEach { entity ->
      entity.traits.forEach { it.onEndTurn(entity) }
      val activeEffects = entity.effectManager.effects.toList()
      activeEffects.forEach { effect ->
        effect.onEndTurn(entity)
      }
    }
  }

  // --- Helpers ---
  private suspend fun checkWinCondition() {
    val isLeftAlive = leftTeam.aliveEntities.isNotEmpty()
    val isRightAlive = rightTeam.aliveEntities.isNotEmpty()

    if (!isLeftAlive || !isRightAlive) {
      delay(1000)

      winner = if (!isLeftAlive) rightTeam.name
      else leftTeam.name
    }
  }

  fun canEntityAct(entity: EntityViewModel): Boolean {
    if (winner != null) return false
    val isLeft = leftTeam.entities.contains(entity)
    val isRight = rightTeam.entities.contains(entity)
    val isTurn = (isLeft && isLeftTeamTurn) || (isRight && !isLeftTeamTurn)
    return isTurn && !actionsTaken.contains(entity) && entity.isAlive && !isActionPlaying && !entity.effectManager.isStunned
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
}
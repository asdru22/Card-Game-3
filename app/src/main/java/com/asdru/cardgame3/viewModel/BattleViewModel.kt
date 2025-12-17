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
import com.asdru.cardgame3.logic.BattleGameLogic
import com.asdru.cardgame3.logic.BattleInputHandler
import com.asdru.cardgame3.logic.BattleTimer

class BattleViewModel(
  initialLeftTeam: TeamViewModel = TeamViewModel(Team("Blue", emptyList(), true)),
  initialRightTeam: TeamViewModel = TeamViewModel(Team("Red", emptyList(), false))
) : ViewModel() {

  // --- Components ---
  // Handles Drag & Drop interactions
  private val inputHandler = BattleInputHandler(this)
  // Handles Combat, Turns, and Win Conditions
  val gameLogic = BattleGameLogic(this)

  // --- State Variables ---
  var leftTeam by mutableStateOf(initialLeftTeam)
  var rightTeam by mutableStateOf(initialRightTeam)

  var dragState by mutableStateOf<DragState?>(null)
    internal set
  var ultimateDragState by mutableStateOf<UltimateDragState?>(null)
    internal set

  var hoveredTarget by mutableStateOf<EntityViewModel?>(null)
    internal set
  var showInfoDialog by mutableStateOf(false)
  var selectedEntity by mutableStateOf<EntityViewModel?>(null)

  var currentWeather by mutableStateOf<WeatherEvent?>(null)
    internal set
  var showWeatherInfo by mutableStateOf(false)

  var isLeftTeamTurn by mutableStateOf(true)
    internal set
  var isActionPlaying by mutableStateOf(false)
    internal set
  var winner by mutableStateOf<String?>(null)
    internal set

  var navigateToSelection by mutableStateOf(false)
    internal set

  // Internal lists for logic tracking
  internal val actionsTaken = mutableStateListOf<EntityViewModel>()
  internal val cardBounds = mutableStateMapOf<EntityViewModel, Rect>()

  var showExitDialog by mutableStateOf(false)

  // --- Timer Delegate ---
  var currentTurnTimeSeconds by mutableIntStateOf(0)
  var maxTurnTimeSeconds by mutableIntStateOf(0)
  internal val battleTimer = BattleTimer(
    scope = viewModelScope,
    onTick = { currentTurnTimeSeconds = it },
    onTimeout = { gameLogic.triggerTimeoutAction() }
  )

  // --- Initialization ---
  fun startGame(
    newLeftTeam: TeamViewModel,
    newRightTeam: TeamViewModel,
    weatherEnabled: Boolean,
    turnTimer: Int
  ) {
    gameLogic.startGame(newLeftTeam, newRightTeam, weatherEnabled, turnTimer)
  }

  // --- UI/Input Events (Delegated) ---

  fun onDragStart(char: EntityViewModel, offset: Offset) {
    inputHandler.onDragStart(char, offset)
  }

  fun onDrag(change: Offset) {
    inputHandler.onDrag(change)
  }

  fun onDragEnd() {
    inputHandler.onDragEnd()
  }

  fun onCardPositioned(entity: EntityViewModel, rect: Rect) {
    inputHandler.onCardPositioned(entity, rect)
  }

  fun onUltimateDragStart(team: TeamViewModel, offset: Offset) {
    inputHandler.onUltimateDragStart(team, offset)
  }

  fun onUltimateDrag(change: Offset) {
    inputHandler.onUltimateDrag(change)
  }

  fun onUltimateDragEnd() {
    inputHandler.onUltimateDragEnd()
  }

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

  // --- UI Helpers ---
  fun getHighlightColor(entity: EntityViewModel): Color {
    return inputHandler.getHighlightColor(entity)
  }
}
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
import com.asdru.cardgame3.data.ShopDragState
import com.asdru.cardgame3.data.Team
import com.asdru.cardgame3.data.TotemDragState
import com.asdru.cardgame3.data.UltimateDragState
import com.asdru.cardgame3.game.item.ShopItem
import com.asdru.cardgame3.game.weather.WeatherEvent
import com.asdru.cardgame3.logic.BattleGameLogic
import com.asdru.cardgame3.logic.BattleTimer
import com.asdru.cardgame3.logic.inputHandler.CardInputHandler
import com.asdru.cardgame3.logic.inputHandler.ShopInputHandler
import com.asdru.cardgame3.logic.inputHandler.TotemInputHandler
import com.asdru.cardgame3.logic.inputHandler.UltimateInputHandler
import kotlinx.coroutines.launch

class BattleViewModel(
  initialLeftTeam: TeamViewModel = TeamViewModel(Team("Blue", emptyList(), true)),
  initialRightTeam: TeamViewModel = TeamViewModel(Team("Red", emptyList(), false)),
  var playerRepository: com.asdru.cardgame3.data.repository.PlayerRepository? = null,
  var characterStatsRepository: com.asdru.cardgame3.data.repository.CharacterStatsRepository? = null
) : ViewModel() {

  var resourceResolver: ((Int) -> String)? = null

  // --- Components ---

  private val cardInputHandler = CardInputHandler(this)
  private val ultimateInputHandler = UltimateInputHandler(this)
  private val shopInputHandler = ShopInputHandler(this)
  private val totemInputHandler = TotemInputHandler(this)

  val gameLogic = BattleGameLogic(this)

  // --- State Variables ---
  var leftTeam by mutableStateOf(initialLeftTeam)
  var rightTeam by mutableStateOf(initialRightTeam)

  var dragState by mutableStateOf<DragState?>(null)
    internal set
  var ultimateDragState by mutableStateOf<UltimateDragState?>(null)
    internal set
  var totemDragState by mutableStateOf<TotemDragState?>(null)
    internal set

  var hoveredTarget by mutableStateOf<EntityViewModel?>(null)
    internal set
  var showInfoDialog by mutableStateOf(false)
  var selectedEntity by mutableStateOf<EntityViewModel?>(null)

  var currentWeather by mutableStateOf<WeatherEvent?>(null)
    internal set
  var weatherActionCounter by mutableIntStateOf(0)
    internal set
  var weatherChangeThreshold by mutableIntStateOf(0)
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

  var roundCount by mutableIntStateOf(1)
    internal set
  var startingTeamIsLeft by mutableStateOf(true)
    internal set

  // Internal lists for logic tracking
  internal val actionsTaken = mutableStateListOf<EntityViewModel>()
  internal val totemActionsTaken = mutableStateListOf<TotemViewModel>()
  internal val cardBounds = mutableStateMapOf<EntityViewModel, Rect>()
  internal val totemBounds = mutableStateMapOf<TotemViewModel, Rect>()

  var showExitDialog by mutableStateOf(false)

  var shopDragState by mutableStateOf<ShopDragState?>(null)
    internal set

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

    // Increment pick rates
    viewModelScope.launch {
      val resolver = resourceResolver
      val statsRepo = characterStatsRepository
      if (resolver != null && statsRepo != null) {
        val allCharacters = newLeftTeam.team.entities + newRightTeam.team.entities
        allCharacters.forEach { charVM ->
          val nameKey = resolver(charVM.name)
          statsRepo.incrementPickCount(nameKey)
        }
      }
    }
  }

  // --- Card Events

  fun onDragStart(char: EntityViewModel, offset: Offset) {
    cardInputHandler.onDragStart(char, offset)
  }

  fun onDrag(change: Offset) {
    cardInputHandler.onDrag(change)
  }

  fun onDragEnd() {
    cardInputHandler.onDragEnd()
  }

  fun onCardPositioned(entity: EntityViewModel, rect: Rect) {
    cardInputHandler.onCardPositioned(entity, rect)
  }

  // --- Ultimate Events

  fun onUltimateDragStart(team: TeamViewModel, offset: Offset) {
    ultimateInputHandler.onUltimateDragStart(team, offset)
  }

  fun onUltimateDrag(change: Offset) {
    ultimateInputHandler.onUltimateDrag(change)
  }

  fun onUltimateDragEnd() {
    ultimateInputHandler.onUltimateDragEnd()
  }

  // --- Shop Events (Delegated to ShopInputHandler) ---
  fun onShopDragStart(item: ShopItem, isLeftTeam: Boolean, offset: Offset) {
    shopInputHandler.onShopDragStart(item, isLeftTeam, offset)
  }

  fun onShopDrag(change: Offset) {
    shopInputHandler.onShopDrag(change)
  }

  fun onShopDragEnd() {
    shopInputHandler.onShopDragEnd()
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
    leftTeam.shop.reset()
    rightTeam.shop.reset()
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

  // --- Totem Dialog ---
  var showTotemInfoDialog by mutableStateOf(false)
  var selectedTotem by mutableStateOf<TotemViewModel?>(null)

  fun onTotemDoubleTap(totem: TotemViewModel) {
    selectedTotem = totem
    showTotemInfoDialog = true
  }

  fun closeTotemInfoDialog() {
    showTotemInfoDialog = false
    selectedTotem = null
  }

  fun onTotemDragStart(totem: TotemViewModel, offset: Offset) {
    totemInputHandler.onTotemDragStart(totem, offset)
  }

  fun onTotemDrag(change: Offset) {
    totemInputHandler.onTotemDrag(change)
  }

  fun onTotemDragEnd() {
    totemInputHandler.onTotemDragEnd()
  }

  fun getHighlightColor(entity: EntityViewModel): Color {
    return cardInputHandler.getHighlightColor(entity)
      ?: ultimateInputHandler.getHighlightColor(entity)
      ?: shopInputHandler.getHighlightColor(entity)
      ?: totemInputHandler.getHighlightColor(entity)
      ?: Color.Transparent
  }
}
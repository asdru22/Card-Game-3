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
import com.asdru.cardgame3.game.state.GameAction
import com.asdru.cardgame3.game.state.GameEvent
import com.asdru.cardgame3.game.state.GameState
import com.asdru.cardgame3.network.GameClient
import com.asdru.cardgame3.network.LocalGameClient
import com.asdru.cardgame3.repository.GameMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class BattleViewModelRefactored(
  private val playerId: String = "player_${System.currentTimeMillis()}",
  private val gameMode: GameMode = GameMode.LOCAL,
) : ViewModel() {

  private val gameClient: GameClient by lazy {
    when (gameMode) {
      GameMode.LOCAL -> LocalGameClient(viewModelScope)
      GameMode.ONLINE_RANDOM,
      GameMode.ONLINE_PRIVATE -> LocalGameClient(
        viewModelScope // TEMP!
      )
    }
  }

  // UI State
  lateinit var leftTeam: TeamViewModel
    private set
  lateinit var rightTeam: TeamViewModel
    private set

  var dragState by mutableStateOf<DragState?>(null)
    private set
  var ultimateDragState by mutableStateOf<UltimateDragState?>(null)
    private set

  var hoveredTarget by mutableStateOf<EntityViewModel?>(null)
    private set
  var showInfoDialog by mutableStateOf(false)
  var selectedEntity by mutableStateOf<EntityViewModel?>(null)

  var isLeftTeamTurn by mutableStateOf(false)
    private set
  var isActionPlaying by mutableStateOf(false)
    private set
  var winner by mutableStateOf<String?>(null)
    private set

  var navigateToSelection by mutableStateOf(false)
    private set

  private val actionsTaken = mutableStateListOf<EntityViewModel>()
  val cardBounds = mutableStateMapOf<EntityViewModel, Rect>()

  // Entity ID mapping for syncing with game state
  private val entityIdMap = mutableMapOf<String, EntityViewModel>()

  init {
    observeGameState()
    observeGameEvents()
  }

  private fun observeGameState() {
    gameClient.observeGameState()
      .onEach { state ->
        syncWithGameState(state)
      }
      .launchIn(viewModelScope)
  }

  private fun observeGameEvents() {
    gameClient.observeGameEvents()
      .onEach { event ->
        handleGameEvent(event)
      }
      .launchIn(viewModelScope)
  }

  fun startGame(newLeftTeam: TeamViewModel, newRightTeam: TeamViewModel) {
    viewModelScope.launch {
      leftTeam = newLeftTeam
      rightTeam = newRightTeam

      newLeftTeam.enemyTeam = newRightTeam
      newRightTeam.enemyTeam = newLeftTeam

      // Set up ID mapping
      newLeftTeam.entities.forEach { entity ->
        val id = generateEntityId(entity)
        entityIdMap[id] = entity
      }
      newRightTeam.entities.forEach { entity ->
        val id = generateEntityId(entity)
        entityIdMap[id] = entity
      }

      // Setup attack offset callback
      val allEntities = newLeftTeam.entities + newRightTeam.entities
      allEntities.forEach { entity ->
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

      // Initialize game via client
      val result = gameClient.connect()
      if (result.isSuccess) {
        // Start game through client
        // For local game, this sets up the GameEngine
      }
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
        entity.isAlive && rect.contains(newCurrent)
      }?.key
    }
  }

  fun onDragEnd() {
    val state = dragState
    val target = hoveredTarget

    if (state != null && target != null && target.isAlive && canEntityAct(state.source)) {
      // Execute interaction locally
      viewModelScope.launch {
        executeInteraction(state.source, target)
      }
    }

    dragState = null
    hoveredTarget = null
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

  private fun checkWinCondition() {
    val isLeftAlive = leftTeam.aliveEntities.isNotEmpty()
    val isRightAlive = rightTeam.aliveEntities.isNotEmpty()

    if (!isLeftAlive) {
      winner = rightTeam.name
    } else if (!isRightAlive) {
      winner = leftTeam.name
    }
  }

  fun increaseRage(team: TeamViewModel?, amount: Float) {
    team?.increaseRage(amount)
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

  private suspend fun processEndOfTurnEffects(team: TeamViewModel?) {
    team?.entities?.filter { it.isAlive }?.forEach { entity ->
      entity.traits.forEach { trait ->
        trait.onEndTurn(entity)
      }
    }
  }

  private suspend fun processStartOfTurnEffects(team: TeamViewModel?) {
    team?.entities?.filter { it.isAlive }?.forEach { entity ->
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

  fun onUltimateDragStart(team: TeamViewModel, offset: Offset) {
    val isLeft = (team == leftTeam)
    if ((isLeft && isLeftTeamTurn) || (!isLeft && !isLeftTeamTurn)) {
      val hasNonStunnedMember = team.entities.any { it.isAlive && !it.isStunned }
      if (team.rage >= team.maxRage && !isActionPlaying && winner == null && hasNonStunnedMember) {
        ultimateDragState = UltimateDragState(team, offset, offset)
      }
    }
  }

  fun onUltimateDrag(change: Offset) {
    ultimateDragState?.let { current ->
      val newPos = current.current + change
      ultimateDragState = current.copy(current = newPos)

      hoveredTarget = cardBounds.entries.firstOrNull { (entity, rect) ->
        entity.isAlive && !entity.isStunned
            && rect.contains(newPos)
            && current.team.entities.contains(entity)
      }?.key
    }
  }

  fun onUltimateDragEnd() {
    val state = ultimateDragState
    val target = hoveredTarget

    if (state != null && target != null && state.team.entities.contains(target)) {
      // Execute ultimate locally
      executeUltimate(state.team, target)
    }

    ultimateDragState = null
    hoveredTarget = null
  }

  // Add this method
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

  fun onCardPositioned(entity: EntityViewModel, rect: Rect) {
    cardBounds[entity] = rect
  }

  fun onDoubleTap(entity: EntityViewModel) {
    println("Double tapped ${entity.name}")
  }

  fun onPressStatus(entity: EntityViewModel, isPressed: Boolean) {
    if (isPressed) {
      selectedEntity = entity
      showInfoDialog = true
    } else {
      showInfoDialog = false
      selectedEntity = null
    }
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

  fun canEntityAct(entity: EntityViewModel): Boolean {
    if (winner != null) return false
    val isLeft = leftTeam.entities.contains(entity)
    val isRight = rightTeam.entities.contains(entity)
    val isTurn = (isLeft && isLeftTeamTurn) || (isRight && !isLeftTeamTurn)
    return isTurn && !actionsTaken.contains(entity) && entity.isAlive
        && !isActionPlaying && !entity.isStunned
  }

  fun onRestartClicked() {
    viewModelScope.launch {
      val action = GameAction.RestartGame(playerId)
      gameClient.sendAction(action)
      navigateToSelection = true
    }
  }

  fun onNavigatedToSelection() {
    navigateToSelection = false
  }

  // Sync methods
  private fun syncWithGameState(state: GameState) {
    isLeftTeamTurn = state.isLeftTeamTurn
    winner = state.winner

    // Update rage
    leftTeam.rage = state.leftTeam.rage
    rightTeam.rage = state.rightTeam.rage

    // Sync entity states
    state.leftTeam.entities.forEach { entityState ->
      entityIdMap[entityState.entityId]?.let { vm ->
        vm.health = entityState.health
        vm.damage = entityState.damage
      }
    }
    state.rightTeam.entities.forEach { entityState ->
      entityIdMap[entityState.entityId]?.let { vm ->
        vm.health = entityState.health
        vm.damage = entityState.damage
      }
    }
  }

  private fun handleGameEvent(event: GameEvent) {
    when (event) {
      is GameEvent.DamageDealt -> {
        // Trigger animations or UI feedback
      }

      is GameEvent.GameEnded -> {
        winner = event.winnerId
      }

      is GameEvent.TurnChanged -> {
        actionsTaken.clear()
      }

      else -> {
        // Handle other events
      }
    }
  }

  private fun generateEntityId(entity: EntityViewModel): String {
    return "${entity.entity::class.simpleName}_${System.currentTimeMillis()}"
  }

  private fun getEntityId(entity: EntityViewModel): String {
    return entityIdMap.entries.find { it.value == entity }?.key
      ?: generateEntityId(entity)
  }
}
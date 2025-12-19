package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.asdru.cardgame3.data.Entity

// Placeholder classes
data class DragState(val start: Offset, val current: Offset, val entity: EntityViewModel)
data class ShopDragState(val current: Offset, val item: Entity) // Placeholder item type
data class UltimateDragState(val current: Offset, val team: TeamViewModel)
data class Weather(val iconRes: Int, val color: Color)
class GameLogic {
    fun canEntityAct(entity: EntityViewModel): Boolean = true
}

class BattleViewModel(
    val leftTeam: TeamViewModel,
    val rightTeam: TeamViewModel
) : ViewModel() {

    var dragState by mutableStateOf<DragState?>(null)
    var shopDragState by mutableStateOf<ShopDragState?>(null)
    var ultimateDragState by mutableStateOf<UltimateDragState?>(null)
    
    var hoveredTarget by mutableStateOf<EntityViewModel?>(null)
    val cardBounds = mutableStateMapOf<EntityViewModel, Rect>()
    
    var currentTurnTimeSeconds by mutableIntStateOf(60)
    val maxTurnTimeSeconds = 60
    var winner by mutableStateOf<TeamViewModel?>(null)
    
    var showWeatherInfo by mutableStateOf(false)
    var currentWeather by mutableStateOf<Weather?>(null)
    
    var showInfoDialog by mutableStateOf(false)
    var selectedEntity by mutableStateOf<EntityViewModel?>(null)
    
    var showExitDialog by mutableStateOf(false)
    
    var isLeftTeamTurn by mutableStateOf(true)
    
    val gameLogic = GameLogic()

    fun onExitClicked() { showExitDialog = true }
    fun onExitConfirmed() { /* TODO */ }
    fun onExitCancelled() { showExitDialog = false }
    
    fun closeInfoDialog() { 
        showInfoDialog = false 
        selectedEntity = null
    }

    // Ultimate Drag
    fun onUltimateDragStart(team: TeamViewModel, offset: Offset) {
        ultimateDragState = UltimateDragState(offset, team)
    }
    fun onUltimateDrag(offset: Offset) {
        ultimateDragState = ultimateDragState?.copy(current = ultimateDragState!!.current + offset)
    }
    fun onUltimateDragEnd() {
        ultimateDragState = null
    }

    // Shop Drag
    fun onShopDragStart(item: Entity, isLeft: Boolean, offset: Offset) {
         shopDragState = ShopDragState(offset, item)
    }
    fun onShopDrag(offset: Offset) {
         shopDragState = shopDragState?.copy(current = shopDragState!!.current + offset)
    }
    fun onShopDragEnd() {
        shopDragState = null
    }

    // Card Drag
    fun onDragStart(entity: EntityViewModel, offset: Offset) {
        dragState = DragState(offset, offset, entity)
    }
    fun onDrag(offset: Offset) {
        dragState = dragState?.copy(current = dragState!!.current + offset)
    }
    fun onDragEnd() {
        dragState = null
    }
    
    fun onCardPositioned(entity: EntityViewModel, rect: Rect) {
        cardBounds[entity] = rect
    }
    
    fun onDoubleTap(entity: EntityViewModel) {
        selectedEntity = entity
        showInfoDialog = true
    }
    
    fun getHighlightColor(entity: EntityViewModel): Color {
        return Color.Transparent
    }
}

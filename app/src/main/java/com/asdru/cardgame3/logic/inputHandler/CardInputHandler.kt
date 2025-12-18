package com.asdru.cardgame3.logic.inputHandler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.data.DragState
import com.asdru.cardgame3.helper.BattleTargetingHelper
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.EntityViewModel

class CardInputHandler(private val vm: BattleViewModel) {

  fun onDragStart(char: EntityViewModel, offset: Offset) {
    if (vm.gameLogic.canEntityAct(char)) {
      val cardTopLeft = vm.cardBounds[char]?.topLeft ?: Offset.Zero
      val globalStart = cardTopLeft + offset
      vm.dragState = DragState(char, globalStart, globalStart)
    }
  }

  fun onDrag(change: Offset) {
    vm.dragState?.let { currentDrag ->
      val newCurrent = currentDrag.current + change
      vm.dragState = currentDrag.copy(current = newCurrent)

      vm.hoveredTarget = BattleTargetingHelper.findValidTarget(
        dragState = currentDrag,
        dragPosition = newCurrent,
        cardBounds = vm.cardBounds,
        leftTeamEntities = vm.leftTeam.entities
      )
    }
  }

  fun onDragEnd() {
    val state = vm.dragState
    val target = vm.hoveredTarget

    if (state != null && target != null && target.isAlive && vm.gameLogic.canEntityAct(state.source)) {
      vm.gameLogic.executeInteraction(state.source, target)
    }
    vm.dragState = null
    vm.hoveredTarget = null
  }

  fun onCardPositioned(entity: EntityViewModel, rect: Rect) {
    vm.cardBounds[entity] = rect
  }

  fun getHighlightColor(entity: EntityViewModel): Color? {
    val draggingState = vm.dragState
    val target = vm.hoveredTarget

    if (draggingState != null && entity == target) {
      val sourceLeft = vm.leftTeam.entities.contains(draggingState.source)
      val targetLeft = vm.leftTeam.entities.contains(entity)
      return if (sourceLeft == targetLeft) Color.Green else Color.Red
    }
    return null
  }
}
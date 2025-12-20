package com.asdru.cardgame3.logic.inputHandler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.data.TotemDragState
import com.asdru.cardgame3.game.effect.Vanish
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.TotemViewModel

class TotemInputHandler(private val vm: BattleViewModel) {

  fun onTotemDragStart(totem: TotemViewModel, offset: Offset) {
    if (vm.gameLogic.canTotemAct(totem)) {
      val totemTopLeft = vm.totemBounds[totem]?.topLeft ?: Offset.Zero
      val globalStart = totemTopLeft + offset
      vm.totemDragState = TotemDragState(totem, globalStart, globalStart)
    }
  }

  fun onTotemDrag(change: Offset) {
    vm.totemDragState?.let { currentDrag ->
      val newCurrent = currentDrag.current + change
      vm.totemDragState = currentDrag.copy(current = newCurrent)

      // Find target
      vm.hoveredTarget = findTotemTarget(newCurrent)
    }
  }

  fun onTotemDragEnd() {
    val state = vm.totemDragState
    val target = vm.hoveredTarget

    if (state != null && target != null && target.isAlive && vm.gameLogic.canTotemAct(state.source)) {
      vm.gameLogic.executeTotemInteraction(state.source, target)
    }
    vm.totemDragState = null
    vm.hoveredTarget = null
  }

  private fun findTotemTarget(dragPosition: Offset): EntityViewModel? {
    return vm.cardBounds.entries.firstOrNull { (entity, rect) ->
      val isTargetAlive = entity.isAlive
      val isHovering = rect.contains(dragPosition)

      if (!isTargetAlive || !isHovering) return@firstOrNull false

      // Check for Vanish on enemies
      val isSourceLeft = vm.leftTeam.totem == vm.totemDragState?.source
      val isTargetLeft = vm.leftTeam.entities.contains(entity)
      val isEnemy = isSourceLeft != isTargetLeft

      if (isEnemy && entity.effectManager.effects.any { it is Vanish }) {
        return@firstOrNull false
      }
      true
    }?.key
  }

  fun getHighlightColor(entity: EntityViewModel): Color? {
    val draggingState = vm.totemDragState
    val target = vm.hoveredTarget

    if (draggingState != null && entity == target) {
      val isSourceLeft = vm.leftTeam.totem == draggingState.source
      val isTargetLeft = vm.leftTeam.entities.contains(entity)
      return if (isSourceLeft == isTargetLeft) Color.Green else Color.Red
    }
    return null
  }
}

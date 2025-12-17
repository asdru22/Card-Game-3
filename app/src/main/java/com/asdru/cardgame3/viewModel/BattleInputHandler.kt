package com.asdru.cardgame3.viewModel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.data.DragState
import com.asdru.cardgame3.data.UltimateDragState

class BattleInputHandler(private val vm: BattleViewModel) {

    // --- Standard Card Drag ---

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

            // Delegate hit-testing logic
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

        // Execute action via GameLogic
        if (state != null && target != null && target.isAlive && vm.gameLogic.canEntityAct(state.source)) {
            vm.gameLogic.executeInteraction(state.source, target)
        }
        vm.dragState = null
        vm.hoveredTarget = null
    }

    fun onCardPositioned(entity: EntityViewModel, rect: Rect) {
        vm.cardBounds[entity] = rect
    }

    // --- Ultimate Ability Drag ---

    fun onUltimateDragStart(team: TeamViewModel, offset: Offset) {
        val isLeft = (team == vm.leftTeam)
        // Check if it is this team's turn
        if ((isLeft && vm.isLeftTeamTurn) || (!isLeft && !vm.isLeftTeamTurn)) {
            
            val memberCanPerformUltimate = team.getAliveMembers().any { 
                !it.effectManager.isStunned && !it.effectManager.isSilenced 
            }
            
            if (team.rage >= team.maxRage &&
                !vm.isActionPlaying &&
                vm.winner == null &&
                memberCanPerformUltimate
            ) {
                vm.ultimateDragState = UltimateDragState(team, offset, offset)
            }
        }
    }

    fun onUltimateDrag(change: Offset) {
        vm.ultimateDragState?.let { current ->
            val newPos = current.current + change
            vm.ultimateDragState = current.copy(current = newPos)
            vm.hoveredTarget = BattleTargetingHelper.findUltimateTarget(
                newPos, 
                current.team.entities, 
                vm.cardBounds
            )
        }
    }

    fun onUltimateDragEnd() {
        val state = vm.ultimateDragState
        val target = vm.hoveredTarget
        if (state != null && target != null && state.team.entities.contains(target)) {
            vm.gameLogic.executeUltimate(state.team, target)
        }
        vm.ultimateDragState = null
        vm.hoveredTarget = null
    }

    // --- Visual Helpers ---

    fun getHighlightColor(entity: EntityViewModel): Color {
        val draggingState = vm.dragState
        val ultState = vm.ultimateDragState
        val target = vm.hoveredTarget

        if (draggingState != null && entity == target) {
            val sourceLeft = vm.leftTeam.entities.contains(draggingState.source)
            val targetLeft = vm.leftTeam.entities.contains(entity)
            return if (sourceLeft == targetLeft) Color.Green else Color.Red
        }
        if (ultState != null && entity == target) {
            if (ultState.team.entities.contains(entity)) return Color.Cyan
        }
        return Color.Transparent
    }
}
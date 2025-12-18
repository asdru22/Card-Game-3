package com.asdru.cardgame3.logic.inputHandler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.data.UltimateDragState
import com.asdru.cardgame3.helper.BattleTargetingHelper
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.TeamViewModel

class UltimateInputHandler(private val vm: BattleViewModel) {

  fun onUltimateDragStart(team: TeamViewModel, offset: Offset) {
    val isLeft = (team == vm.leftTeam)

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

  fun getHighlightColor(entity: EntityViewModel): Color? {
    val ultState = vm.ultimateDragState
    val target = vm.hoveredTarget

    if (ultState != null && entity == target) {
      if (ultState.team.entities.contains(entity)) return Color.Cyan
    }
    return null
  }
}
package com.asdru.cardgame3.logic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.data.DragState
import com.asdru.cardgame3.data.ShopDragState
import com.asdru.cardgame3.data.ShopItem
import com.asdru.cardgame3.data.UltimateDragState
import com.asdru.cardgame3.helper.BattleTargetingHelper
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.TeamViewModel

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

  // --- Ultimate Ability Drag ---

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

  // --- Visual Helpers ---

  fun getHighlightColor(entity: EntityViewModel): Color {
    val draggingState = vm.dragState
    val ultState = vm.ultimateDragState
    val target = vm.hoveredTarget
    val shopState = vm.shopDragState

    if (draggingState != null && entity == target) {
      val sourceLeft = vm.leftTeam.entities.contains(draggingState.source)
      val targetLeft = vm.leftTeam.entities.contains(entity)
      return if (sourceLeft == targetLeft) Color.Green else Color.Red
    }
    if (ultState != null && entity == target) {
      if (ultState.team.entities.contains(entity)) return Color.Cyan
    }

    if (shopState != null && entity == target) {
      // Must be friendly to highlight
      val isFriendly = (shopState.teamIsLeft && vm.leftTeam.entities.contains(entity)) ||
          (!shopState.teamIsLeft && vm.rightTeam.entities.contains(entity))
      if (isFriendly) return Color.Yellow
    }
    return Color.Transparent
  }

  fun onShopDragStart(item: ShopItem, isLeftTeam: Boolean, offset: Offset) {
    val team = if (isLeftTeam) vm.leftTeam else vm.rightTeam

    // 1. Check turns (Optional: Remove if you want to allow shopping during enemy turn)
    val isMyTurn = (isLeftTeam && vm.isLeftTeamTurn) || (!isLeftTeam && !vm.isLeftTeamTurn)

    // 2. Check Coins
    if (isMyTurn && team.coins >= item.cost && vm.winner == null && !vm.isActionPlaying) {
      vm.shopDragState = ShopDragState(
        item = item,
        teamIsLeft = isLeftTeam,
        start = offset,
        current = offset
      )
    }
  }

  fun onShopDrag(change: Offset) {
    vm.shopDragState?.let { current ->
      val newPos = current.current + change
      vm.shopDragState = current.copy(current = newPos)

      // Only highlight FRIENDLY units
      val friendlyEntities = if (current.teamIsLeft) vm.leftTeam.entities else vm.rightTeam.entities

      vm.hoveredTarget = BattleTargetingHelper.findUltimateTarget(
        newPos,
        friendlyEntities, // Restrict to own team
        vm.cardBounds
      )
    }
  }

  fun onShopDragEnd() {
    val state = vm.shopDragState
    val target = vm.hoveredTarget

    if (state != null && target != null) {
      val team = if (state.teamIsLeft) vm.leftTeam else vm.rightTeam

      // Double check target belongs to team and is alive
      if (target.team == team && target.isAlive) {
        // Apply Effect
        state.item.onApply(target)
        // Deduct Coins
        team.coins -= state.item.cost
        team.totalCoinsSpent += state.item.cost
      }
    }

    vm.shopDragState = null
    vm.hoveredTarget = null
  }

}
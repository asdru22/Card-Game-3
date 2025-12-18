package com.asdru.cardgame3.logic.inputHandler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.asdru.cardgame3.data.ShopDragState
import com.asdru.cardgame3.game.item.ShopItem
import com.asdru.cardgame3.helper.BattleTargetingHelper
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlinx.coroutines.launch

class ShopInputHandler(private val vm: BattleViewModel) {

  fun onShopDragStart(item: ShopItem, isLeftTeam: Boolean, offset: Offset) {
    val team = if (isLeftTeam) vm.leftTeam else vm.rightTeam
    val isMyTurn = (isLeftTeam && vm.isLeftTeamTurn) || (!isLeftTeam && !vm.isLeftTeamTurn)

    if (isMyTurn &&
      team.shop.canAfford(item.cost) &&
      vm.winner == null &&
      !vm.isActionPlaying
    ) {
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

      val friendlyEntities = if (current.teamIsLeft) vm.leftTeam.entities else vm.rightTeam.entities

      vm.hoveredTarget = BattleTargetingHelper.findUltimateTarget(
        newPos,
        friendlyEntities,
        vm.cardBounds
      )
    }
  }

  fun onShopDragEnd() {
    val state = vm.shopDragState
    val target = vm.hoveredTarget

    if (state != null && target != null) {
      val team = if (state.teamIsLeft) vm.leftTeam else vm.rightTeam

      if (target.team == team && target.isAlive) {
        if (team.shop.trySpend(state.item.cost)) {
          vm.viewModelScope.launch {
            state.item.onApply(target)
          }
          team.totalCoinsSpent += state.item.cost
        }
      }
    }

    vm.shopDragState = null
    vm.hoveredTarget = null
  }

  fun getHighlightColor(entity: EntityViewModel): Color? {
    val shopState = vm.shopDragState
    val target = vm.hoveredTarget

    if (shopState != null && entity == target) {
      // Must be friendly to highlight
      val isFriendly = (shopState.teamIsLeft && vm.leftTeam.entities.contains(entity)) ||
          (!shopState.teamIsLeft && vm.rightTeam.entities.contains(entity))
      if (isFriendly) return Color.Yellow
    }
    return null
  }
}
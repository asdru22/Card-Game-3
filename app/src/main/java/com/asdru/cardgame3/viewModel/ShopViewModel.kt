package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.asdru.cardgame3.game.item.ShopItem
import com.asdru.cardgame3.helper.ShopDataProvider

class ShopViewModel(
  val totemProvider: () -> TotemViewModel? = { null }
) {

  var coins by mutableIntStateOf(30)
    private set

  var isOpen by mutableStateOf(false)
    private set

  val isTotemDestroyed by derivedStateOf {
     val totem = totemProvider()
     totem?.isAlive != true
  }

  val items: List<ShopItem> by derivedStateOf {
    ShopDataProvider.getShopItems()
  }

  val totemItems: List<ShopItem.TotemItem> by derivedStateOf {
    if (isTotemDestroyed) {
      com.asdru.cardgame3.helper.TotemDataProvider.getAvailableTotems().map {
        ShopItem.TotemItem(it, 15)
      }
    } else {
      emptyList()
    }
  }

  fun toggle() {
    isOpen = !isOpen
  }


  fun modifyCoins(amount: Int) {
    coins += amount
  }

  fun onEndOfTurn() {
    modifyCoins(3)
  }

  fun reset() {
    isOpen = false
  }

  fun canAfford(cost: Int): Boolean {
    return coins >= cost
  }

  fun trySpend(amount: Int): Boolean {
    if (canAfford(amount)) {
      coins -= amount
      return true
    }
    return false
  }
}
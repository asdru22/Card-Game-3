package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.asdru.cardgame3.game.item.ShopItem
import com.asdru.cardgame3.helper.ShopDataProvider

class ShopViewModel() {

  var coins by mutableIntStateOf(30)
    private set

  var isOpen by mutableStateOf(false)
    private set

  var isTotemDestroyed by mutableStateOf(false)

  val items: List<ShopItem> by derivedStateOf {
    val standardItems = ShopDataProvider.getShopItems()
    if (isTotemDestroyed) {
      standardItems + ShopItem.TotemRepairItem(15)
    } else {
      standardItems
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
package com.asdru.cardgame3.game.weather

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.BattleViewModel

class GoldenShower : WeatherEvent(
  nameRes = R.string.weather_golden_shower,
  descriptionRes = R.string.weather_golden_shower_desc,
  iconRes = R.drawable.weather_golden_shower,
  formatArgs = listOf(COIN_AMOUNT),
  color = Color(0xFFFFEB3B),
) {

  override suspend fun onEndTurn(viewModel: BattleViewModel) {
    viewModel.rightTeam.shop.addCoins(COIN_AMOUNT)
    viewModel.leftTeam.shop.addCoins(COIN_AMOUNT)
  }

  private companion object {
    const val COIN_AMOUNT = 7
  }
}
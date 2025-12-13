package com.asdru.cardgame3.game.weather

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.BattleViewModel

class Eruption : WeatherEvent(
  nameRes = R.string.weather_eruption,
  descriptionRes = R.string.weather_eruption_desc,
  iconRes = R.drawable.weather_eruption,
  formatArgs = listOf(RAGE_INCREASE),
  color = Color(0xFFE91E63),
) {

  override suspend fun onEndTurn(viewModel: BattleViewModel) {
    viewModel.rightTeam.increaseRage(RAGE_INCREASE)
    viewModel.leftTeam.increaseRage(RAGE_INCREASE)
  }

  private companion object {
    const val RAGE_INCREASE = 7f
  }
}
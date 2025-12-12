package com.asdru.cardgame3.game.weather

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.game.effect.Strength
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.random.Random

class Fog() : WeatherEvent(
  nameRes = R.string.weather_fog,
  descriptionRes = R.string.weather_fog_desc,
  iconRes = R.drawable.weather_fog,
  formatArgs = listOf(DODGE_CHANCE),
  color = Color(0xFF8BD5D9),
) {

  override fun modifyIncomingDamage(
    owner: EntityViewModel,
    source: EntityViewModel?,
    amount: Float
  ): Float {
    if (Random.nextFloat() < (DODGE_CHANCE / 100)) {
      owner.addPopup(R.string.game_miss)
      return 0f
    }
    return amount
  }

  private companion object {
    const val DODGE_CHANCE = 5
  }
}
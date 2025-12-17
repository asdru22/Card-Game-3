package com.asdru.cardgame3.game.weather

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.helper.receiveDamage
import com.asdru.cardgame3.system.BatteryMonitor
import com.asdru.cardgame3.viewModel.BattleViewModel

class ElectricStorm : WeatherEvent(
  nameRes = R.string.weather_electric_storm,
  descriptionRes = R.string.weather_electric_storm_desc,
  iconRes = R.drawable.weather_electric_storm,
  formatArgs = listOf(BatteryStatus, LIGHTNING_DAMAGE),
  color = Color(0xFFFFEB3B),
) {

  override suspend fun onStartTurn(viewModel: BattleViewModel) {

    val possibleTargets =
      viewModel.leftTeam.getAliveMembers() + viewModel.rightTeam.getAliveMembers()
    if (possibleTargets.isNotEmpty()) {
      possibleTargets.random().receiveDamage(LIGHTNING_DAMAGE)
    }
  }

  private object BatteryStatus : Translatable {
    override val nameRes = R.string.effect_battery_level
    override val descriptionRes = R.string.effect_battery_level_desc

    override fun getName(context: Context): String {
      val percentage = BatteryMonitor.getInstance(context).getBatteryPercentage()
      return "$percentage"
    }

    override val isPositive: Boolean = false
  }

  private companion object {
    const val LIGHTNING_DAMAGE = 24f
  }
}
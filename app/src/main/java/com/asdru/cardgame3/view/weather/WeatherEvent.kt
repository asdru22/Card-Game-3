package com.asdru.cardgame3.view.weather

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.reflect.full.createInstance

sealed class WeatherEvent(
  @get:StringRes override val nameRes: Int,
  @get:StringRes override val descriptionRes: Int,
  @get:DrawableRes val iconRes: Int,
  val color: Color,
  override val formatArgs: List<Any> = emptyList()
) : Translatable {

  open fun onApply(viewModel: BattleViewModel) {}
  open suspend fun onStartTurn(viewModel: BattleViewModel) {}
  open suspend fun onEndTurn(viewModel: BattleViewModel) {}
  open fun modifyIncomingDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float): Float =
    amount
  companion object {
    fun getRandomWeather(): WeatherEvent? {
      val subclasses = WeatherEvent::class.sealedSubclasses
      if (subclasses.isEmpty()) return null
      return try {
        subclasses.random().createInstance()
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    }
  }
}
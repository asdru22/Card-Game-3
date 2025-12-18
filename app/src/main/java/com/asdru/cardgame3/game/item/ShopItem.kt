package com.asdru.cardgame3.game.item

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel

sealed class ShopItem(
  @get:StringRes override val nameRes: Int,
  @get:StringRes override val descriptionRes: Int,
  val cost: Int,
  @get:DrawableRes val iconRes: Int,
  override val formatArgs: List<Any> = emptyList(),
  val onApply: suspend (EntityViewModel) -> Unit
) : Translatable {

  override fun getName(context: Context): String {
    return context.getString(nameRes)
  }

  override fun getDescription(context: Context): String {
    val processedArgs = formatArgs.map { arg ->
      if (arg is Translatable) {
        val name = context.getString(arg.nameRes)
        val descArgs = arg.formatArgs.toTypedArray()
        val desc = context.getString(arg.descriptionRes, *descArgs)
        val isPositive = arg.isPositive

        "[[$name|$desc|$isPositive]]"
      } else {
        arg
      }
    }
    return context.getString(descriptionRes, *processedArgs.toTypedArray())
  }
}
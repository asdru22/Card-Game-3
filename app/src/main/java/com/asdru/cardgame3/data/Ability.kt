package com.asdru.cardgame3.data

import android.content.Context
import androidx.annotation.StringRes
import com.asdru.cardgame3.viewModel.EntityViewModel

open class Ability(
  @get:StringRes override val nameRes: Int,
  @get:StringRes override val descriptionRes: Int,
  override val formatArgs: List<Any> = emptyList(),
  val charges: Int = 1,
  protected val onEffect: suspend (source: EntityViewModel, target: EntityViewModel) -> Unit
) : Translatable {

  suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
    onEffect(source, target)
  }

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
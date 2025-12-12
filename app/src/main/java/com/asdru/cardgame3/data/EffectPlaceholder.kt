package com.asdru.cardgame3.data

data class EffectPlaceholder(
  val name: String,
  val description: String,
  val isPositive: Boolean
) {
  override fun toString(): String = name
}
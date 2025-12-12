package com.asdru.cardgame3.data

data class EffectInfo(
  val name: String,
  val description: String,
  val isPositive: Boolean,
  val startIndex: Int,
  val endIndex: Int
)
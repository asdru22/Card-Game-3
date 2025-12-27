package com.asdru.cardgame3.data

data class DamageData(
  val enemyRageDecrease: Float = 0f,
  val ownRageIncrease: Float = 0f,
  val damageDecay: Float = 0f,
  val isRetaliation: Boolean = false
)

package com.asdru.cardgame3.data

data class Team(
  val name: String,
  val entities: List<Entity>,
  val left: Boolean,
  val initialRage: Float = 0f,
  val maxRage: Float = 100f
)
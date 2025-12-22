package com.asdru.cardgame3.data

import com.asdru.cardgame3.game.entity.Entity

data class Team(
  val name: String,
  val entities: List<Entity>,
  val left: Boolean,
  val playerId: Long? = null
)
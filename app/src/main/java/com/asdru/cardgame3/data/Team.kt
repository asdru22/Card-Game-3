package com.asdru.cardgame3.data

import com.asdru.cardgame3.entity.Entity

data class Team(
  val name: String,
  val entities: List<com.asdru.cardgame3.entity.Entity>
)
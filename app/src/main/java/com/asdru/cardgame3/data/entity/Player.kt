package com.asdru.cardgame3.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val wins: Int = 0
)

package com.asdru.cardgame3.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_stats")
data class CharacterStats(
    @PrimaryKey val characterName: String,
    val pickCount: Int = 0
)

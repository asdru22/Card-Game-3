package com.asdru.cardgame3.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.asdru.cardgame3.data.entity.CharacterStats
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterStatsDao {
    @Query("SELECT * FROM character_stats ORDER BY pickCount DESC")
    fun getAllStats(): Flow<List<CharacterStats>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(stats: CharacterStats): Long

    @Query("UPDATE character_stats SET pickCount = pickCount + 1 WHERE characterName = :characterName")
    suspend fun incrementPickCount(characterName: String): Int
}

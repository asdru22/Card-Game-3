package com.asdru.cardgame3.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.asdru.cardgame3.data.entity.Player
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY name ASC")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE name = :name LIMIT 1")
    suspend fun getPlayerByName(name: String): Player?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayer(player: Player): Long

    @Query("UPDATE players SET wins = wins + 1 WHERE id = :playerId")
    suspend fun incrementWins(playerId: Long): Int
}

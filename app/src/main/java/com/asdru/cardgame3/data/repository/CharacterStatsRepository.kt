package com.asdru.cardgame3.data.repository

import androidx.room.withTransaction
import com.asdru.cardgame3.data.database.AppDatabase
import com.asdru.cardgame3.data.entity.CharacterStats
import kotlinx.coroutines.flow.Flow

class CharacterStatsRepository(private val db: AppDatabase) {
    private val characterStatsDao = db.characterStatsDao()
    
    val allStats: Flow<List<CharacterStats>> = characterStatsDao.getAllStats()

    suspend fun incrementPickCount(characterName: String) {
        db.withTransaction {
            val stats = CharacterStats(characterName, 1)
            val rowId = characterStatsDao.insert(stats)
            if (rowId == -1L) {
                characterStatsDao.incrementPickCount(characterName)
            }
        }
    }
}

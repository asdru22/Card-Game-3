package com.asdru.cardgame3.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.asdru.cardgame3.data.dao.CharacterStatsDao
import com.asdru.cardgame3.data.dao.PlayerDao
import com.asdru.cardgame3.data.entity.CharacterStats
import com.asdru.cardgame3.data.entity.Player

@Database(entities = [Player::class, CharacterStats::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun playerDao(): PlayerDao
  abstract fun characterStatsDao(): CharacterStatsDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "cardgame3_database"
        ).fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}

package com.asdru.cardgame3.data.repository

import com.asdru.cardgame3.data.dao.PlayerDao
import com.asdru.cardgame3.data.entity.Player
import kotlinx.coroutines.flow.Flow

class PlayerRepository(private val playerDao: PlayerDao) {

  val allPlayers: Flow<List<Player>> = playerDao.getAllPlayers()

  suspend fun addPlayer(name: String): Boolean {
    if (name.isBlank()) return false
    val existing = playerDao.getPlayerByName(name)
    if (existing != null) return false

    val newPlayer = Player(name = name)
    playerDao.insertPlayer(newPlayer)
    return true
  }

  suspend fun getPlayerByName(name: String): Player? {
    return playerDao.getPlayerByName(name)
  }

  suspend fun incrementWins(playerId: Long) {
    playerDao.incrementWins(playerId)
  }
}

package com.asdru.cardgame3.repository

import com.asdru.cardgame3.game.state.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class RemoteGameRepository(
    private val apiUrl: String,
    private val wsUrl: String
) : GameRepository {
    

    override suspend fun createGame(
        player1Name: String,
        player2Name: String,
        mode: GameMode
    ): Result<String> {
        return try {
            // POST to /api/games
            // val response = httpClient.post("$apiUrl/games") { ... }
            // Result.success(response.gameId)
            
            TODO("Implement API call")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun joinGame(gameId: String, playerName: String): Result<Unit> {
        return try {
            // POST to /api/games/{gameId}/join
            TODO("Implement API call")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun startGame(
        gameId: String,
        player1Team: List<String>,
        player2Team: List<String>
    ): Result<GameState> {
        return try {
            // POST to /api/games/{gameId}/start
            TODO("Implement API call")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun submitAction(gameId: String, action: GameAction): Result<Unit> {
        return try {
            // Send via WebSocket or POST to /api/games/{gameId}/actions
            TODO("Implement API call")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeGameState(gameId: String): Flow<GameState> {
        return flow {
            // WebSocket subscription to game state updates
            TODO("Implement WebSocket subscription")
        }
    }
    
    override fun observeGameEvents(gameId: String): Flow<GameEvent> {
        return flow {
            // WebSocket subscription to game events
            TODO("Implement WebSocket subscription")
        }
    }
    
    override suspend fun getGameHistory(gameId: String): Result<List<GameAction>> {
        return try {
            // GET /api/games/{gameId}/history
            TODO("Implement API call")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun endGame(gameId: String): Result<Unit> {
        return try {
            // POST to /api/games/{gameId}/end
            TODO("Implement API call")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
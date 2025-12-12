package com.asdru.cardgame3.repository

import com.asdru.cardgame3.game.engine.GameEngine
import com.asdru.cardgame3.game.state.*
import com.asdru.cardgame3.network.LocalGameClient
import com.asdru.cardgame3.network.TeamSetup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class LocalGameRepository(
    private val scope: CoroutineScope
) : GameRepository {
    
    private val activeGames = mutableMapOf<String, GameEngine>()
    private val gameClients = mutableMapOf<String, LocalGameClient>()
    
    override suspend fun createGame(
        player1Name: String,
        player2Name: String,
        mode: GameMode
    ): Result<String> {
        val gameId = UUID.randomUUID().toString()
        val engine = GameEngine(gameId, scope)
        activeGames[gameId] = engine
        
        val client = LocalGameClient(scope)
        gameClients[gameId] = client
        client.connect()
        
        return Result.success(gameId)
    }
    
    override suspend fun joinGame(gameId: String, playerName: String): Result<Unit> {
        // For local games, joining is automatic
        return Result.success(Unit)
    }
    
    override suspend fun startGame(
        gameId: String,
        player1Team: List<String>,
        player2Team: List<String>
    ): Result<GameState> {
        val client = gameClients[gameId] 
            ?: return Result.failure(Exception("Game not found"))
        
        val teamSetup = TeamSetup(player1Team, player2Team)
        return client.startGame(teamSetup)
    }
    
    override suspend fun submitAction(gameId: String, action: GameAction): Result<Unit> {
        val client = gameClients[gameId]
            ?: return Result.failure(Exception("Game not found"))
        
        return client.sendAction(action)
    }
    
    override fun observeGameState(gameId: String): Flow<GameState> {
        val client = gameClients[gameId]
            ?: throw IllegalStateException("Game not found")
        
        return client.observeGameState()
    }
    
    override fun observeGameEvents(gameId: String): Flow<GameEvent> {
        val client = gameClients[gameId]
            ?: throw IllegalStateException("Game not found")
        
        return client.observeGameEvents()
    }
    
    override suspend fun getGameHistory(gameId: String): Result<List<GameAction>> {
        // Local games don't persist history
        return Result.success(emptyList())
    }
    
    override suspend fun endGame(gameId: String): Result<Unit> {
        gameClients[gameId]?.disconnect()
        gameClients.remove(gameId)
        activeGames.remove(gameId)
        return Result.success(Unit)
    }
}
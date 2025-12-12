package com.asdru.cardgame3.repository

import kotlinx.coroutines.CoroutineScope

object RepositoryFactory {
    fun createRepository(
        mode: GameMode,
        scope: CoroutineScope
    ): GameRepository {
        return when (mode) {
            GameMode.LOCAL -> LocalGameRepository(scope)
            GameMode.ONLINE_RANDOM,
            GameMode.ONLINE_PRIVATE -> RemoteGameRepository(
                apiUrl = "https://your-api.com",
                wsUrl = "wss://your-api.com/ws"
            )
        }
    }
}
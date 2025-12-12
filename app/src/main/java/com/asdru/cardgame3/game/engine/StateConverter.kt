package com.asdru.cardgame3.game.engine

import com.asdru.cardgame3.game.state.*
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.TeamViewModel
import java.util.UUID


object StateConverter {
    
    fun teamToState(team: TeamViewModel): TeamState {
        return TeamState(
            teamId = UUID.randomUUID().toString(),
            name = team.name,
            entities = team.entities.mapIndexed { index, entity ->
                entityToState(entity, index)
            },
            rage = team.rage,
            maxRage = team.maxRage
        )
    }
    
    fun entityToState(entity: EntityViewModel, position: Int): EntityState {
        return EntityState(
            entityId = UUID.randomUUID().toString(),
            entityClass = entity.entity::class.qualifiedName ?: "",
            health = entity.health,
            maxHealth = entity.maxHealth,
            damage = entity.damage,
            statusEffects = entity.statusEffects.map { effect ->
                StatusEffectState(
                    effectClass = effect::class.qualifiedName ?: "",
                    duration = effect.duration,
                    sourceEntityId = null // Would need to map this
                )
            },
            isAlive = entity.isAlive,
            position = position
        )
    }
    
    fun gameStateFromViewModels(
        gameId: String,
        leftTeam: TeamViewModel,
        rightTeam: TeamViewModel,
        isLeftTeamTurn: Boolean,
        actionsTaken: List<EntityViewModel>,
        winner: String?
    ): GameState {
        return GameState(
            gameId = gameId,
            leftTeam = teamToState(leftTeam),
            rightTeam = teamToState(rightTeam),
            isLeftTeamTurn = isLeftTeamTurn,
            turnNumber = 1,
            actionsTaken = actionsTaken.map { it.entity::class.qualifiedName ?: "" },
            winner = winner
        )
    }
}
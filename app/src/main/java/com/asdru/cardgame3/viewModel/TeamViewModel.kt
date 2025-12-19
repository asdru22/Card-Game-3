package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.asdru.cardgame3.data.Team

class TeamViewModel(
    val team: Team,
    initialEntities: List<EntityViewModel>
) : ViewModel() {

    lateinit var opposingTeam: TeamViewModel

    val entities = mutableStateListOf<EntityViewModel>().apply {
        addAll(initialEntities)
        forEach { it.team = this@TeamViewModel }
    }

    fun addEntity(entity: EntityViewModel) {
        entity.team = this
        entities.add(entity)
    }

    fun getAllEntities(): List<EntityViewModel> {
        return entities
    }

    fun getMembers(): List<EntityViewModel> {
        return entities.filter { !it.combatManager.isDead }
    }

    fun getOpposingTeam(): List<EntityViewModel> {
        return if (::opposingTeam.isInitialized) {
            opposingTeam.getMembers()
        } else {
            emptyList()
        }
    }

    fun getCharacters(): List<CharacterViewModel> {
        return entities.filterIsInstance<CharacterViewModel>()
    }
}

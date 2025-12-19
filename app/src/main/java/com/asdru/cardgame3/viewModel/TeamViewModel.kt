package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.asdru.cardgame3.data.Team

class TeamViewModel(
  val team: Team,
  initialEntities: List<EntityViewModel>
) : ViewModel() {

  var rage by mutableFloatStateOf(team.initialRage)
  val maxRage = team.maxRage

  lateinit var opposingTeam: TeamViewModel

  val entities = mutableStateListOf<EntityViewModel>().apply {
    addAll(initialEntities)
    forEach { it.team = this@TeamViewModel }
  }

  fun addEntity(entity: EntityViewModel) {
    entity.team = this
    entities.add(entity)
  }

  fun removeEntity(entity: EntityViewModel) {
    entities.remove(entity)
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

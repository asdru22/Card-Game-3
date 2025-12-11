package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import com.asdru.cardgame3.data.Team

class TeamViewModel(
  val team: Team
) {
  val name: String = team.name
  val entities: List<EntityViewModel> = team.entities.map { EntityViewModel(it) }

  lateinit var enemyTeam: TeamViewModel

  init {
    entities.forEach {
      it.team = this
    }
  }

  val aliveEntities by derivedStateOf {
    entities.filter { it.isAlive }
  }

  var rage by mutableFloatStateOf(0f)
  val maxRage = 100f

  fun increaseRage(amount: Float) {
    rage = (rage + amount).coerceAtMost(maxRage)
  }

  fun getAliveEnemies(): List<EntityViewModel> {
    return enemyTeam.aliveEntities
  }

  fun getAllTeamMembers(): List<EntityViewModel> {
    return entities
  }

  fun getAliveTeamMembers(): List<EntityViewModel> {
    return getAllTeamMembers().filter { it.isAlive }
  }

  fun getRandomAliveMember(): EntityViewModel {
    return getAliveTeamMembers().random()
  }

  fun getRandomAliveEnemy(): EntityViewModel {
    return getAliveEnemies().random()
  }

}
package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import com.asdru.cardgame3.data.Team
import com.asdru.cardgame3.effect.Vanish
import kotlin.collections.filter

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

  fun getAllMembers(): List<EntityViewModel> {
    return entities
  }

  fun getAliveMembers(): List<EntityViewModel> {
    return getAllMembers().filter { it.isAlive }
  }

  fun getOtherAliveMembers(entity: EntityViewModel): List<EntityViewModel> {
    return getAliveMembers().filter { it != entity }
  }

  fun getRandomAliveMember(): EntityViewModel {
    return getAliveMembers().random()
  }

  fun getAliveEnemies(): List<EntityViewModel> {
    return enemyTeam.aliveEntities
  }

  fun getTargetableEnemies(): List<EntityViewModel> {
    return getAliveEnemies().filter { entity ->
      entity.statusEffects.none { it is Vanish }
    }
  }

  fun getRandomTargetableEnemy(): EntityViewModel {
    return getAliveEnemies().random()
  }

}
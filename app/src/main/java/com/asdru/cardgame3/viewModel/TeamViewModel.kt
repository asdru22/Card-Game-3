package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.asdru.cardgame3.data.Team
import com.asdru.cardgame3.game.effect.Vanish

class TeamViewModel(
  val team: Team
) {
  val name: String = team.name
  val playerId: Long? = team.playerId
  val entities: List<EntityViewModel> = team.entities.map { EntityViewModel(it, team.left) }
  lateinit var enemyTeam: TeamViewModel

  val shop = ShopViewModel { totem }
  var totem: TotemViewModel? by mutableStateOf(null)

  fun onTeamDamage(amount: Float) {
    if (amount > 0) {
      totem?.takeDamage(amount)
    }
  }

  fun updateShopState() {
     // Shop state is now derived from totem health
  }

  var totalDamageDealt: Float = 0f
  var totalHealing: Float = 0f
  var totalEffectsApplied: Int = 0
  var totalCoinsSpent: Int = 0

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

  fun decreaseRage(amount: Float) {
    rage = (rage - amount).coerceAtLeast(0f)
  }

  fun getAllMembers(): List<EntityViewModel> {
    return entities
  }

  fun getAliveMembers(): List<EntityViewModel> {
    return getAllMembers().filter { it.isAlive }
  }

  fun getOtherAliveMembers(filter: EntityViewModel): List<EntityViewModel> {
    return getAliveMembers().filter { it != filter }
  }

  fun getRandomAliveMember(): EntityViewModel? {
    return getAliveMembers().randomOrNull()
  }

  fun getAliveEnemies(): List<EntityViewModel> {
    return enemyTeam.aliveEntities
  }

  fun getTargetableEnemies(): List<EntityViewModel> {
    return getAliveEnemies().filter { entity ->
      entity.effectManager.effects.none { it is Vanish }
    }
  }

  fun getRandomTargetableEnemy(): EntityViewModel? {
    return getTargetableEnemies().randomOrNull()
  }
}
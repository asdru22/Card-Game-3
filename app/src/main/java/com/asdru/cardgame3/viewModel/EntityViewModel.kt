package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.game.effect.StatusEffect
import com.asdru.cardgame3.game.entity.Entity
import com.asdru.cardgame3.game.trait.Trait
import com.asdru.cardgame3.game.weather.WeatherEvent
import com.asdru.cardgame3.helper.EntityEffectManager
import com.asdru.cardgame3.helper.EntityPopupManager
import com.asdru.cardgame3.helper.onDeath

open class EntityViewModel(
  val entity: Entity,
  val isLeftTeam: Boolean
) : ViewModel() {

  val popupManager = EntityPopupManager()
  val effectManager = EntityEffectManager(onEffectsChanged = { recalculateStats() })

  lateinit var team: TeamViewModel

  private var baseDamage = entity.initialStats.damage

  var damage by mutableFloatStateOf(baseDamage)
    internal set

  var health by mutableFloatStateOf(entity.initialStats.maxHealth)
  var maxHealth by mutableFloatStateOf(entity.initialStats.maxHealth)
  var overhealAmount by mutableFloatStateOf(0f)

  var currentActiveCharges by mutableIntStateOf(0)
  var currentPassiveCharges by mutableIntStateOf(0)
  var traitCharges = mutableStateMapOf<String, Int>()

  var attackAnimOffset by mutableStateOf<Offset?>(null)
  var hitAnimTrigger by mutableIntStateOf(0)
  var passiveAnimTrigger by mutableIntStateOf(0)
  var chargeAnimTrigger by mutableIntStateOf(0)

  var onGetAttackOffset: ((EntityViewModel) -> Offset?)? = null

  val isAlive: Boolean get() = health > 0
  val name: Int = entity.name
  val color: Color = entity.color
  val damageType: DamageType = entity.damageType
  val iconRes: Int = entity.iconRes
  open val traits: List<Trait> get() = entity.traits

  private var activeAbilityOverride by mutableStateOf<Ability?>(null)
  private var ultimateAbilityOverride by mutableStateOf<Ability?>(null)

  val activeAbility: Ability
    get() = activeAbilityOverride ?: entity.activeAbility

  val passiveAbility: Ability
    get() = entity.passiveAbility

  val ultimateAbility: Ability
    get() = ultimateAbilityOverride ?: entity.ultimateAbility

  fun swapAbilities() {
    if (entity.alternateActiveAbilities.isNotEmpty()) {
      activeAbilityOverride = if (activeAbilityOverride == null) {
        entity.alternateActiveAbilities.firstOrNull()
      } else {
        null
      }
    }

    if (entity.alternateUltimateAbilities.isNotEmpty()) {
      ultimateAbilityOverride = if (ultimateAbilityOverride == null) {
        entity.alternateUltimateAbilities.firstOrNull()
      } else {
        null
      }
    }
  }

  var onGetWeather: (() -> WeatherEvent?)? = null

  suspend fun kill() {
    this.health = 0f
    this.onDeath(null, 0f)
  }

  fun recalculateStats() {
    var newDamage = baseDamage
    effectManager.effects.forEach { effect ->
      newDamage = effect.modifyDamage(newDamage, this, null)
    }
    damage = newDamage
  }

  inline fun applyTraits(action: (Trait) -> Unit) {
    traits.forEach(action)
  }

  fun addEffect(effect: StatusEffect, source: EntityViewModel? = null) {
    effectManager.addEffect(effect, source, this)
  }

  fun resetCharges() {
    currentActiveCharges = 0
    currentPassiveCharges = 0
  }
}
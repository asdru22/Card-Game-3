package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.game.effect.StatusEffect
import com.asdru.cardgame3.game.entity.Entity
import com.asdru.cardgame3.game.trait.Trait
import com.asdru.cardgame3.game.weather.WeatherEvent
import com.asdru.cardgame3.manager.EntityEffectManager
import com.asdru.cardgame3.manager.EntityPopupManager

class EntityViewModel(
  val entity: Entity
) : ViewModel() {

  val popupManager = EntityPopupManager()
  val effectManager = EntityEffectManager(onEffectsChanged = { recalculateStats() })

  lateinit var team: TeamViewModel

  private var baseDamage = entity.initialStats.damage

  var damage by mutableFloatStateOf(baseDamage)
    internal set

  var health by mutableFloatStateOf(entity.initialStats.maxHealth)
  var maxHealth by mutableFloatStateOf(entity.initialStats.maxHealth)

  var currentActiveCharges by mutableIntStateOf(0)
  var currentPassiveCharges by mutableIntStateOf(0)

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
  val traits: List<Trait> get() = entity.traits

  var onGetWeather: (() -> WeatherEvent?)? = null

  fun recalculateStats() {
    var newDamage = baseDamage
    effectManager.effects.forEach { effect ->
      newDamage = effect.modifyDamage(newDamage)
    }
    damage = newDamage
  }

  inline fun applyTraits(action: (Trait) -> Unit) {
    traits.forEach(action)
  }

  fun addEffect(effect: StatusEffect, source: EntityViewModel?) {
    effectManager.addEffect(effect, source, this)
  }

  fun removeEffect(effect: StatusEffect) {
    effectManager.removeEffect(effect, this)
  }
}
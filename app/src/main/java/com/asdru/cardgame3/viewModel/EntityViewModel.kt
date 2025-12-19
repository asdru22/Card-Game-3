package com.asdru.cardgame3.viewModel

import androidx.lifecycle.ViewModel
import com.asdru.cardgame3.data.Entity

open class EntityViewModel(
    val entity: Entity
) : ViewModel() {
    lateinit var team: TeamViewModel
    val combatManager = CombatManager(entity.initialStats)
    val traitManager = TraitManager(entity.traits)
}
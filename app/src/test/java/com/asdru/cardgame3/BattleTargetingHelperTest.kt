package com.asdru.cardgame3

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.asdru.cardgame3.data.DragState
import com.asdru.cardgame3.game.entity.Archer
import com.asdru.cardgame3.game.trait.Trait
import com.asdru.cardgame3.game.trait.Ugly
import com.asdru.cardgame3.helper.BattleTargetingHelper
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.TeamViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BattleTargetingHelperTest {

  // Subclass to override traits
  class TestEntityViewModel(
    val overrideTraits: List<Trait>? = null,
    isLeftTeam: Boolean
  ) : EntityViewModel(Archer(), isLeftTeam) {
    override val traits: List<Trait>
      get() = overrideTraits ?: super.traits
  }

  @Test
  fun testUglyTrait_AllyTargeting() {
    val uglyTrait = Ugly()
    val source = TestEntityViewModel(isLeftTeam = true) // Normal ally
    val target = TestEntityViewModel(overrideTraits = listOf(uglyTrait), isLeftTeam = true) // Ugly ally

    val dragState = DragState(source, Offset.Zero, Offset.Zero)
    val cardBounds = mapOf(target to Rect(0f, 0f, 100f, 100f))
    val leftTeamEntities = listOf(source, target)

    // Simulate drag over target
    val result = BattleTargetingHelper.findValidTarget(
      dragState,
      Offset(50f, 50f),
      cardBounds,
      leftTeamEntities
    )

    // Should be null because allies cannot target Ugly
    assertNull("Ally should not be able to target Ugly ally", result)
  }

  @Test
  fun testUglyTrait_EnemyTargeting() {
    val uglyTrait = Ugly()
    val source = TestEntityViewModel(isLeftTeam = false) // Enemy
    val target = TestEntityViewModel(overrideTraits = listOf(uglyTrait), isLeftTeam = true) // Ugly ally

    val dragState = DragState(source, Offset.Zero, Offset.Zero)
    val cardBounds = mapOf(target to Rect(0f, 0f, 100f, 100f))
    val leftTeamEntities = listOf(target) // Source is NOT in left team

    // Simulate drag over target
    val result = BattleTargetingHelper.findValidTarget(
      dragState,
      Offset(50f, 50f),
      cardBounds,
      leftTeamEntities
    )

    // Should be valid because enemies CAN target Ugly
    assertEquals("Enemy should be able to target Ugly unit", target, result)
  }

  @Test
  fun testUglyTrait_SelfTargeting() {
    val uglyTrait = Ugly()
    val source = TestEntityViewModel(overrideTraits = listOf(uglyTrait), isLeftTeam = true) // Ugly unit
    val target = source // Self

    val dragState = DragState(source, Offset.Zero, Offset.Zero)
    val cardBounds = mapOf(target to Rect(0f, 0f, 100f, 100f))
    val leftTeamEntities = listOf(source)

    // Simulate drag over self
    val result = BattleTargetingHelper.findValidTarget(
      dragState,
      Offset(50f, 50f),
      cardBounds,
      leftTeamEntities
    )

    // Should be valid (or at least not blocked by Ugly trait logic)
    // Note: Whether self-targeting is valid generally depends on other logic, 
    // but here we check it's NOT blocked by 'isSource != target' check in Ugly logic.
    // The default logic allows self targeting if alive etc.
    assertEquals("Unit should be able to target itself despite Ugly trait", target, result)
  }

  @Test
  fun testNormalTargeting() {
    val source = TestEntityViewModel(isLeftTeam = true)
    val target = TestEntityViewModel(isLeftTeam = true) // Normal ally

    val dragState = DragState(source, Offset.Zero, Offset.Zero)
    val cardBounds = mapOf(target to Rect(0f, 0f, 100f, 100f))
    val leftTeamEntities = listOf(source, target)

    val result = BattleTargetingHelper.findValidTarget(
      dragState,
      Offset(50f, 50f),
      cardBounds,
      leftTeamEntities
    )

    // Should be valid
    assertEquals("Ally should be able to target normal ally", target, result)
  }
}

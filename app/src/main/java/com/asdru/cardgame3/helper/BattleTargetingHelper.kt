package com.asdru.cardgame3.helper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.asdru.cardgame3.data.DragState
import com.asdru.cardgame3.game.effect.Taunt
import com.asdru.cardgame3.game.effect.Vanish
import com.asdru.cardgame3.game.trait.Ugly
import com.asdru.cardgame3.viewModel.EntityViewModel

object BattleTargetingHelper {

  fun findValidTarget(
    dragState: DragState,
    dragPosition: Offset,
    cardBounds: Map<EntityViewModel, Rect>,
    leftTeamEntities: List<EntityViewModel>
  ): EntityViewModel? {

    return cardBounds.entries.firstOrNull { (entity, rect) ->
      val isTargetAlive = entity.isAlive
      val isHovering = rect.contains(dragPosition)

      if (!isTargetAlive || !isHovering) return@firstOrNull false

      val isSourceLeft = leftTeamEntities.contains(dragState.source)
      val isTargetLeft = leftTeamEntities.contains(entity)
      val isEnemy = isSourceLeft != isTargetLeft

      if (isEnemy && entity.effectManager.effects.any { it is Vanish }) {
        return@firstOrNull false
      }

      if (!isEnemy && dragState.source != entity && entity.traits.any { it is Ugly }) {
        return@firstOrNull false
      }

      val taunt = dragState.source.effectManager.effects.find { it is Taunt }
      if (taunt != null && taunt.source?.isAlive == true) {
        if (isEnemy && entity != taunt.source) {
          return@firstOrNull false
        }
      }

      true
    }?.key
  }


  fun findUltimateTarget(
    newPos: Offset,
    teamEntities: List<EntityViewModel>,
    cardBounds: Map<EntityViewModel, Rect>
  ): EntityViewModel? {
    return cardBounds.entries.firstOrNull { (entity, rect) ->
      entity.isAlive &&
          !entity.effectManager.isStunned &&
          !entity.effectManager.isSilenced &&
          rect.contains(newPos) &&
          teamEntities.contains(entity)
    }?.key
  }
}
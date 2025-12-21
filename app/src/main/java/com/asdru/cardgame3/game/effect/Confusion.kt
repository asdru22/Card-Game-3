package com.asdru.cardgame3.game.effect

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.random.Random

class Confusion(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {

  override fun modifyActiveTarget(
    owner: EntityViewModel,
    target: EntityViewModel
  ): EntityViewModel {
    if (Random.nextFloat() >= (CHANGE_TARGET_CHANCE / 100f)) return target

    val potentialTargets = target.team.getTargetableEnemies().filter { it != target }

    if (potentialTargets.isEmpty()) return target

    owner.popupManager.add("?", Color.Magenta)
    return potentialTargets.random()
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_confusion
    override val formatArgs = listOf(CHANGE_TARGET_CHANCE)
    override val nameRes = R.string.effect_confusion
    override val descriptionRes = R.string.effect_confusion_desc
    override val isPositive = false

    private const val CHANGE_TARGET_CHANCE = 33
  }
}

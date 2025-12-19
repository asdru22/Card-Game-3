package com.asdru.cardgame3.data

import androidx.annotation.StringRes
import com.asdru.cardgame3.viewModel.CharacterViewModel
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.SummonViewModel

class SummonAbility(
  @StringRes nameRes: Int,
  @StringRes descriptionRes: Int,
  formatArgs: List<Any> = emptyList(),
  charges: Int = 1,
  private val onSummonEffect: suspend (summon: SummonViewModel, summoner: CharacterViewModel, target: EntityViewModel) -> Unit
) : Ability(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  formatArgs = formatArgs,
  charges = charges,
  onEffect = { source, target ->
    val summon = source as SummonViewModel
    val summoner = summon.owner
    onSummonEffect(summon, summoner, target)
  }
)
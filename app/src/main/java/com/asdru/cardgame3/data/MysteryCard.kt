package com.asdru.cardgame3.data

import com.asdru.cardgame3.viewModel.TeamViewModel

data class MysteryCard(
  val descriptionRes: Int,
  val weight: Int = 1,
  val onApply: suspend (TeamViewModel) -> Unit
)

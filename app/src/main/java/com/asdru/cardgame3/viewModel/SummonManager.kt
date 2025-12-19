package com.asdru.cardgame3.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.asdru.cardgame3.data.Summon

class SummonManager(
  val owner: CharacterViewModel
) {
  var activeSummon by mutableStateOf<SummonViewModel?>(null)
    private set

  fun summon(summonData: Summon) {
    activeSummon?.let {
      owner.team.removeEntity(it)
    }

    val newSummon = SummonViewModel(summonData, owner)
    owner.team.addEntity(newSummon)
    activeSummon = newSummon
  }
}

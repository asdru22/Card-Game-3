package com.asdru.cardgame3.game.trait

import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.EntityViewModel

class Artist(
  private var currentNotes: Int = 0
) : Trait {
  override val nameRes: Int = R.string.trait_artist
  override val descriptionRes: Int = R.string.trait_artist_desc
  override val formatArgs: List<Any>
    get() = listOf(currentNotes)

  public fun addNotes(amount: Int) {
    currentNotes += amount
  }

  public fun resetNotes(): Int {
    val notes = currentNotes
    currentNotes = 0
    return notes
  }
}
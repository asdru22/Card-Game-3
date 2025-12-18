package com.asdru.cardgame3.game.summon

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.Stats

class TestSummon : Summon(
  name = R.string.ability_cover,
  iconRes = R.drawable.icon_discord,
  initialStats = Stats(100f,0f),
  color = Color.Red ,
  ability = Ability(
    nameRes = R.string.ability_cover,
    descriptionRes = R.string.ability_cover_desc,
  ) { _, _ ->
  }
)
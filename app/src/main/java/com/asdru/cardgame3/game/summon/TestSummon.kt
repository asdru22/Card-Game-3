package com.asdru.cardgame3.game.summon

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.helper.applyDamage

class TestSummon : Summon(
  name = R.string.ability_cover,
  iconRes = R.drawable.icon_discord,
  initialStats = Stats(100f, 3f),
  color = Color.Red,
  ability = Ability(
    nameRes = R.string.ability_cover,
    descriptionRes = R.string.ability_cover_desc,
    formatArgs = listOf("POP", 2)
  ) { summoner, target ->
    summoner.applyDamage(target, 10f, playAttackAnimation = false)
  }
)
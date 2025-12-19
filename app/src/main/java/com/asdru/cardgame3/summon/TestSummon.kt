package com.asdru.cardgame3.summon

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.data.Summon
import com.asdru.cardgame3.data.SummonAbility

class TestSummon : Summon(
  name = R.string.entity_archer,
  iconRes = R.drawable.entity_archer,
  initialStats = Stats(maxHealth = 34f, damage = 23f),
  color = Color(0xFF0893FF),
  ability = SummonAbility(
    nameRes = R.string.ability_arrow_rain,
    descriptionRes = R.string.ability_arrow_rain_desc,
    formatArgs = listOf(46)
  ) { summon, _, target ->
    summon.combatManager.applyDamage(target, 10f)
  },
)
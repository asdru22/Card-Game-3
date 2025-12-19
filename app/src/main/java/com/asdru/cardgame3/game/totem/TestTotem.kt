package com.asdru.cardgame3.game.totem

import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.data.TotemAbility

class TestTotem : Totem(
  R.string.entity_the_magnet,
  R.drawable.entity_the_magnet,
  Stats(50f, 5f),
  activeAbility = TotemAbility(
    R.string.totem_ability_magnet_pull_name,
    R.string.totem_ability_magnet_pull_desc
  ) { sourceTotem, target ->
  },
  passiveAbility = TotemAbility(
    R.string.totem_ability_magnet_pull_name,
    R.string.totem_ability_magnet_pull_desc
  ) { sourceTotem, target ->
  }
)
package com.asdru.cardgame3.game.entity

import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.data.Stats
import com.asdru.cardgame3.game.effect.Inspired
import com.asdru.cardgame3.game.trait.Artist
import com.asdru.cardgame3.helper.applyDamage
import com.asdru.cardgame3.helper.heal
import com.asdru.cardgame3.viewModel.EntityViewModel

class Bard : Entity(
  name = R.string.entity_bard,
  iconRes = R.drawable.entity_bard,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFF708FF),
  damageType = DamageType.Magic,
  traits = listOf(Artist()),
  radarStats = RadarStats(0.2f, 0.7f, 0.8f, 0.4f, 0.7f),
  activeAbility = Ability(
    nameRes = R.string.ability_power_chord,
    descriptionRes = R.string.ability_power_chord_desc,
    formatArgs = listOf(ACTIVE_NOTES),
  ) { source, target ->
    source.applyDamage(target)
    val trait = getArtistTrait(source)
    var notes = ACTIVE_NOTES
    if (source.effectManager.hasEffect<Inspired>()) notes += Inspired.getExtraNotes()
    trait.addNotes(notes)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_serenade,
    descriptionRes = R.string.ability_serenade_desc,
    formatArgs = listOf(HEAL_PER_NOTE, EFFECT_CLEAR_TRESHOLD)
  ) { source, target ->
    val notes = getArtistTrait(source).resetNotes()
    target.heal(notes * HEAL_PER_NOTE, source)
    if (notes >= EFFECT_CLEAR_TRESHOLD) target.effectManager.clearNegative(target, false)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_magnum_opus,
    descriptionRes = R.string.ability_magnum_opus_desc,
    formatArgs = listOf(ULTIMATE_NOTES_GAINED, Inspired.Spec, ULTIMATE_EFFECT_DURATION)
  ) { source, _ ->
    getArtistTrait(source).addNotes(ULTIMATE_NOTES_GAINED)
    source.addEffect(Inspired(ULTIMATE_EFFECT_DURATION), source)
  }
) {
  private companion object {
    private fun getArtistTrait(source: EntityViewModel): Artist {
      return source.traits.get(0) as Artist
    }
    const val MAX_HEALTH = 150f
    const val DAMAGE = 8f
    const val ACTIVE_NOTES = 5
    const val HEAL_PER_NOTE = 2f
    const val EFFECT_CLEAR_TRESHOLD = 16
    const val ULTIMATE_NOTES_GAINED = 15
    const val ULTIMATE_EFFECT_DURATION = 2
  }
}
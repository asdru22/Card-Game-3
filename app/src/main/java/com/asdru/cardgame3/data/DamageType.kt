package com.asdru.cardgame3.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.asdru.cardgame3.R

enum class DamageType(
  val tintColor: Color,
  @param:DrawableRes val iconResId: Int
) {
  Ranged(Color(0xFF8BC34A), R.drawable.damage_type_ranged),
  Magic(Color(0xFF00EAFF), R.drawable.damage_type_magic),
  Melee(Color(0xFFE91E63), R.drawable.damage_type_melee);
}
package com.asdru.cardgame3.view.character

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.view.common.SmartDescriptionText

@Composable
fun CharacterAbility(
  context: Context,
  label: String,
  ability: Ability,
  color: Color
) {
  Column(modifier = Modifier.padding(bottom = 12.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = label,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 12.sp,
        style = TextStyle(
          platformStyle = PlatformTextStyle(
            includeFontPadding = false
          )
        ),
        modifier = Modifier
          .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
          .padding(horizontal = 6.dp, vertical = 3.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = ability.getName(context),
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
      )
    }

    SmartDescriptionText(
      translatable = ability,
      textColor = Color.LightGray
    )
  }
}
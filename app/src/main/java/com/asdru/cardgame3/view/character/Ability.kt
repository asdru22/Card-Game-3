package com.asdru.cardgame3.view.character

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.asdru.cardgame3.data.Ability
import com.asdru.cardgame3.data.EffectInfo
import com.asdru.cardgame3.data.EffectPlaceholder
import com.asdru.cardgame3.data.Translatable
import kotlinx.coroutines.delay


@Composable
fun CharacterAbility(context: Context, label: String, ability: Ability, color: Color) {
  var popupControl by remember { mutableStateOf<Pair<String, Offset>?>(null) }

  LaunchedEffect(popupControl) {
    if (popupControl != null) {
      delay(2000)
      popupControl = null
    }
  }

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

    Box {
      val (annotatedString, effectInfos) = remember(ability, context) {
        buildAbilityText(ability, context)
      }

      val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

      Text(
        text = annotatedString,
        color = Color.LightGray,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        modifier = Modifier
          .padding(top = 4.dp)
          .pointerInput(effectInfos) {
            detectTapGestures { pos ->
              layoutResult.value?.let { layout ->
                val offset = layout.getOffsetForPosition(pos)

                // Find which effect was clicked
                val clickedEffect = effectInfos.firstOrNull { effect ->
                  offset in effect.startIndex until effect.endIndex
                }

                if (clickedEffect != null) {
                  popupControl = clickedEffect.description to pos
                }
              }
            }
          },
        onTextLayout = { layoutResult.value = it }
      )

      popupControl?.let { (desc, pos) ->
        Popup(
          alignment = Alignment.TopStart,
          offset = IntOffset(pos.x.toInt(), pos.y.toInt() + 20),
          onDismissRequest = { popupControl = null }
        ) {
          Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .widthIn(max = 400.dp)
              .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
          ) {
            Text(
              text = desc,
              color = Color.White,
              fontSize = 12.sp,
              modifier = Modifier.padding(4.dp)
            )
          }
        }
      }
    }
  }
}

private fun buildAbilityText(
  ability: Ability,
  context: Context
): Pair<androidx.compose.ui.text.AnnotatedString, List<EffectInfo>> {
  val effectInfos = mutableListOf<EffectInfo>()

  // Get the raw description template
  val template = context.getString(ability.descriptionRes)

  // Process format args to find Translatable objects
  val processedArgs = ability.formatArgs.map { arg ->
    if (arg is Translatable) {
      // Create a placeholder that we'll replace with styled text
      EffectPlaceholder(
        name = context.getString(arg.nameRes),
        description = context.getString(arg.descriptionRes, *arg.formatArgs.toTypedArray()),
        isPositive = arg.isPositive
      )
    } else {
      arg
    }
  }.toTypedArray()

  // Format the string with placeholders
  val formattedText = template.format(*processedArgs)

  // Build annotated string
  val annotatedString = buildAnnotatedString {
    var textIndex = 0

    while (textIndex < formattedText.length) {
      // Check if we're at a placeholder
      val placeholder = processedArgs.filterIsInstance<EffectPlaceholder>()
        .firstOrNull { formattedText.startsWith(it.name, textIndex) }

      if (placeholder != null) {
        // Add styled effect name
        val startIdx = length
        val effectColor = if (placeholder.isPositive) Color(0xFF00D471) else Color(0xFFBD3BF5)

        withStyle(SpanStyle(color = effectColor, fontWeight = FontWeight.Bold)) {
          append(placeholder.name)
        }

        val endIdx = length
        effectInfos.add(
          EffectInfo(
            name = placeholder.name,
            description = placeholder.description,
            isPositive = placeholder.isPositive,
            startIndex = startIdx,
            endIndex = endIdx
          )
        )

        textIndex += placeholder.name.length
      } else {
        // Regular character
        append(formattedText[textIndex])
        textIndex++
      }
    }
  }

  return annotatedString to effectInfos
}

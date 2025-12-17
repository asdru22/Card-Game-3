package com.asdru.cardgame3.view.common

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.asdru.cardgame3.data.EffectInfo
import com.asdru.cardgame3.data.EffectPlaceholder
import com.asdru.cardgame3.data.Translatable
import kotlinx.coroutines.delay

@Composable
fun SmartDescriptionText(
  translatable: Translatable,
  textColor: Color = Color.LightGray
) {
  val context = LocalContext.current
  var popupControl by remember { mutableStateOf<Pair<String, Offset>?>(null) }
  val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

  // Auto-dismiss popup
  LaunchedEffect(popupControl) {
    if (popupControl != null) {
      delay(2000)
      popupControl = null
    }
  }

  Box {
    val (annotatedString, effectInfos) = remember(
      translatable,
      context
    ) {
      buildSmartText(translatable, context)
    }

    Text(
      text = annotatedString,
      color = textColor,
      fontSize = 12.sp,
      lineHeight = 15.sp,
      modifier = Modifier
        .pointerInput(effectInfos) {
          detectTapGestures { pos ->
            layoutResult.value?.let { layout ->
              val offset = layout.getOffsetForPosition(pos)
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

    // Popup Logic
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
            .widthIn(max = 250.dp)
            .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
        ) {
          Text(
            text = desc,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(8.dp)
          )
        }
      }
    }
  }
}

private fun buildSmartText(
  item: Translatable,
  context: Context
): Pair<androidx.compose.ui.text.AnnotatedString, List<EffectInfo>> {
  val effectInfos = mutableListOf<EffectInfo>()
  val template = context.getString(item.descriptionRes)

  val processedArgs = item.formatArgs.map { arg ->
    if (arg is Translatable) {
      EffectPlaceholder(
        name = context.getString(arg.nameRes),
        description = context.getString(arg.descriptionRes, *arg.formatArgs.toTypedArray()),
        isPositive = arg.isPositive
      )
    } else {
      arg
    }
  }.toTypedArray()

  val formattedText = template.format(*processedArgs)

  val annotatedString = buildAnnotatedString {
    var textIndex = 0
    while (textIndex < formattedText.length) {
      val placeholder = processedArgs.filterIsInstance<EffectPlaceholder>()
        .firstOrNull { formattedText.startsWith(it.name, textIndex) }

      if (placeholder != null) {
        val startIdx = length
        val effectColor = if (placeholder.isPositive) Color(0xFF00D471)
        else Color(0xFFBD3BF5)

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
        append(formattedText[textIndex])
        textIndex++
      }
    }
  }
  return annotatedString to effectInfos
}
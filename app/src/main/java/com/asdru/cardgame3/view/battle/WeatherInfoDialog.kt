package com.asdru.cardgame3.view.battle

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import com.asdru.cardgame3.data.EffectInfo
import com.asdru.cardgame3.data.EffectPlaceholder
import com.asdru.cardgame3.data.Translatable
import com.asdru.cardgame3.game.weather.WeatherEvent
import kotlinx.coroutines.delay

@Composable
fun WeatherInfoDialog(
  weather: WeatherEvent,
  onClose: () -> Unit
) {
  val context = LocalContext.current

  Dialog(
    onDismissRequest = onClose,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null
        ) { onClose() },
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth(0.8f)
          .heightIn(max = 1000.dp)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) {},
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = stringResource(id = weather.nameRes),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
              )
            }

            IconButton(onClick = onClose) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Gray
              )
            }
          }

          HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = Color.Gray.copy(alpha = 0.5f)
          )

          Column(
            modifier = Modifier
              .weight(1f, fill = false)
              .verticalScroll(rememberScrollState())
          ) {
            var popupControl by remember { mutableStateOf<Pair<String, Offset>?>(null) }
            val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

            LaunchedEffect(popupControl) {
              if (popupControl != null) {
                delay(2000)
                popupControl = null
              }
            }

            val (annotatedString, effectInfos) = remember(weather, context) {
              buildWeatherText(weather, context)
            }

            Box {
              Text(
                text = annotatedString,
                color = Color.LightGray,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                onTextLayout = { layoutResult.value = it },
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
                  }
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
                      fontSize = 14.sp,
                      lineHeight = 18.sp,
                      modifier = Modifier.padding(8.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

private fun buildWeatherText(
  weather: WeatherEvent,
  context: Context
): Pair<androidx.compose.ui.text.AnnotatedString, List<EffectInfo>> {
  val effectInfos = mutableListOf<EffectInfo>()
  val template = context.getString(weather.descriptionRes)

  val processedArgs = weather.formatArgs.map { arg ->
    if (arg is Translatable) {
      EffectPlaceholder(
        name = arg.getName(context),
        description = arg.getDescription(context),
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
        append(formattedText[textIndex])
        textIndex++
      }
    }
  }
  return annotatedString to effectInfos
}
package com.asdru.cardgame3.view.totem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.asdru.cardgame3.viewModel.TotemViewModel

@Composable
fun TotemView(
  totemVm: TotemViewModel?,
  canUseAbility: Boolean = false,
  onDoubleTap: (TotemViewModel) -> Unit,
  onDragStart: (TotemViewModel, Offset) -> Unit = { _, _ -> },
  onDrag: (Offset) -> Unit = {},
  onDragEnd: () -> Unit = {},
  onPositioned: (Rect) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(12.dp)

  if (totemVm != null && totemVm.isAlive) {
    Box(
      modifier = modifier
        .size(80.dp) // Increased size
        .clip(shape)
        .background(Color(0xFF424242))
        .then(if (canUseAbility) Modifier.border(2.dp, Color.White, shape) else Modifier)
        .onGloballyPositioned { coordinates ->
           onPositioned(coordinates.boundsInRoot())
        }
        .pointerInput(Unit) {
          detectTapGestures(
            onDoubleTap = { onDoubleTap(totemVm) }
          )
        }
        .pointerInput(Unit) {
          detectDragGestures(
            onDragStart = { offset -> onDragStart(totemVm, offset) },
            onDrag = { change, dragAmount ->
              change.consume()
              onDrag(dragAmount)
            },
            onDragEnd = { onDragEnd() },
            onDragCancel = { onDragEnd() }
          )
        },
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          painter = painterResource(id = totemVm.totem.iconRes),
          contentDescription = null,
          tint = Color.Unspecified,
          modifier = Modifier.size(48.dp) // Increased icon size
        )

        // Health Bar
        val hp = totemVm.currentHealth
        val maxHp = totemVm.maxHealth
        val hpPercent = (hp / maxHp).coerceIn(0f, 1f)

        val barColor = when {
          hpPercent > 0.5f -> Color(0xFF4CAF50)
          hpPercent > 0.2f -> Color(0xFFFFC107)
          else -> Color(0xFFF44336)
        }

        Box(
          modifier = Modifier
            .padding(top = 4.dp)
            .size(width = 60.dp, height = 6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color.Black.copy(alpha = 0.6f))
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(hpPercent)
              .fillMaxHeight()
              .background(barColor)
          )
        }
      }
    }
  } else {
    val stroke = Stroke(
      width = 4f,
      pathEffect = PathEffect.dashPathEffect(
        floatArrayOf(10f, 10f),
        0f
      )
    )

    Box(
      modifier = modifier
        .size(80.dp)
        .clip(shape)
        .background(Color(0xFF2C2C2C).copy(alpha = 0.5f))
        .drawBehind {
          drawRoundRect(
            color = Color.Gray,
            style = stroke,
            cornerRadius = CornerRadius(12.dp.toPx())
          )
        },
      contentAlignment = Alignment.Center
    ) {
      // Empty
    }
  }
}

package com.asdru.cardgame3.view.character

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


@Composable
fun RadarCard(viewModel: EntityViewModel, onClick: () -> Unit) {
  Surface(
    modifier = Modifier.fillMaxSize(),
    color = Color(0xFF1E1E1E),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Overlay Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        val nameComponent = @Composable {
          Text(
            text = stringResource(viewModel.name),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
        }

        val closeComponent = @Composable {
          IconButton(
            onClick = onClick,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Stats",
              tint = Color.White
            )
          }
        }

        if (viewModel.isLeftTeam) {
          // Left Player: Close button on Left
          closeComponent()
          nameComponent()
        } else {
          // Right Player: Close button on Right
          nameComponent()
          closeComponent()
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // The Chart
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(0.8f),
        contentAlignment = Alignment.Center
      ) {
        RadarChart(
          stats = viewModel.entity.radarStats,
          color = viewModel.color,
          modifier = Modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(1f)
        )
      }
    }
  }
}

@Composable
fun RadarChart(
  stats: RadarStats,
  color: Color,
  modifier: Modifier = Modifier
) {
  val labels = listOf("DMG", "SURV", "SUP", "CTRL", "CMPLX")
  val values = listOf(
    stats.damage,
    stats.survivability,
    stats.support,
    stats.control,
    stats.complexity
  )

  var animationTriggered by remember { mutableStateOf(false) }
  val progress by animateFloatAsState(
    targetValue = if (animationTriggered) 1f else 0f,
    animationSpec = tween(
      durationMillis = 1000,
      delayMillis = 100,
      easing = FastOutSlowInEasing
    ),
    label = "RadarAnimation"
  )

  LaunchedEffect(Unit) {
    animationTriggered = true
  }

  @Composable
  fun GraphCanvas(
    graphRadius: Float,
    center: Offset,
    angleStep: Float
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val gridColor = Color.Gray.copy(alpha = 0.3f)

      for (i in 1..4) {
        val r = graphRadius * (i / 5f)
        drawPath(
          path = createPolygonPath(center, r, 5, angleStep),
          color = gridColor,
          style = Stroke(width = 1.dp.toPx())
        )
      }

      drawPath(
        path = createPolygonPath(center, graphRadius, 5, angleStep),
        color = Color.Gray,
        style = Stroke(width = 2.dp.toPx())
      )

      for (i in 0 until 5) {
        val angle = i * angleStep - Math.PI / 2
        val end = Offset(
          center.x + (graphRadius * cos(angle)).toFloat(),
          center.y + (graphRadius * sin(angle)).toFloat()
        )
        drawLine(
          color = gridColor,
          start = center,
          end = end,
          strokeWidth = 1.dp.toPx()
        )
      }

      val dataPath = Path()
      for (i in 0 until 5) {
        val angle = i * angleStep - Math.PI / 2

        val r = graphRadius * values[i].coerceIn(0f, 1f) * progress

        val point = Offset(
          center.x + (r * cos(angle)).toFloat(),
          center.y + (r * sin(angle)).toFloat()
        )
        if (i == 0) dataPath.moveTo(point.x, point.y)
        else dataPath.lineTo(point.x, point.y)
      }
      dataPath.close()

      // Fill
      drawPath(
        path = dataPath,
        color = color.copy(alpha = 0.5f),
        style = Fill
      )
      // Stroke
      drawPath(
        path = dataPath,
        color = color,
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
      )
    }
  }

  BoxWithConstraints(modifier = modifier) {
    val density = LocalDensity.current
    val center = Offset(constraints.maxWidth / 2f, constraints.maxHeight / 2f)

    val labelBuffer = with(density) { 30.dp.toPx() }
    val graphRadius = (min(constraints.maxWidth, constraints.maxHeight) / 2f) - labelBuffer
    val angleStep = (2 * Math.PI / 5).toFloat()

    GraphCanvas(graphRadius, center, angleStep)

    val textRadiusPadding = with(density) { 10.dp.toPx() }

    labels.forEachIndexed { index, label ->
      Text(
        text = label,
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.layout { measurable, constraints ->
          val placeable = measurable.measure(constraints)
          val angle = index * angleStep - Math.PI / 2

          val tipX = center.x + ((graphRadius + textRadiusPadding) * cos(angle)).toFloat()
          val tipY = center.y + ((graphRadius + textRadiusPadding) * sin(angle)).toFloat()

          val finalX = when (index) {
            0 -> tipX - (placeable.width / 2)
            1, 2 -> tipX
            3, 4 -> tipX - placeable.width
            else -> tipX
          }

          val finalY = when (index) {
            0 -> tipY - placeable.height
            1, 4 -> tipY - placeable.height
            2, 3 -> tipY
            else -> tipY
          }

          layout(placeable.width, placeable.height) {
            placeable.place(x = finalX.toInt(), y = finalY.toInt())
          }
        }
      )
    }
  }
}

private fun createPolygonPath(center: Offset, radius: Float, sides: Int, angleStep: Float): Path {
  val path = Path()
  for (i in 0 until sides) {
    val angle = i * angleStep - Math.PI / 2
    val x = center.x + (radius * cos(angle)).toFloat()
    val y = center.y + (radius * sin(angle)).toFloat()
    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
  }
  path.close()
  return path
}
package com.asdru.cardgame3.view.character

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.game.effect.StatusEffect
import com.asdru.cardgame3.game.trait.Trait
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun CharacterInfoCard(
  viewModel: EntityViewModel,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var showRadarGraph by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.6f))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {},
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .widthIn(max = 600.dp)
        .fillMaxWidth()
        .padding(16.dp),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
      elevation = CardDefaults.cardElevation(8.dp)
    ) {
      Box(modifier = Modifier.fillMaxWidth()) {

        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.Start
        ) {

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
          ) {

            IconButton(
              onClick = onClose,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
              text = stringResource(viewModel.name),
              color = Color.White,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Radar Button
            Surface(
              color = Color(0xFF2D2D2D),
              shape = RoundedCornerShape(50),
              modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable { showRadarGraph = true }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .padding(horizontal = 12.dp, vertical = 6.dp)
                  .height(IntrinsicSize.Min)
              ) {
                // Health
                Icon(
                  painter = painterResource(id = R.drawable.icon_health),
                  contentDescription = "Health",
                  tint = Color(0xFFEF5350),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "${viewModel.health.toInt()}/${viewModel.maxHealth.toInt()}",
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )

                VerticalDivider(
                  modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxHeight(0.7f),
                  color = Color.Gray.copy(alpha = 0.3f),
                  thickness = 1.dp
                )

                // Damage
                Icon(
                  painter = painterResource(id = R.drawable.icon_attack_damage),
                  contentDescription = "Damage",
                  tint = Color(0xFFFFCA28),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "${viewModel.damage.toInt()}",
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )

                VerticalDivider(
                  modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxHeight(0.7f),
                  color = Color.Gray.copy(alpha = 0.3f),
                  thickness = 1.dp
                )

                // Type
                DamageTypeChip(viewModel.damageType)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
          Spacer(modifier = Modifier.height(12.dp))

          // Abilities, Traits, Effects columns
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(
              modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .heightIn(max = 250.dp)
                .verticalScroll(rememberScrollState())
            ) {
              CharacterAbility(
                context = context,
                label = stringResource(R.string.ui_active),
                ability = viewModel.entity.activeAbility,
                color = Color(0xFF66BB6A),
              )
              CharacterAbility(
                context = context,
                label = stringResource(R.string.ui_passive),
                ability = viewModel.entity.passiveAbility,
                color = Color(0xFF42A5F5)
              )
              CharacterAbility(
                context = context,
                label = stringResource(R.string.ui_ultimate),
                ability = viewModel.entity.ultimateAbility,
                color = Color(0xFFE91E63)
              )
            }

            val hasEffects = viewModel.effectManager.effects.isNotEmpty()

            if (viewModel.traits.isNotEmpty()) {
              VerticalDivider(
                modifier = Modifier
                  .fillMaxHeight()
                  .padding(vertical = 4.dp),
                color = Color.Gray.copy(alpha = 0.2f),
                thickness = 1.dp
              )

              Column(
                modifier = Modifier
                  .weight(1f)
                  .padding(horizontal = 8.dp)
                  .heightIn(max = 250.dp)
                  .verticalScroll(rememberScrollState())
              ) {
                Text(
                  text = stringResource(R.string.ui_traits),
                  color = Color.Gray,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(bottom = 6.dp)
                )
                viewModel.traits.forEach {
                  Trait(it, context = context)
                }
              }
            }

            if (hasEffects) {
              VerticalDivider(
                modifier = Modifier
                  .fillMaxHeight()
                  .padding(vertical = 4.dp),
                color = Color.Gray.copy(alpha = 0.2f),
                thickness = 1.dp
              )

              Column(
                modifier = Modifier
                  .weight(1f)
                  .padding(start = 8.dp)
                  .heightIn(max = 250.dp)
                  .verticalScroll(rememberScrollState())
              ) {
                Text(
                  text = stringResource(R.string.ui_effects),
                  color = Color.Gray,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(bottom = 6.dp)
                )
                viewModel.effectManager.effects.forEach {
                  Effect(it, context)
                }
              }
            }
          }
        }

        if (showRadarGraph) {

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
                Text(
                  text = stringResource(viewModel.name),
                  color = Color.White,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold
                )
                IconButton(
                  onClick = { showRadarGraph = false },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Stats",
                    tint = Color.White
                  )
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
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f)
                )
              }
            }
          }
        }
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

  Box(modifier = modifier) {
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
      val center = Offset(size.width / 2, size.height / 2)
      val radius = min(size.width, size.height) / 2
      val angleStep = (2 * Math.PI / 5).toFloat()

      val gridColor = Color.Gray.copy(alpha = 0.3f)
      for (i in 1..4) {
        val r = radius * (i / 5f)
        drawPath(
          path = createPolygonPath(center, r, 5, angleStep),
          color = gridColor,
          style = Stroke(width = 1.dp.toPx())
        )
      }
      // Outer border
      drawPath(
        path = createPolygonPath(center, radius, 5, angleStep),
        color = Color.Gray,
        style = Stroke(width = 2.dp.toPx())
      )

      // Draw Axes
      for (i in 0 until 5) {
        val angle = i * angleStep - Math.PI / 2
        val end = Offset(
          center.x + (radius * cos(angle)).toFloat(),
          center.y + (radius * sin(angle)).toFloat()
        )
        drawLine(
          color = gridColor,
          start = center,
          end = end,
          strokeWidth = 1.dp.toPx()
        )
      }

      // Draw Data Polygon
      val dataPath = Path()
      for (i in 0 until 5) {
        val angle = i * angleStep - Math.PI / 2
        val r = radius * values[i].coerceIn(0f, 1f)
        val point = Offset(
          center.x + (r * cos(angle)).toFloat(),
          center.y + (r * sin(angle)).toFloat()
        )
        if (i == 0) dataPath.moveTo(point.x, point.y)
        else dataPath.lineTo(point.x, point.y)
      }
      dataPath.close()

      drawPath(
        path = dataPath,
        color = color.copy(alpha = 0.5f),
        style = Fill
      )
      drawPath(
        path = dataPath,
        color = color,
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
      )
    }


    LabelsOverlay(labels)
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

@Composable
fun LabelsOverlay(labels: List<String>) {
  Box(modifier = Modifier.fillMaxSize()) {
    // Top
    Text(labels[0], color = Color.White, fontSize = 10.sp, modifier = Modifier.align(Alignment.TopCenter))
    // Top Right
    Text(labels[1], color = Color.White, fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterEnd).padding(bottom = 60.dp))
    // Bottom Right
    Text(labels[2], color = Color.White, fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 20.dp, end = 20.dp))
    // Bottom Left
    Text(labels[3], color = Color.White, fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 20.dp, start = 20.dp))
    // Top Left
    Text(labels[4], color = Color.White, fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterStart).padding(bottom = 60.dp))
  }
}

@Composable
fun DamageTypeChip(
  damageType: DamageType,
) {
  Icon(
    painter = painterResource(damageType.iconResId),
    contentDescription = damageType.name,
    tint = damageType.tintColor,
    modifier = Modifier.size(20.dp)
  )
}

@Composable
fun Trait(trait: Trait, context: Context) {
  Column(modifier = Modifier.padding(bottom = 8.dp)) {
    Text(
      text = "• ${trait.getName(context)}",
      color = Color(0xFFFF9800),
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = trait.getDescription(context),
      color = Color.LightGray,
      fontSize = 11.sp,
      lineHeight = 13.sp,
      modifier = Modifier.padding(start = 8.dp)
    )
  }
}

@Composable
fun Effect(effect: StatusEffect, context: Context) {
  Column(
    modifier = Modifier.padding(bottom = 8.dp),
    horizontalAlignment = Alignment.Start
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        painter = painterResource(id = effect.iconRes),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = effect.getName(context),
        color = if (effect.isPositive) Color(0xFF00D471) else Color(0xFFBD3BF5),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "x${effect.duration}",
        color = Color.Gray,
        fontSize = 10.sp
      )
    }
    Text(
      text = effect.getDescription(context),
      color = Color.LightGray,
      fontSize = 11.sp,
      lineHeight = 13.sp
    )
  }
}
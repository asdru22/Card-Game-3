package com.asdru.cardgame3.view.character

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.asdru.cardgame3.viewModel.SummonViewModel

@Composable
fun SummonCard(
  viewModel: SummonViewModel,
  onDoubleTap: (SummonViewModel) -> Unit,
  modifier: Modifier = Modifier
) {
  val cardShape = RoundedCornerShape(12.dp)

  Card(
    shape = cardShape,
    modifier = modifier
      .fillMaxSize()
      .pointerInput(Unit) {
        detectTapGestures(
          onDoubleTap = { onDoubleTap(viewModel) }
        )
      },
    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
    elevation = CardDefaults.cardElevation(8.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top
    ) {
      Spacer(modifier = Modifier.height(12.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth(0.5f)
          .aspectRatio(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(Color.DarkGray),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(viewModel.iconRes),
          tint = if (viewModel.isAlive) viewModel.color else Color.Gray,
          contentDescription = null,
          modifier = Modifier
            .fillMaxSize()
            .padding(6.dp),
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Health Bar
      SummonStatsBar(viewModel)
    }
  }
}

@Composable
private fun SummonStatsBar(viewModel: SummonViewModel) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth()
  ) {
    val hp = viewModel.health
    val hpPercent = (hp / viewModel.maxHealth).coerceIn(0f, 1f)

    val barColor = when {
      hpPercent > 0.5f -> Color(0xFF4CAF50)
      hpPercent > 0.2f -> Color(0xFFFFC107)
      else -> Color(0xFFF44336)
    }

    // Health Bar Background
    Box(
      modifier = Modifier
        .fillMaxWidth(0.7f)
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp))
        .background(Color.DarkGray)
    ) {
      // Foreground
      Box(
        modifier = Modifier
          .fillMaxWidth(hpPercent)
          .fillMaxHeight()
          .background(barColor)
      )
    }
  }
}
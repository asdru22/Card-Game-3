package com.asdru.cardgame3.view.battle

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.R
import com.asdru.cardgame3.view.character.CharacterInfoCard
import com.asdru.cardgame3.viewModel.BattleViewModel
import kotlin.collections.get
import kotlin.math.roundToInt
import androidx.compose.runtime.key
import com.asdru.cardgame3.view.character.PopupView

@Composable
fun BattleScreen(viewModel: BattleViewModel) {
  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF1E1E1E))
  ) {
    val finalCardHeight = min(
      this.maxHeight / 3.4f,
      (this.maxWidth / 2.5f) / 0.7f
    )
    val finalCardWidth = finalCardHeight * 0.7f

    viewModel.dragState?.let { dragState ->
      val lineEnd =
        if (viewModel.hoveredTarget != null && viewModel.cardBounds.contains(viewModel.hoveredTarget)) {
          viewModel.cardBounds[viewModel.hoveredTarget]!!.center
        } else {
          dragState.current
        }
      LineCanvas(dragState.start, lineEnd, Color.White)
    }

    BattleLayout(viewModel, finalCardHeight, finalCardWidth)

    if (viewModel.currentWeather != null) {
      WeatherIcon(
        viewModel = viewModel,
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 16.dp)
          .size(56.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.6f))
          .border(2.dp, viewModel.currentWeather!!.color, CircleShape)
          .clickable { viewModel.showWeatherInfo = true },
      )
    }

    viewModel.cardBounds.forEach { (entity, rect) ->
      entity.popups.forEach { popup ->
        key(popup.id) {
          Box(
            modifier = Modifier
              .offset { IntOffset(rect.center.x.toInt(), rect.center.y.toInt()) }
          ) {
            PopupView(
              popup = popup,
              parentTranslation = Offset.Zero
            ) {
              entity.popups.remove(popup)
            }
          }
        }
      }
    }


    viewModel.ultimateDragState?.let { ultState ->
      val iconSize = 48.dp
      val density = LocalDensity.current
      val iconSizePx = with(density) { iconSize.toPx() }

      Box(
        modifier = Modifier
          .offset {
            IntOffset(
              x = (ultState.current.x - iconSizePx / 2).roundToInt(),
              y = (ultState.current.y - iconSizePx / 2).roundToInt()
            )
          }
          .size(iconSize)
          .clip(CircleShape)
          .background(Color.Red.copy(alpha = 0.8f))
          .border(2.dp, Color.Red, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(id = R.drawable.ultimate),
          contentDescription = "Dragging Ultimate",
          tint = Color.Black,
          modifier = Modifier.size(28.dp)
        )
      }
    }

    if (viewModel.showWeatherInfo && viewModel.currentWeather != null) {
      WeatherInfoDialog(
        weather = viewModel.currentWeather!!,
        onClose = { viewModel.showWeatherInfo = false }
      )
    }

    if (viewModel.showInfoDialog && viewModel.selectedEntity != null) {
      CharacterInfoCard(
        viewModel = viewModel.selectedEntity!!,
        onClose = { viewModel.closeInfoDialog() }
      )
    }

    if (viewModel.winner != null) {
      Winner(viewModel)
    }
  }
}

@Composable
fun WeatherIcon(modifier: Modifier = Modifier, viewModel: BattleViewModel) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    Icon(
      painter = painterResource(id = viewModel.currentWeather!!.iconRes),
      contentDescription = "Weather",
      tint = Color.Unspecified,
      modifier = Modifier.size(32.dp)
    )
  }
}

@Composable
fun Winner(viewModel: BattleViewModel) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.85f))
      .clickable(enabled = true) {},
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = stringResource(R.string.ui_winner, viewModel.winner!!),
        color = Color.Yellow,
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(16.dp))
      Button(
        onClick = { viewModel.onRestartClicked() },
        colors = ButtonDefaults.buttonColors(
          containerColor = Color.White,
          contentColor = Color.Black
        )
      ) {
        Text(
          stringResource(R.string.ui_restart),
        )
      }
    }
  }
}

@Composable
fun LineCanvas(dragStart: Offset, dragCurrent: Offset, color: Color) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    drawLine(
      color = color,
      start = dragStart,
      end = dragCurrent,
      strokeWidth = 8f
    )
  }
}
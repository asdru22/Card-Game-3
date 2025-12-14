package com.asdru.cardgame3.view.characterSelection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.R

@Composable
fun SelectionHeader(
  p1Name: String,
  p2Name: String,
  p1Color: Color = Color.Gray,
  p2Color: Color = Color.Gray,
  p1Subtitle: String? = null,
  p2Subtitle: String? = null,
  controls: @Composable () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 16.dp)
      .height(56.dp)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      controls()
    }

    PlayerInfoColumn(
      name = p1Name,
      color = p1Color,
      subtitle = p1Subtitle,
      alignment = Alignment.Start,
      modifier = Modifier.align(Alignment.CenterStart)
    )

    PlayerInfoColumn(
      name = p2Name,
      color = p2Color,
      subtitle = p2Subtitle,
      alignment = Alignment.End,
      modifier = Modifier.align(Alignment.CenterEnd)
    )
  }
}

@Composable
private fun PlayerInfoColumn(
  name: String,
  color: Color,
  subtitle: String?,
  alignment: Alignment.Horizontal,
  modifier: Modifier
) {
  Column(modifier = modifier, horizontalAlignment = alignment) {
    Text(
      text = name,
      color = color,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold
    )
    if (subtitle != null) {
      Text(text = subtitle, color = color, fontSize = 12.sp)
    }
  }
}

@Composable
fun GameSetupControls(
  onBack: () -> Unit,
  onStart: () -> Unit,
  canStart: Boolean,
  isWeatherMode: Boolean,
  onToggleWeather: () -> Unit,
  timerSeconds: Int,
  onToggleTimer: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier.weight(1f),
      contentAlignment = Alignment.CenterEnd
    ) {
      IconButton(onClick = onBack) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = Color.White
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
    }

    Button(
      onClick = onStart,
      enabled = canStart,
      colors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF4CAF50),
        disabledContainerColor = Color.DarkGray
      ),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.height(48.dp)
    ) {
      Text(
        text = stringResource(R.string.ui_start),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = if (canStart) Color.White else Color.Gray
      )
    }

    Box(
      modifier = Modifier.weight(1f),
      contentAlignment = Alignment.CenterStart
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(8.dp))

        // Weather
        IconButton(onClick = onToggleWeather) {
          Icon(
            painter = painterResource(id = R.drawable.icon_weather_mode),
            contentDescription = "Toggle Weather",
            tint = if (isWeatherMode) Color(0xFF2196F3) else Color.Gray,
            modifier = Modifier.size(24.dp)
          )
        }

        // Timer
        Button(
          onClick = onToggleTimer,
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          contentPadding = PaddingValues(0.dp)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val isActive = timerSeconds > 0
            val color = if (isActive) Color(0xFFFF9800) else Color.Gray
            Icon(
              painter = painterResource(id = R.drawable.icon_timer),
              contentDescription = "Timer",
              tint = color,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = if (isActive) "${timerSeconds}s" else "OFF",
              color = color,
              fontSize = 10.sp
            )
          }
        }
      }
    }
  }
}
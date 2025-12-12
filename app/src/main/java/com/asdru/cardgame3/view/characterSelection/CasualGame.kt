package com.asdru.cardgame3.view.characterSelection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.R
import com.asdru.cardgame3.game.entity.Entity
import kotlin.reflect.full.createInstance

@Composable
fun CharacterSelectionScreen(
  player1Name: String,
  player2Name: String,
  onBack: () -> Unit,
  onStartGame: (List<Entity>, List<Entity>, Boolean) -> Unit
) {
  val p1Team = remember { mutableStateListOf<Entity>() }
  val p2Team = remember { mutableStateListOf<Entity>() }
  var isWeatherMode by remember { mutableStateOf(false) }

  val availableCharacters = remember {
    Entity::class.sealedSubclasses.map { it.createInstance() }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF121212))
      .padding(16.dp)
  ) {
    // Top Row: P1 | Space | Back | Start | Weather | Space | P2
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = player1Name,
        color = Color.Gray,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.weight(1f))

      // Center Cluster
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Back Button
        IconButton(onClick = onBack) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }

        // Start Button
        Button(
          onClick = {
            onStartGame(p1Team.toList(), p2Team.toList(), isWeatherMode)
          },
          enabled = p1Team.size == 3 && p2Team.size == 3,
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
            color = if (p1Team.size == 3 && p2Team.size == 3) Color.White else Color.Gray
          )
        }

        // Weather Toggle Button
        IconButton(
          onClick = { isWeatherMode = !isWeatherMode }
        ) {
          Icon(
            painter = painterResource(id = R.drawable.icon_weather_mode),
            contentDescription = "Toggle Weather",
            tint = if (isWeatherMode) Color(0xFF2196F3) else Color.Gray,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Spacer(modifier = Modifier.weight(1f))

      Text(
        text = player2Name,
        color = Color.Gray,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Row(
      modifier = Modifier.fillMaxSize(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(modifier = Modifier.weight(1f)) {
        PlayerGridSection(
          team = p1Team,
          available = availableCharacters,
        )
      }

      VerticalDivider(
        modifier = Modifier.fillMaxHeight(),
        color = Color.Gray.copy(alpha = 0.3f),
        thickness = 1.dp
      )

      Box(modifier = Modifier.weight(1f)) {
        PlayerGridSection(
          team = p2Team,
          available = availableCharacters,
        )
      }
    }
  }
}
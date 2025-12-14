package com.asdru.cardgame3.view.characterSelection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.asdru.cardgame3.game.entity.Entity
import kotlin.reflect.full.createInstance

@Composable
fun CharacterSelectionScreen(
  player1Name: String,
  player2Name: String,
  onBack: () -> Unit,
  onStartGame: (List<Entity>, List<Entity>, Boolean, Int) -> Unit
) {
  val p1Team = remember { mutableStateListOf<Entity>() }
  val p2Team = remember { mutableStateListOf<Entity>() }
  var isWeatherMode by remember { mutableStateOf(false) }
  var timerSeconds by remember { mutableIntStateOf(0) }

  val availableCharacters = remember {
    Entity::class.sealedSubclasses.map { it.createInstance() }
  }

  val canStart = p1Team.size == 3 && p2Team.size == 3

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF121212))
      .padding(16.dp)
  ) {
    // Modular Header
    SelectionHeader(
      p1Name = player1Name,
      p2Name = player2Name,
      controls = {
        GameSetupControls(
          onBack = onBack,
          onStart = { onStartGame(p1Team, p2Team, isWeatherMode, timerSeconds) },
          canStart = canStart,
          isWeatherMode = isWeatherMode,
          onToggleWeather = { isWeatherMode = !isWeatherMode },
          timerSeconds = timerSeconds,
          onToggleTimer = {
            timerSeconds = when (timerSeconds) {
              0 -> 10
              10 -> 30
              30 -> 60
              else -> 0
            }
          }
        )
      }
    )

    Row(
      modifier = Modifier.fillMaxSize(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(modifier = Modifier.weight(1f)) {
        PlayerGridSection(
          team = p1Team,
          available = availableCharacters,
          isLeft = true
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
          isLeft = false
        )
      }
    }
  }
}
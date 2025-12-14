package com.asdru.cardgame3.view.characterSelection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.asdru.cardgame3.R
import com.asdru.cardgame3.game.entity.Entity
import com.asdru.cardgame3.view.character.CharacterInfoCard
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.random.Random
import kotlin.reflect.full.createInstance

@Composable
fun StrategicSelectionScreen(
  player1Name: String,
  player2Name: String,
  onBack: () -> Unit,
  onStartGame: (List<Entity>, List<Entity>, Boolean, Int) -> Unit
) {
  val p1Team = remember { mutableStateListOf<Entity>() }
  val p2Team = remember { mutableStateListOf<Entity>() }
  var isP1Turn by remember { mutableStateOf(Random.nextBoolean()) }
  var infoCharacter by remember { mutableStateOf<Entity?>(null) }
  var isWeatherMode by remember { mutableStateOf(false) }
  var timerSeconds by remember { mutableIntStateOf(0) }

  val availableCharacters = remember {
    Entity::class.sealedSubclasses.map { it.createInstance() }
  }

  val p1Color = Color(0xFF4CAF50)
  val p2Color = Color(0xFFE53935)
  val canStart = p1Team.size == 3 && p2Team.size == 3

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF121212))
      .padding(16.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      SelectionHeader(
        p1Name = player1Name,
        p2Name = player2Name,
        p1Color = if (isP1Turn) p1Color else Color.Gray,
        p2Color = if (!isP1Turn) p2Color else Color.Gray,
        p1Subtitle = if (isP1Turn) stringResource(R.string.ui_choose_card) else null,
        p2Subtitle = if (!isP1Turn) stringResource(R.string.ui_choose_card) else null,
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


      LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.weight(1f)
      ) {
        items(availableCharacters) { entity ->
          val isTakenByP1 = p1Team.any { it::class == entity::class }
          val isTakenByP2 = p2Team.any { it::class == entity::class }
          val isSelected = isTakenByP1 || isTakenByP2

          CharacterGridItem(
            entity = entity,
            isSelected = isSelected,
            activeColor = if (isTakenByP1) p1Color else if (isTakenByP2) p2Color else Color.White,
            onSelect = {
              if (!isSelected && !canStart) {
                if (isP1Turn) p1Team.add(entity) else p2Team.add(entity)
                isP1Turn = !isP1Turn
              }
            },
            onInfo = { infoCharacter = entity }
          )
        }
      }
    }

    // Popups
    AnimatedVisibility(
      visible = infoCharacter != null,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.Center)
    ) {
      infoCharacter?.let { entity ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          val tempViewModel = remember(entity, isP1Turn) {
            EntityViewModel(entity, isP1Turn)
          }

          CharacterInfoCard(
            viewModel = tempViewModel,
            onClose = { infoCharacter = null }
          )
        }
      }
    }
  }
}
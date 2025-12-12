package com.asdru.cardgame3.view.mainMenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.asdru.cardgame3.entity.Entity
import com.asdru.cardgame3.view.character.CharacterInfoCard
import com.asdru.cardgame3.view.characterSelection.CharacterGridItem
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.random.Random
import kotlin.reflect.full.createInstance

@Composable
fun StrategicSelectionScreen(
  player1Name: String,
  player2Name: String,
  onBack: () -> Unit,
  onStartGame: (List<Entity>, List<Entity>, Boolean) -> Unit
) {
  val p1Team = remember { mutableStateListOf<Entity>() }
  val p2Team = remember { mutableStateListOf<Entity>() }
  var isP1Turn by remember { mutableStateOf(Random.nextBoolean()) }
  var infoCharacter by remember { mutableStateOf<Entity?>(null) }
  var isWeatherMode by remember { mutableStateOf(false) }

  val availableCharacters = remember {
    Entity::class.sealedSubclasses.map { it.createInstance() }
  }

  val p1Color = Color(0xFF4CAF50)
  val p2Color = Color(0xFFE53935)

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
      // Top Section Layout
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp)
      ) {
        // Player 1 Info (Left)
        Column(
          modifier = Modifier.align(Alignment.CenterStart),
          horizontalAlignment = Alignment.Start
        ) {
          Text(
            text = player1Name,
            color = if (isP1Turn) p1Color else Color.Gray,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
          if (isP1Turn) {
            Text(
              text = stringResource(R.string.ui_choose_card),
              color = p1Color,
              fontSize = 12.sp
            )
          }
        }

        Row(
          modifier = Modifier.align(Alignment.Center),
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

        // Player 2 Info (Right)
        Column(
          modifier = Modifier.align(Alignment.CenterEnd),
          horizontalAlignment = Alignment.End
        ) {
          Text(
            text = player2Name,
            color = if (!isP1Turn) p2Color else Color.Gray,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
          if (!isP1Turn) {
            Text(
              text = stringResource(R.string.ui_choose_card),
              color = p2Color,
              fontSize = 12.sp
            )
          }
        }
      }

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
              if (!isSelected) {
                // Prevent picking if teams are full
                if (p1Team.size == 3 && p2Team.size == 3) return@CharacterGridItem

                if (isP1Turn) {
                  p1Team.add(entity)
                } else {
                  p2Team.add(entity)
                }
                isP1Turn = !isP1Turn
              }
            },
            onInfo = { infoCharacter = entity }
          )
        }
      }
    }

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
          val tempViewModel = remember(entity) { EntityViewModel(entity) }

          CharacterInfoCard(
            viewModel = tempViewModel,
            onClose = { infoCharacter = null }
          )
        }
      }
    }
  }
}
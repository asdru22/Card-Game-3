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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
  onStartGame: (List<Entity>, List<Entity>) -> Unit
) {
  val p1Team = remember { mutableStateListOf<Entity>() }
  val p2Team = remember { mutableStateListOf<Entity>() }
  var isP1Turn by remember { mutableStateOf(Random.nextBoolean()) }
  var infoCharacter by remember { mutableStateOf<Entity?>(null) }

  val availableCharacters = remember {
    Entity::class.sealedSubclasses.map { it.createInstance() }
  }

  val p1Color = Color(0xFF4CAF50)
  val p2Color = Color(0xFFE53935)

  LaunchedEffect(p1Team.size, p2Team.size) {
    if (p1Team.size == 3 && p2Team.size == 3) {
      onStartGame(p1Team.toList(), p2Team.toList())
    }
  }

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
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = null,
          tint = if (isP1Turn) p1Color else Color.Transparent,
          modifier = Modifier
            .size(48.dp)
            .padding(end = 16.dp)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = if (isP1Turn) player1Name else player2Name,
            color = if (isP1Turn) p1Color else p2Color,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = stringResource(R.string.ui_choose_card),
            color = Color.Gray,
            fontSize = 14.sp
          )
        }

        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          tint = if (!isP1Turn) p2Color else Color.Transparent,
          modifier = Modifier
            .size(48.dp)
            .padding(start = 16.dp)
        )
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

          CharacterInfoCard(viewModel = tempViewModel)

          IconButton(
            onClick = { infoCharacter = null },
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Info",
              tint = Color.White
            )
          }
        }
      }
    }
  }
}
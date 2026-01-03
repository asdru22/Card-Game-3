package com.asdru.cardgame3.view.characterSelection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.RadarStats
import com.asdru.cardgame3.game.entity.Entity
import com.asdru.cardgame3.view.character.CharacterInfoCard
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.random.Random
import kotlin.reflect.full.createInstance

private enum class SelectionPhase {
  BANNING,
  PICKING
}

private enum class SortOption(val labelRes: Int, val selector: (RadarStats) -> Float) {
  DAMAGE(R.string.ui_radar_damage, { it.damage }),
  SURVIVABILITY(R.string.ui_radar_survivability, { it.survivability }),
  SUPPORT(R.string.ui_radar_support, { it.support }),
  CONTROL(R.string.ui_radar_control, { it.control }),
  COMPLEXITY(R.string.ui_radar_complexity, { it.complexity })
}

private val BANS_PER_PLAYER = 2

@Composable
fun StrategicSelectionScreen(
  player1Name: String,
  player2Name: String,
  onBack: () -> Unit,
  onStartGame: (List<Entity>, List<Entity>, Boolean, Boolean, Int) -> Unit
) {
  val p1Team = remember { mutableStateListOf<Entity>() }
  val p2Team = remember { mutableStateListOf<Entity>() }
  val bannedEntities = remember { mutableStateListOf<Entity>() }
  var phase by remember { mutableStateOf(SelectionPhase.BANNING) }
  var isP1Turn by remember { mutableStateOf(Random.nextBoolean()) }
  var infoCharacter by remember { mutableStateOf<Entity?>(null) }
  var isWeatherMode by remember { mutableStateOf(false) }
  var isMysteryMode by remember { mutableStateOf(true) }
  var timerSeconds by remember { mutableIntStateOf(0) }
  var sortOption by remember { mutableStateOf<SortOption?>(null) }

  val allCharacters = remember {
    Entity::class.sealedSubclasses.map { it.createInstance() }
  }

  // Derived state for sorted characters
  val displayedCharacters = remember(sortOption) {
    if (sortOption == null) {
      allCharacters
    } else {
      allCharacters.sortedByDescending { entity ->
        sortOption!!.selector(entity.radarStats)
      }
    }
  }

  val p1Color = Color(0xFF4CAF50)
  val p2Color = Color(0xFFE53935)

  val canStart = p1Team.size == 3 && p2Team.size == 3

  val gridState = rememberLazyGridState()

  // Scroll to top when sort option changes
  LaunchedEffect(sortOption) {
    gridState.animateScrollToItem(0)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF121212))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      val subtitle = if (phase == SelectionPhase.BANNING) {
        stringResource(R.string.ui_ban_card)
      } else {
        stringResource(R.string.ui_choose_card)
      }

      SelectionHeader(
        p1Name = player1Name,
        p2Name = player2Name,
        p1Color = if (canStart) Color.White else if (isP1Turn) p1Color else Color.Gray,
        p2Color = if (canStart) Color.White else if (!isP1Turn) p2Color else Color.Gray,
        p1Subtitle = if (canStart) null else if (isP1Turn) subtitle else null,
        p2Subtitle = if (canStart) null else if (!isP1Turn) subtitle else null,
        controls = {
          val buttonText = if (canStart) {
            stringResource(R.string.ui_start)
          } else if (phase == SelectionPhase.BANNING) {
            stringResource(R.string.ui_ban_turn)
          } else {
            stringResource(R.string.ui_pick_turn)
          }

          StrategicGameSetupControls(
            onBack = onBack,
            onStart = { onStartGame(p1Team, p2Team, isWeatherMode, isMysteryMode, timerSeconds) },
            canStart = canStart,
            isWeatherMode = isWeatherMode,
            onToggleWeather = { isWeatherMode = !isWeatherMode },
            isMysteryMode = isMysteryMode,
            onToggleMystery = { isMysteryMode = !isMysteryMode },
            timerSeconds = timerSeconds,
            onToggleTimer = {
              timerSeconds = when (timerSeconds) {
                0 -> 10
                10 -> 30
                30 -> 60
                else -> 0
              }
            },
            buttonText = buttonText,
            sortButtons = {
              Row(
                modifier = Modifier.padding(start = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                SortOption.entries.forEach { option ->
                  SortButton(
                    option = option,
                    isSelected = sortOption == option,
                    onClick = { sortOption = if (sortOption == option) null else option }
                  )
                }
              }
            }
          )
        }
      )

      LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        state = gridState,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.weight(1f)
      ) {
        items(
          items = displayedCharacters,
          key = { it::class.qualifiedName ?: it.toString() }
        ) { entity ->
          val isTakenByP1 = p1Team.any { it::class == entity::class }
          val isTakenByP2 = p2Team.any { it::class == entity::class }
          val isBanned = bannedEntities.any { it::class == entity::class }
          val isSelected = isTakenByP1 || isTakenByP2

          Box(modifier = Modifier.animateItem()) {
            CharacterGridItem(
              entity = entity,
              isSelected = isSelected,
              isBanned = isBanned,
              activeColor = if (isTakenByP1) p1Color else if (isTakenByP2) p2Color else Color.White,
              onSelect = {
                if (phase == SelectionPhase.BANNING) {
                  if (!isBanned) {
                    bannedEntities.add(entity)
                    isP1Turn = !isP1Turn
                    if (bannedEntities.size >= BANS_PER_PLAYER * 2) {
                      phase = SelectionPhase.PICKING
                    }
                  }
                } else {
                  if (!isSelected && !isBanned && !canStart) {
                    if (isP1Turn) p1Team.add(entity) else p2Team.add(entity)
                    isP1Turn = !isP1Turn
                  }
                }
              },
              onInfo = { infoCharacter = entity }
            )
          }
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
            .background(Color.Black.copy(alpha = 0.8f)),
          contentAlignment = Alignment.Center
        ) {
          val tempViewModel = remember(entity, isP1Turn) {
            EntityViewModel(entity, isP1Turn)
          }

          CharacterInfoCard(
            viewModel = tempViewModel,
            onClose = { infoCharacter = null },
            showAlternates = true
          )
        }
      }
    }
  }
}

@Composable
private fun SortButton(
  option: SortOption,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.1f))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Text(
      text = stringResource(option.labelRes),
      color = if (isSelected) Color.Black else Color.White,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold
    )
  }
}
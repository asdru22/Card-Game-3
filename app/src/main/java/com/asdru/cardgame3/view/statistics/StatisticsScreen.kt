package com.asdru.cardgame3.view.statistics

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.entity.CharacterStats
import com.asdru.cardgame3.data.entity.Player
import com.asdru.cardgame3.viewModel.PlayerViewModel
import com.asdru.cardgame3.viewModel.StatisticsViewModel

@Composable
fun LeaderboardScreen(
  playerViewModel: PlayerViewModel,
  statisticsViewModel: com.asdru.cardgame3.viewModel.StatisticsViewModel,
  onBack: () -> Unit
) {
  val players by playerViewModel.players.collectAsState()
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  val tabs = listOf(R.string.ui_wins, R.string.ui_card_picks)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF121212))
  ) {
    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      // Top Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF1E1E1E))
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }
        Text(
          text = stringResource(R.string.ui_statistics),
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          modifier = Modifier.padding(start = 16.dp)
        )
      }
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF1E1E1E))
      ) {
        PrimaryTabRow(
          selectedTabIndex = selectedTabIndex,
          containerColor = Color.Transparent,
          contentColor = Color.White,
          indicator = {
            TabRowDefaults.PrimaryIndicator(
              modifier = Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = false),
              color = Color(0xFFFFA000),
              width = Dp.Unspecified
            )
          }
        ) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTabIndex == index,
              onClick = { selectedTabIndex = index },
              text = { Text(stringResource(title)) }
            )
          }
        }
      }

      when (selectedTabIndex) {
        0 -> WinsList(players)
        1 -> PickRatesView(statisticsViewModel)
      }
    }
  }
}

@Composable
fun WinsList(players: List<Player>) {
  val sortedPlayers = remember(players) { players.sortedByDescending { it.wins } }

  if (sortedPlayers.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("No players found.", color = Color.Gray)
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(1.dp) // Divider effect
    ) {
      itemsIndexed(sortedPlayers) { index, player ->
        PlayerRow(index + 1, player)
        HorizontalDivider(color = Color(0xFF333333))
      }
    }
  }
}

@Composable
fun PlayerRow(rank: Int, player: Player) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF1E1E1E))
      .padding(start = 64.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = "$rank.",
        color = Color(0xFFFFA000), // Gold color for rank
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(end = 16.dp)
      )
      Text(
        text = player.name,
        color = Color.White,
        fontSize = 18.sp
      )
    }
    Text(
      text = stringResource(R.string.ui_player_wins, player.wins),
      color = Color.LightGray,
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun PickRatesView(viewModel: StatisticsViewModel) {
  val pickRates by viewModel.pickRates.collectAsState()

  if (pickRates.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("No pick data available.", color = Color.Gray)
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
      itemsIndexed(pickRates) { index, stats ->
        PickRateRow(index + 1, stats)
        HorizontalDivider(color = Color(0xFF333333))
      }
    }
  }
}

@Composable
fun PickRateRow(rank: Int, stats: CharacterStats) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF1E1E1E))
      .padding(start = 64.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = stringResource(R.string.ui_card_rank, rank),
        color = Color(0xFFFFA000),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(end = 16.dp)
      )

      val context = LocalContext.current

      val resId = remember(stats.characterName) {
        val snakeCaseName = camelToSnakeCase(stats.characterName)
        val resourceName = "entity_$snakeCaseName"
        context.run { resources.getIdentifier(resourceName, "string", packageName) }
      }
      val displayName = if (resId != 0) {
        stringResource(resId)
      } else {
        stats.characterName
      }

      Text(
        text = displayName,
        color = Color.White,
        fontSize = 18.sp
      )
    }
    Text(
      text = stringResource(R.string.ui_card_pick,stats.pickCount),
      color = Color.LightGray,
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

fun camelToSnakeCase(str: String): String {
  return str.fold(StringBuilder()) { acc, c ->
    if (c.isUpperCase()) {
      if (acc.isNotEmpty()) acc.append('_')
      acc.append(c.lowercaseChar())
    } else {
      acc.append(c)
    }
  }.toString()
}

package com.asdru.cardgame3.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.asdru.cardgame3.data.AppScreen
import com.asdru.cardgame3.data.Team
import com.asdru.cardgame3.game.entity.Entity
import com.asdru.cardgame3.view.characterSelection.CharacterSelectionScreen
import com.asdru.cardgame3.view.characterSelection.StrategicSelectionScreen
import com.asdru.cardgame3.view.mainMenu.MainMenuScreen
import com.asdru.cardgame3.view.statistics.LeaderboardScreen
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.PlayerViewModel
import com.asdru.cardgame3.viewModel.StatisticsViewModel
import com.asdru.cardgame3.viewModel.TeamViewModel

@Composable
fun CardGameApp(
  battleViewModel: BattleViewModel,
  playerViewModel: PlayerViewModel,
  statisticsViewModel: StatisticsViewModel,
  gameContent: @Composable () -> Unit
) {
  var currentScreen by remember { mutableStateOf(AppScreen.MENU) }
  var p1Name by remember { mutableStateOf("Player 1") }
  var p2Name by remember { mutableStateOf("Player 2") }
  var p1Id by remember { mutableStateOf<Long?>(null) }
  var p2Id by remember { mutableStateOf<Long?>(null) }

  LaunchedEffect(battleViewModel.navigateToSelection) {
    if (battleViewModel.navigateToSelection) {
      currentScreen = AppScreen.MENU
      battleViewModel.onNavigatedToSelection()
    }
  }

  fun navigateToSelection(targetScreen: AppScreen, name1: String, name2: String, id1: Long?, id2: Long?) {
    p1Name = name1.ifBlank { "Player 1" }
    p2Name = name2.ifBlank { "Player 2" }
    p1Id = id1
    p2Id = id2
    currentScreen = targetScreen
  }

  fun startGame(
    p1Entities: List<Entity>,
    p2Entities: List<Entity>,
    weatherEnabled: Boolean,
    turnTimer: Int
  ) {
    val p1Team = Team(p1Name, p1Entities, true, p1Id)
    val p2Team = Team(p2Name, p2Entities, false, p2Id)

    val leftTeamVM = TeamViewModel(p1Team)
    val rightTeamVM = TeamViewModel(p2Team)

    battleViewModel.startGame(leftTeamVM, rightTeamVM, weatherEnabled, turnTimer)
    currentScreen = AppScreen.GAME
  }

  when (currentScreen) {
    AppScreen.MENU -> {
      MainMenuScreen(
        playerViewModel = playerViewModel,
        onCasualGame = { n1, n2, id1, id2 ->
          navigateToSelection(AppScreen.SELECTION, n1, n2, id1, id2)
        },

        onStrategicGame = { n1, n2, id1, id2 ->
          navigateToSelection(AppScreen.STRATEGIC_SELECTION, n1, n2, id1, id2)
        },
        onLeaderboard = {
          currentScreen = AppScreen.LEADERBOARD
        }
      )
    }

    AppScreen.LEADERBOARD -> {
        LeaderboardScreen(
            playerViewModel = playerViewModel,
            statisticsViewModel = statisticsViewModel,
            onBack = { currentScreen = AppScreen.MENU }
        )
    }

    AppScreen.SELECTION -> {
      CharacterSelectionScreen(
        player1Name = p1Name,
        player2Name = p2Name,
        onBack = { currentScreen = AppScreen.MENU },
        onStartGame = { p1, p2, weather, timer ->
          startGame(p1, p2, weather, timer)
        }
      )
    }

    AppScreen.STRATEGIC_SELECTION -> {
      StrategicSelectionScreen(
        player1Name = p1Name,
        player2Name = p2Name,
        onBack = { currentScreen = AppScreen.MENU },
        // Updated to accept timer
        onStartGame = { p1, p2, weather, timer ->
          startGame(p1, p2, weather, timer)
        }
      )
    }

    AppScreen.GAME -> {
      gameContent()
    }
  }
}
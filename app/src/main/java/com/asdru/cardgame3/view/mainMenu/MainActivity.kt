package com.asdru.cardgame3.view.mainMenu

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.asdru.cardgame3.MainView
import com.asdru.cardgame3.entity.Entity
import com.asdru.cardgame3.data.Team
import com.asdru.cardgame3.ui.theme.CardGame3Theme
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.TeamViewModel

enum class AppScreen {
  MENU, SELECTION, STRATEGIC_SELECTION, GAME
}

class MainActivity : ComponentActivity() {

  private val battleViewModel: BattleViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    enableEdgeToEdge()
    window.attributes.layoutInDisplayCutoutMode =
      WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.systemBarsBehavior =
      WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

    val mainView = MainView(battleViewModel)

    setContent {
      CardGame3Theme {
        CardGameApp(
          battleViewModel = battleViewModel,
          gameContent = { mainView.Content() }
        )
      }
    }
  }
}

@Composable
fun CardGameApp(
  battleViewModel: BattleViewModel,
  gameContent: @Composable () -> Unit
) {
  var currentScreen by remember { mutableStateOf(AppScreen.MENU) }
  var p1Name by remember { mutableStateOf("Player 1") }
  var p2Name by remember { mutableStateOf("Player 2") }

  LaunchedEffect(battleViewModel.navigateToSelection) {
    if (battleViewModel.navigateToSelection) {
      currentScreen = AppScreen.MENU
      battleViewModel.onNavigatedToSelection()
    }
  }

  fun navigateToSelection(targetScreen: AppScreen, name1: String, name2: String) {
    p1Name = name1.ifBlank { "Player 1" }
    p2Name = name2.ifBlank { "Player 2" }
    currentScreen = targetScreen
  }

  fun startGame(
    p1Entities: List<Entity>,
    p2Entities: List<Entity>
  ) {
    val p1Team = Team(p1Name, p1Entities)
    val p2Team = Team(p2Name, p2Entities)

    val leftTeamVM = TeamViewModel(p1Team)
    val rightTeamVM = TeamViewModel(p2Team)

    battleViewModel.startGame(leftTeamVM, rightTeamVM)
    currentScreen = AppScreen.GAME
  }

  when (currentScreen) {
    AppScreen.MENU -> {
      MainMenuScreen(
        onCasualGame = { n1, n2 ->
          navigateToSelection(AppScreen.SELECTION, n1, n2)
        },

        onStrategicGame = { n1, n2 ->
          navigateToSelection(AppScreen.STRATEGIC_SELECTION, n1, n2)
        }
      )
    }

    AppScreen.SELECTION -> {
      CharacterSelectionScreen(
        player1Name = p1Name,
        player2Name = p2Name,
        onStartGame = { p1, p2 -> startGame(p1, p2) }
      )
    }

    AppScreen.STRATEGIC_SELECTION -> {
      StrategicSelectionScreen(
        player1Name = p1Name,
        player2Name = p2Name,
        onStartGame = { p1, p2 -> startGame(p1, p2) }
      )
    }

    AppScreen.GAME -> {
      gameContent()
    }
  }
}
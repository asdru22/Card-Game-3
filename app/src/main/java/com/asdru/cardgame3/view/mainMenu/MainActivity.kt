package com.asdru.cardgame3.view.mainMenu

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.asdru.cardgame3.MainView
import com.asdru.cardgame3.entityFeatures.Team
import com.asdru.cardgame3.ui.theme.CardGame3Theme
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.EntityViewModel

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
    val windowInsetsController =
      WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.systemBarsBehavior =
      WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

    val mainView = MainView(battleViewModel)

    setContent {
      CardGame3Theme {
        var currentScreen by remember { mutableStateOf(AppScreen.MENU) }
        var p1Name by remember { mutableStateOf("") }
        var p2Name by remember { mutableStateOf("") }

        LaunchedEffect(battleViewModel.navigateToSelection) {
          if (battleViewModel.navigateToSelection) {
            currentScreen = AppScreen.MENU
            battleViewModel.onNavigatedToSelection()
          }
        }

        when (currentScreen) {
          AppScreen.MENU -> {
            MainMenuScreen(
              onCasualGame = { name1, name2 ->
                p1Name = name1
                p2Name = name2
                currentScreen = AppScreen.SELECTION
              },
              onStrategicGame = {
                p1Name = p1Name.ifBlank { "Player 1" }
                p2Name = p2Name.ifBlank { "Player 2" }
                currentScreen = AppScreen.STRATEGIC_SELECTION
              }
            )
          }
          AppScreen.SELECTION -> {
            CharacterSelectionScreen(
              player1Name = p1Name,
              player2Name = p2Name,
              onStartGame = { p1Entities, p2Entities ->
                val leftTeam = Team(p1Name, p1Entities.map { EntityViewModel(it) })
                val rightTeam = Team(p2Name, p2Entities.map { EntityViewModel(it) })

                battleViewModel.startGame(leftTeam, rightTeam)
                currentScreen = AppScreen.GAME
              }
            )
          }
          AppScreen.STRATEGIC_SELECTION -> {
            StrategicSelectionScreen(
              player1Name = p1Name,
              player2Name = p2Name,
              onStartGame = { p1Entities, p2Entities ->
                val leftTeam = Team(p1Name, p1Entities.map { EntityViewModel(it) })
                val rightTeam = Team(p2Name, p2Entities.map { EntityViewModel(it) })

                battleViewModel.startGame(leftTeam, rightTeam)
                currentScreen = AppScreen.GAME
              }
            )
          }
          AppScreen.GAME -> {
            mainView.Content()
          }
        }
      }
    }
  }
}
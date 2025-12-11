package com.asdru.cardgame3

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
import com.asdru.cardgame3.entityFeatures.Team
import com.asdru.cardgame3.ui.theme.CardGame3Theme
import com.asdru.cardgame3.view.characterSelection.CharacterSelectionScreen
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.EntityViewModel

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
        var isBattleStarted by remember { mutableStateOf(false) }

        LaunchedEffect(battleViewModel.navigateToSelection) {
          if (battleViewModel.navigateToSelection) {
            isBattleStarted = false
            battleViewModel.onNavigatedToSelection()
          }
        }

        if (!isBattleStarted) {
          CharacterSelectionScreen(
            onStartGame = { p1Name, p1Entities, p2Name, p2Entities ->
              val leftTeam = Team(p1Name, p1Entities.map { EntityViewModel(it) })
              val rightTeam = Team(p2Name, p2Entities.map { EntityViewModel(it) })

              battleViewModel.startGame(leftTeam, rightTeam)

              isBattleStarted = true
            }
          )
        } else {
          mainView.Content()
        }
      }
    }
  }
}
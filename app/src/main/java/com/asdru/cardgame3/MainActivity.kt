package com.asdru.cardgame3

import ForceDefaultSystemSettings
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.asdru.cardgame3.data.database.AppDatabase
import com.asdru.cardgame3.data.repository.PlayerRepository
import com.asdru.cardgame3.ui.theme.CardGame3Theme
import com.asdru.cardgame3.view.CardGameApp
import com.asdru.cardgame3.view.battle.BattleScreen
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.BattleViewModelFactory
import com.asdru.cardgame3.viewModel.PlayerViewModel
import com.asdru.cardgame3.viewModel.PlayerViewModelFactory
import com.asdru.cardgame3.viewModel.StatisticsViewModel
import com.asdru.cardgame3.viewModel.StatisticsViewModelFactory


class MainActivity : ComponentActivity() {

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

    val database = AppDatabase.getDatabase(applicationContext)
    val repository = PlayerRepository(database.playerDao())
    val charStatsRepository = com.asdru.cardgame3.data.repository.CharacterStatsRepository(database)

    val playerViewModelFactory = PlayerViewModelFactory(repository)
    val playerViewModel: PlayerViewModel by viewModels { playerViewModelFactory }

    val statisticsViewModelFactory =
      StatisticsViewModelFactory(charStatsRepository)
    val statisticsViewModel: StatisticsViewModel by viewModels { statisticsViewModelFactory }

    val battleViewModelFactory = BattleViewModelFactory(
      playerRepository = repository,
      characterStatsRepository = charStatsRepository,
      resourceResolver = { id -> resources.getResourceEntryName(id) }
    )
    val battleViewModel: BattleViewModel by viewModels { battleViewModelFactory }

    setContent {
      ForceDefaultSystemSettings {
        CardGame3Theme {
          CardGameApp(
            battleViewModel = battleViewModel,
            playerViewModel = playerViewModel,
            statisticsViewModel = statisticsViewModel,
            gameContent = { BattleScreen(battleViewModel) }
          )
        }
      }
    }
  }
}
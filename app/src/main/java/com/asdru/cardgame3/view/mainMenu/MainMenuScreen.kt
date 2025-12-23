package com.asdru.cardgame3.view.mainMenu

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.entity.Player
import com.asdru.cardgame3.viewModel.PlayerViewModel

@Composable
fun MainMenuScreen(
  playerViewModel: PlayerViewModel,
  onCasualGame: (String, String, Long?, Long?) -> Unit,
  onStrategicGame: (String, String, Long?, Long?) -> Unit,
  onLeaderboard: () -> Unit
) {
  val players by playerViewModel.players.collectAsState()

  var player1 by remember { mutableStateOf<Player?>(null) }
  var player2 by remember { mutableStateOf<Player?>(null) }

  var showRules by remember { mutableStateOf(false) }
  var showAddPlayerDialog by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      MenuHeader()

      PlayerSelectionSection(
        players = players,
        player1 = player1,
        player2 = player2,
        onPlayer1Selected = { player1 = it },
        onPlayer2Selected = { player2 = it }
      )

      Spacer(modifier = Modifier.height(32.dp))

      GameModeSelectionSection(
        player1 = player1,
        player2 = player2,
        onCasualGame = onCasualGame,
        onStrategicGame = onStrategicGame
      )

      Spacer(modifier = Modifier.height(24.dp))

      FooterActions(
        onAddPlayer = { showAddPlayerDialog = true },
        onLeaderboard = onLeaderboard,
        onRules = { showRules = true }
      )
    }

    // --- Corner Actions ---
    SocialLinks(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(16.dp),
      context = LocalContext.current
    )

    ShareButton(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp),
      context = LocalContext.current
    )

    // --- Overlays ---
    HowToPlayOverlay(
      visible = showRules,
      onClose = { showRules = false }
    )

    if (showAddPlayerDialog) {
      AddPlayerDialog(
        onDismiss = { showAddPlayerDialog = false },
        onAdd = { name ->
          playerViewModel.addPlayer(
            name = name,
            onSuccess = { showAddPlayerDialog = false },
            onError = {}
          )
        }
      )
    }
  }
}

// --- Components ---

@Composable
private fun MenuHeader() {
  Text(
    text = stringResource(R.string.ui_main_menu),
    fontSize = 48.sp,
    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.onBackground,
    modifier = Modifier.padding(bottom = 48.dp)
  )
}

@Composable
private fun PlayerSelectionSection(
  players: List<Player>,
  player1: Player?,
  player2: Player?,
  onPlayer1Selected: (Player) -> Unit,
  onPlayer2Selected: (Player) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(0.7f),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    PlayerDropdown(
      label = stringResource(R.string.ui_player_name, 1),
      players = players.filter { it.id != player2?.id },
      selectedPlayer = player1,
      onPlayerSelected = onPlayer1Selected,
      modifier = Modifier.weight(1f)
    )

    PlayerDropdown(
      label = stringResource(R.string.ui_player_name, 2),
      players = players.filter { it.id != player1?.id },
      selectedPlayer = player2,
      onPlayerSelected = onPlayer2Selected,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun GameModeSelectionSection(
  player1: Player?,
  player2: Player?,
  onCasualGame: (String, String, Long?, Long?) -> Unit,
  onStrategicGame: (String, String, Long?, Long?) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(0.7f),
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    MenuButton(
      text = stringResource(R.string.ui_casual_game),
      color = Color(0xFF4CAF50),
      onClick = {
        onCasualGame(
          player1?.name ?: "Player 1",
          player2?.name ?: "Player 2",
          player1?.id,
          player2?.id
        )
      },
      modifier = Modifier.weight(1f)
    )

    MenuButton(
      text = stringResource(R.string.ui_strategic_game),
      color = Color(0xFF1E88E5),
      onClick = {
        onStrategicGame(
          player1?.name ?: "Player 1",
          player2?.name ?: "Player 2",
          player1?.id,
          player2?.id
        )
      },
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun MenuButton(
  text: String,
  color: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Button(
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(containerColor = color),
    shape = RoundedCornerShape(8.dp),
    modifier = modifier.height(60.dp)
  ) {
    Text(
      text = text,
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )
  }
}

@Composable
private fun FooterActions(
  onAddPlayer: () -> Unit,
  onLeaderboard: () -> Unit,
  onRules: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(0.8f),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    FooterIconButton(
      iconRes = R.drawable.icon_add_player,
      contentDescription = "Add Player",
      containerColor = Color(0xFF2E7D32),
      onClick = onAddPlayer
    )

    Spacer(modifier = Modifier.size(16.dp))

    FooterIconButton(
      iconRes = R.drawable.icon_statistics,
      contentDescription = "Leaderboard",
      containerColor = Color(0xFFFFA000),
      onClick = onLeaderboard
    )

    Spacer(modifier = Modifier.size(16.dp))

    FooterIconButton(
      imageVector = Icons.AutoMirrored.Filled.Help,
      contentDescription = "How to play",
      containerColor = Color(0xFF757575),
      onClick = onRules
    )
  }
}

@Composable
private fun FooterIconButton(
  iconRes: Int? = null,
  imageVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
  contentDescription: String,
  containerColor: Color,
  onClick: () -> Unit
) {
  IconButton(
    onClick = onClick,
    colors = IconButtonDefaults.iconButtonColors(containerColor = containerColor),
    modifier = Modifier.background(
      containerColor,
      RoundedCornerShape(8.dp)
    )
  ) {
    if (iconRes != null) {
      Icon(
        painter = painterResource(iconRes),
        contentDescription = contentDescription,
        tint = Color.White
      )
    } else if (imageVector != null) {
      Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = Color.White
      )
    }
  }
}

@Composable
fun PlayerDropdown(
  label: String,
  players: List<Player>,
  selectedPlayer: Player?,
  onPlayerSelected: (Player) -> Unit,
  modifier: Modifier = Modifier
) {
  var expanded by remember { mutableStateOf(false) }

  Box(modifier = modifier) {
    OutlinedTextField(
      value = selectedPlayer?.name ?: "",
      onValueChange = {},
      label = { Text(label) },
      readOnly = true,
      trailingIcon = {
        Icon(
          imageVector = Icons.Default.ArrowDropDown,
          contentDescription = null
        )
      },
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
      ),
      modifier = Modifier
        .fillMaxWidth()
        .clickable { expanded = true }
    )

    Box(
      modifier = Modifier
        .matchParentSize()
        .clickable { expanded = true }
    )

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
      players.forEach { player ->
        DropdownMenuItem(
          text = {
            Text(
              player.name,
              color = MaterialTheme.colorScheme.onSurface
            )
          },
          onClick = {
            onPlayerSelected(player)
            expanded = false
          }
        )
      }
    }
  }
}

@Composable
fun AddPlayerDialog(
  onDismiss: () -> Unit,
  onAdd: (String) -> Unit
) {
  var name by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add New Player") },
    text = {
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Player Name") },
        singleLine = true
      )
    },
    confirmButton = {
      TextButton(
        onClick = {
          if (name.isNotBlank()) onAdd(name)
        }
      ) {
        Text("Add")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun SocialLinks(modifier: Modifier = Modifier, context: Context) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    IconButton(
      onClick = {
        val intent = Intent(Intent.ACTION_VIEW, "https://github.com/asdru22/Card-Game-3".toUri())
        context.startActivity(intent)
      },
      modifier = Modifier.size(48.dp)
    ) {
      Icon(
        painter = painterResource(id = R.drawable.icon_github),
        contentDescription = "GitHub",
        tint = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxSize()
      )
    }

    IconButton(
      onClick = {
        val intent = Intent(Intent.ACTION_VIEW, "https://discord.gg/yR7MDch2zG".toUri())
        context.startActivity(intent)
      },
      modifier = Modifier.size(48.dp)
    ) {
      Icon(
        painter = painterResource(id = R.drawable.icon_discord),
        contentDescription = "Join Discord",
        tint = Color(0xFF5865F2),
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}

@Composable
fun ShareButton(modifier: Modifier = Modifier, context: Context) {
  val gameUrl = "https://github.com/asdru22/Card-Game-3/releases"
  val shareText = stringResource(R.string.ui_share_game, gameUrl)
  val shareTitle = stringResource(R.string.ui_share_via)

  IconButton(
    onClick = {
      val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
      }
      val shareIntent = Intent.createChooser(sendIntent, shareTitle)
      context.startActivity(shareIntent)
    },
    modifier = modifier.size(48.dp)
  ) {
    Icon(
      imageVector = Icons.Default.Share,
      contentDescription = "Share Game",
      tint = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.fillMaxSize()
    )
  }
}
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
  onStrategicGame: (String, String, Long?, Long?) -> Unit
) {
  val players by playerViewModel.players.collectAsState()

  var player1 by remember { mutableStateOf<Player?>(null) }
  var player2 by remember { mutableStateOf<Player?>(null) }

  var showRules by remember { mutableStateOf(false) }
  var showAddPlayerDialog by remember { mutableStateOf(false) }
  var showLeaderboard by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF121212))
  ) {
    // Existing Bottom Left Icons
    IconRow(
      Modifier
        .align(Alignment.BottomStart)
        .padding(16.dp),
      LocalContext.current
    )

    // NEW: Share Button at Bottom Right
    ShareButton(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp),
      context = LocalContext.current
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = stringResource(R.string.ui_main_menu),
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(bottom = 48.dp)
      )

      Row(
        modifier = Modifier.fillMaxWidth(0.7f),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Player 1 Dropdown
        PlayerDropdown(
          label = stringResource(R.string.ui_player_name, 1),
          players = players.filter { it.id != player2?.id },
          selectedPlayer = player1,
          onPlayerSelected = { player1 = it },
          modifier = Modifier.weight(1f)
        )

        // Player 2 Dropdown
        PlayerDropdown(
          label = stringResource(R.string.ui_player_name, 2),
          players = players.filter { it.id != player1?.id },
          selectedPlayer = player2,
          onPlayerSelected = { player2 = it },
          modifier = Modifier.weight(1f)
        )
      }



      Spacer(modifier = Modifier.height(32.dp))

      Row(
        modifier = Modifier.fillMaxWidth(0.7f),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Button(
          onClick = {
            onCasualGame(
              player1?.name ?: "Player 1",
              player2?.name ?: "Player 2",
              player1?.id,
              player2?.id
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .height(60.dp)
            .weight(1f)
        ) {
          Text(
            text = stringResource(R.string.ui_casual_game),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }

        Button(
          onClick = {
            onStrategicGame(
              player1?.name ?: "Player 1",
              player2?.name ?: "Player 2",
              player1?.id,
              player2?.id
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .height(60.dp)
            .weight(1f)
        ) {
          Text(
            text = stringResource(R.string.ui_strategic_game),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }



      Spacer(modifier = Modifier.height(24.dp))

      Row(
        modifier = Modifier.fillMaxWidth(0.7f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Add Player Button
        IconButton(
          onClick = { showAddPlayerDialog = true },
          modifier = Modifier
            .background(Color(0xFF2E7D32), RoundedCornerShape(8.dp))
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Player",
            tint = Color.White
          )
        }

        Spacer(modifier = Modifier.size(16.dp))

        // Leaderboard Button
        IconButton(
          onClick = { showLeaderboard = true },
          modifier = Modifier
            .background(Color(0xFFFFA000), RoundedCornerShape(8.dp))
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.List,
            contentDescription = "Leaderboard",
            tint = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      IconButton(
        onClick = { showRules = true },
        modifier = Modifier.size(48.dp)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Help,
          contentDescription = "How to play",
          tint = Color.Gray,
          modifier = Modifier.fillMaxSize()
        )
      }
    }

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
            onError = { /* Handle error, potentially show toast or error state in dialog */ }
          )
        }
      )
    }

    if (showLeaderboard) {
      LeaderboardDialog(
        players = players,
        onDismiss = { showLeaderboard = false }
      )
    }
  }
}

@Composable
fun LeaderboardDialog(
  players: List<Player>,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Leaderboard") },
    text = {
      Column {
        val sortedPlayers = remember(players) { players.sortedByDescending { it.wins } }
        
        if (sortedPlayers.isEmpty()) {
            Text("No players yet.")
        } else {
            // Using a Column inside a Scrollable container would be better if list is long, 
            // but AlertDialog text area handles scrolling automatically if content is large.
            // For many players, LazyColumn inside a custom Dialog would be better, but keeping it simple for now.
             sortedPlayers.forEachIndexed { index, player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${index + 1}. ${player.name}")
                    Text(text = "${player.wins} wins", fontWeight = FontWeight.Bold)
                }
             }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Close")
      }
    }
  )
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
      label = { Text(label, color = Color.Gray) },
      readOnly = true,
      trailingIcon = {
        Icon(
          imageVector = Icons.Default.ArrowDropDown,
          contentDescription = null,
          tint = Color.White
        )
      },
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.Gray,
        cursorColor = Color.White,
        focusedLabelColor = Color.White
      ),
      modifier = Modifier
        .fillMaxWidth()
        .clickable { expanded = true }
    )

    // Overlay transparent button to capture clicks if TextField eats them (depends on version, usually readOnly consumes clicks differently)
    // Better: use Box with onClick on modifier of Box, but TextField needs to be disabled?
    // Actually, creating a transparent box over it is reliable.
    Box(
      modifier = Modifier
        .matchParentSize()
        .clickable { expanded = true }
    )

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      modifier = Modifier.background(Color(0xFF2C2C2C))
    ) {
      players.forEach { player ->
        DropdownMenuItem(
          text = {
            Text(
              player.name,
              color = Color.White
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
fun IconRow(modifier: Modifier = Modifier, context: Context) {
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
        tint = Color.White,
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
      tint = Color.White,
      modifier = Modifier.fillMaxSize()
    )
  }
}
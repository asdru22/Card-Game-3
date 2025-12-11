package com.asdru.cardgame3.view.mainMenu

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.asdru.cardgame3.view.characterSelection.HowToPlayOverlay

@Composable
fun MainMenuScreen(
  onCasualGame: (String, String) -> Unit,
  onStrategicGame: () -> Unit
) {
  var player1Name by remember { mutableStateOf("Player 1") }
  var player2Name by remember { mutableStateOf("Player 2") }
  var showRules by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF121212))
  ) {
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
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        OutlinedTextField(
          value = player1Name,
          onValueChange = { player1Name = it },
          label = { Text(stringResource(R.string.ui_player_name, 1), color = Color.Gray) },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.White,
            focusedLabelColor = Color.White
          ),
          modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
          value = player2Name,
          onValueChange = { player2Name = it },
          label = { Text(stringResource(R.string.ui_player_name, 2), color = Color.Gray) },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.White,
            focusedLabelColor = Color.White
          ),
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp)
      ) {
        Button(
          onClick = { onCasualGame(player1Name, player2Name) },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .height(60.dp)
            .width(200.dp)
        ) {
          Text(
            text = stringResource(R.string.ui_casual_game),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }

        Button(
          onClick = onStrategicGame,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .height(60.dp)
            .width(200.dp)
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
  }
}
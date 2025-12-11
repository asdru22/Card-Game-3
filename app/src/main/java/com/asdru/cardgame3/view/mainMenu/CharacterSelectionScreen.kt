package com.asdru.cardgame3.view.mainMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.R
import com.asdru.cardgame3.entity.Entity
import com.asdru.cardgame3.view.characterSelection.PlayerGridSection
import kotlin.reflect.full.createInstance

@Composable
fun CharacterSelectionScreen(
  player1Name: String,
  player2Name: String,
  onStartGame: (List<Entity>, List<Entity>) -> Unit
) {
  val p1Team = remember { mutableStateListOf<Entity>() }
  val p2Team = remember { mutableStateListOf<Entity>() }

  val availableCharacters = remember {
    Entity::class.sealedSubclasses.map { it.createInstance() }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF121212))
      .padding(16.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = player1Name,
        color = Color.Gray,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.align(Alignment.CenterStart)
      )

      Button(
        onClick = {
          onStartGame(p1Team.toList(), p2Team.toList())
        },
        enabled = p1Team.size == 3 && p2Team.size == 3,
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF4CAF50),
          disabledContainerColor = Color.DarkGray
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(48.dp)
      ) {
        Text(
          text = stringResource(R.string.ui_start),
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = if (p1Team.size == 3 && p2Team.size == 3) Color.White else Color.Gray
        )
      }

      Text(
        text = player2Name,
        color = Color.Gray,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.align(Alignment.CenterEnd)
      )
    }

    Row(
      modifier = Modifier.fillMaxSize(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(modifier = Modifier.weight(1f)) {
        PlayerGridSection(
          team = p1Team,
          available = availableCharacters,
        )
      }

      VerticalDivider(
        modifier = Modifier.fillMaxHeight(),
        color = Color.Gray.copy(alpha = 0.3f),
        thickness = 1.dp
      )

      Box(modifier = Modifier.weight(1f)) {
        PlayerGridSection(
          team = p2Team,
          available = availableCharacters,
        )
      }
    }
  }
}
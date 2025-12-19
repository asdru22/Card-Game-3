package com.asdru.cardgame3.view.battle

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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.BattleViewModel


@Composable
fun Winner(viewModel: BattleViewModel) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.90f))
      .clickable(enabled = true) {},
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth()
    ) {
      // --- Title ---
      Text(
        text = "BATTLE ENDED",
        color = Color.White,
        fontSize = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 4.sp
      )

      Spacer(modifier = Modifier.height(32.dp))

      // --- Teams Comparison Row ---
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Top
      ) {
        // Left Team Stats
        TeamResultItem(
          team = viewModel.leftTeam,
          isWinner = viewModel.winner == viewModel.leftTeam.name
        )

        // VS Divider (Visual separator)
        Box(
          modifier = Modifier
            .height(200.dp)
            .width(1.dp)
            .background(Color.Gray.copy(alpha = 0.5f))
        )

        // Right Team Stats
        TeamResultItem(
          team = viewModel.rightTeam,
          isWinner = viewModel.winner == viewModel.rightTeam.name
        )
      }

      Spacer(modifier = Modifier.height(48.dp))

      // --- Restart Button ---
      Button(
        onClick = { viewModel.onRestartClicked() },
        colors = ButtonDefaults.buttonColors(
          containerColor = Color.White,
          contentColor = Color.Black
        ),
        modifier = Modifier
          .width(200.dp)
          .height(56.dp)
      ) {
        Text(
          text = stringResource(R.string.ui_restart).uppercase(),
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun TeamResultItem(
  team: com.asdru.cardgame3.viewModel.TeamViewModel,
  isWinner: Boolean
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.width(200.dp)
  ) {
    Text(
      text = team.name,
      color = if (isWinner) Color(0xFFFFD700) else Color.White,
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      maxLines = 1
    )

    if (isWinner) {
      Text(
        text = "WINNER",
        color = Color(0xFFFFD700),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(top = 4.dp)
      )
    } else {

      Spacer(modifier = Modifier.height(20.dp))
    }

    Spacer(modifier = Modifier.height(24.dp))

    // --- Statistics ---
    StatRow(label = stringResource(R.string.ui_damage_dealt), value = "${team.totalDamageDealt.toInt()}")
    StatRow(label = stringResource(R.string.ui_total_healing), value = "${team.totalHealing.toInt()}")
    StatRow(
      label = stringResource(R.string.ui_effects_applied),
      value = "${team.totalEffectsApplied}"
    )
    StatRow(
      label = stringResource(R.string.ui_coins_spent),
      value = "${team.totalCoinsSpent}",
      iconRes = R.drawable.icon_coins
    )
  }
}

@Composable
fun StatRow(
  label: String,
  value: String,
  iconRes: Int? = null
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      color = Color.Gray,
      fontSize = 14.sp
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = value,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
      if (iconRes != null) {
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          painter = painterResource(id = iconRes),
          contentDescription = null,
          tint = Color(0xFFFFD700),
          modifier = Modifier.size(14.dp)
        )
      }
    }
  }
}
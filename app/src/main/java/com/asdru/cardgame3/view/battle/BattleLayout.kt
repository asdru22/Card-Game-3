package com.asdru.cardgame3.view.battle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.R
import com.asdru.cardgame3.view.team.RageBar
import com.asdru.cardgame3.view.team.TeamColumn
import com.asdru.cardgame3.viewModel.BattleViewModel

@Composable
fun BattleLayout(
  viewModel: BattleViewModel,
  finalCardHeight: Dp,
  finalCardWidth: Dp
) {
  Row(
    modifier = Modifier.fillMaxSize(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {

    // Left Rage Bar
    RageBar(
      rage = viewModel.leftTeam.rage,
      maxRage = viewModel.leftTeam.maxRage,
      isTurn = viewModel.isLeftTeamTurn,
      isDragging = viewModel.ultimateDragState?.team == viewModel.leftTeam,
      onDragStart = { offset -> viewModel.onUltimateDragStart(viewModel.leftTeam, offset) },
      onDrag = viewModel::onUltimateDrag,
      onDragEnd = viewModel::onUltimateDragEnd,
      modifier = Modifier
        .fillMaxHeight()
        .padding(start = 40.dp, top = 24.dp, bottom = 24.dp)
    )

    // Center Game Area
    Row(
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
        .padding(horizontal = 16.dp, vertical = 15.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {

      // --- LEFT GROUP (Team + Coin) ---
      Row(modifier = Modifier.fillMaxHeight()) {
        TeamColumn(
          entities = viewModel.leftTeam.entities,
          alignment = Alignment.Start,
          cardWidth = finalCardWidth,
          cardHeight = finalCardHeight,
          canAct = viewModel.gameLogic::canEntityAct,
          onCardPositioned = viewModel::onCardPositioned,
          onDragStart = viewModel::onDragStart,
          onDrag = viewModel::onDrag,
          onDragEnd = viewModel::onDragEnd,
          onDoubleTap = viewModel::onDoubleTap,
          getHighlightColor = viewModel::getHighlightColor
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Left Coin Pill (Aligned Bottom)
        Box(
          modifier = Modifier.fillMaxHeight(),
          contentAlignment = Alignment.BottomCenter
        ) {
          CoinPill(amount = viewModel.leftTeam.coins)
        }
      }

      // --- VS TEXT ---
      Text(
        text = "VS",
        color = Color.Gray,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.alpha(0.5f)
      )

      // --- RIGHT GROUP (Coin + Team) ---
      Row(modifier = Modifier.fillMaxHeight()) {
        // Right Coin Pill (Aligned Bottom)
        Box(
          modifier = Modifier.fillMaxHeight(),
          contentAlignment = Alignment.BottomCenter
        ) {
          CoinPill(amount = viewModel.rightTeam.coins)
        }

        Spacer(modifier = Modifier.width(12.dp))

        TeamColumn(
          entities = viewModel.rightTeam.entities,
          alignment = Alignment.End,
          cardWidth = finalCardWidth,
          cardHeight = finalCardHeight,
          canAct = viewModel.gameLogic::canEntityAct,
          onCardPositioned = viewModel::onCardPositioned,
          onDragStart = viewModel::onDragStart,
          onDrag = viewModel::onDrag,
          onDragEnd = viewModel::onDragEnd,
          onDoubleTap = viewModel::onDoubleTap,
          getHighlightColor = viewModel::getHighlightColor
        )
      }
    }

    // Right Rage Bar
    RageBar(
      rage = viewModel.rightTeam.rage,
      maxRage = viewModel.rightTeam.maxRage,
      isTurn = !viewModel.isLeftTeamTurn,
      isDragging = viewModel.ultimateDragState?.team == viewModel.rightTeam,
      onDragStart = { offset -> viewModel.onUltimateDragStart(viewModel.rightTeam, offset) },
      onDrag = viewModel::onUltimateDrag,
      onDragEnd = viewModel::onUltimateDragEnd,
      modifier = Modifier
        .fillMaxHeight()
        .padding(end = 40.dp, top = 24.dp, bottom = 24.dp)
    )
  }
}

@Composable
fun CoinPill(
  amount: Int
) {
  Box(
    modifier = Modifier
      .padding(bottom = 8.dp)
      .clip(CircleShape)
      .background(Color.Black.copy(alpha = 0.6f))
      .border(1.dp, Color(0xFFFFD700), CircleShape)
      .padding(horizontal = 12.dp, vertical = 6.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(
        painter = painterResource(id = R.drawable.icon_coins),
        contentDescription = "Coins",
        tint = Color(0xFFFFD700),
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "$amount",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
      )
    }
  }
}
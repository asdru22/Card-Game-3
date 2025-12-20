package com.asdru.cardgame3.view.battle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.asdru.cardgame3.R
import com.asdru.cardgame3.view.team.RageBar
import com.asdru.cardgame3.view.team.Shop
import com.asdru.cardgame3.view.team.TeamColumn
import com.asdru.cardgame3.view.totem.TotemView
import com.asdru.cardgame3.viewModel.BattleViewModel

@Composable
fun BattleLayout(
  viewModel: BattleViewModel,
  finalCardHeight: Dp,
  finalCardWidth: Dp
) {
  Box(modifier = Modifier.fillMaxSize()) {
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

        // --- LEFT GROUP (Team + Shop + Totem) ---
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

          Box(
            modifier = Modifier
              .fillMaxHeight()
              .width(80.dp)
              .zIndex(1f),
            contentAlignment = Alignment.Center
          ) {
            TotemView(
              totemVm = viewModel.leftTeam.totem,
              canUseAbility = viewModel.leftTeam.totem?.let { viewModel.gameLogic.canTotemAct(it) }
                ?: false,
              onDoubleTap = viewModel::onTotemDoubleTap,
              onDragStart = viewModel::onTotemDragStart,
              onDrag = viewModel::onTotemDrag,
              onDragEnd = viewModel::onTotemDragEnd,
              onPositioned = { rect -> viewModel.totemBounds[viewModel.leftTeam.totem!!] = rect },
              modifier = Modifier.align(Alignment.Center)
            )

            Shop(
              viewModel = viewModel.leftTeam.shop,
              onDragStart = { item, offset -> viewModel.onShopDragStart(item, true, offset) },
              onDrag = viewModel::onShopDrag,
              onDragEnd = viewModel::onShopDragEnd,
              modifier = Modifier.align(Alignment.BottomCenter)
            )
          }
        }

        // --- VS TEXT & ROUND ---
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = stringResource(R.string.ui_round, viewModel.roundCount),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
              .padding(bottom = 8.dp)
              .alpha(0.8f)
          )

          Text(
            text = "VS",
            color = Color.Gray,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alpha(0.5f)
          )
        }

        // --- RIGHT GROUP (Totem + Shop + Team) ---
        Row(modifier = Modifier.fillMaxHeight()) {
          Box(
            modifier = Modifier
              .fillMaxHeight()
              .width(80.dp)
              .zIndex(1f),
            contentAlignment = Alignment.Center
          ) {
            TotemView(
              totemVm = viewModel.rightTeam.totem,
              canUseAbility = viewModel.rightTeam.totem?.let { viewModel.gameLogic.canTotemAct(it) }
                ?: false,
              onDoubleTap = viewModel::onTotemDoubleTap,
              onDragStart = viewModel::onTotemDragStart,
              onDrag = viewModel::onTotemDrag,
              onDragEnd = viewModel::onTotemDragEnd,
              onPositioned = { rect -> viewModel.totemBounds[viewModel.rightTeam.totem!!] = rect },
              modifier = Modifier.align(Alignment.Center)
            )

            Shop(
              viewModel = viewModel.rightTeam.shop,
              onDragStart = { item, offset -> viewModel.onShopDragStart(item, false, offset) },
              onDrag = viewModel::onShopDrag,
              onDragEnd = viewModel::onShopDragEnd,
              modifier = Modifier.align(Alignment.BottomCenter)
            )
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

    TurnChangeNotification(
      isLeftTurn = viewModel.isLeftTeamTurn,
      leftName = viewModel.leftTeam.name,
      rightName = viewModel.rightTeam.name,
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 80.dp)
    )
  }
}

@Composable
private fun TurnChangeNotification(
  isLeftTurn: Boolean,
  leftName: String,
  rightName: String,
  modifier: Modifier = Modifier
) {
  var visible by remember { mutableStateOf(false) }

  LaunchedEffect(isLeftTurn) {
    visible = true
    delay(1500)
    visible = false
  }

  AnimatedVisibility(
    visible = visible,
    enter = fadeIn(),
    exit = fadeOut(),
    modifier = modifier.zIndex(10f)
  ) {
    Box(contentAlignment = Alignment.Center) {
      val text = if (isLeftTurn) stringResource(
        R.string.ui_turn,
        leftName
      ) else stringResource(R.string.ui_turn, rightName)

      // Outline
      Text(
        text = text,
        color = Color.Black,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        style = TextStyle.Default.copy(
          drawStyle = Stroke(
            miter = 10f,
            width = 6f,
            join = StrokeJoin.Round
          )
        )
      )

      // Foreground
      Text(
        text = text,
        color = Color.White,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
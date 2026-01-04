package com.asdru.cardgame3.view.battle

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.viewModel.BattleViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MysteryDeckView(
  viewModel: BattleViewModel,
  modifier: Modifier = Modifier
) {
  val centerCard = viewModel.centerDeckCard
  val revealedCard = viewModel.revealedMysteryCard
  val hasUsed = viewModel.hasUsedCenterCard

  val showLeftArrow = !viewModel.isLeftTeamTurn && !hasUsed && centerCard != null
  val showRightArrow = viewModel.isLeftTeamTurn && !hasUsed && centerCard != null

  var offsetX by remember { mutableFloatStateOf(0f) }
  val scope = rememberCoroutineScope()

  val cardWidth = 80.dp
  val cardHeight = 112.dp
  val density = LocalDensity.current
  val cardWidthPx = with(density) { cardWidth.toPx() }

  LaunchedEffect(revealedCard) {
    if (revealedCard == null) {
      offsetX = 0f
    }
  }

  Box(
    modifier = modifier.size(160.dp),
    contentAlignment = Alignment.Center
  ) {

    if (centerCard == null && revealedCard == null) {
      Text(
        text = "VS",
        color = Color.White.copy(alpha = 0.3f),
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )
    }

    if (centerCard != null) {
      CardBack(
        modifier = Modifier
          .offset(x = 6.dp, y = 6.dp)
          .size(cardWidth, cardHeight)
      )
      CardBack(
        modifier = Modifier
          .offset(x = 3.dp, y = 3.dp)
          .size(cardWidth, cardHeight)
      )
    }

    val arrowYOffset = 0.dp
    val arrowXOffset = 65.dp

    if (showLeftArrow) {
      Box(
        modifier = Modifier
          .align(Alignment.Center)
          .offset(x = -arrowXOffset, y = arrowYOffset)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Swipe Left",
          tint = Color.White.copy(alpha = 0.5f),
          modifier = Modifier.size(32.dp)
        )
      }
    }
    if (showRightArrow) {
      Box(
        modifier = Modifier
          .align(Alignment.Center)
          .offset(x = arrowXOffset, y = arrowYOffset)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = "Swipe Right",
          tint = Color.White.copy(alpha = 0.5f),
          modifier = Modifier.size(32.dp)
        )
      }
    }

    if (centerCard != null || revealedCard != null) {
      val isFaceUp = revealedCard != null
      val currentCardData = revealedCard ?: centerCard!!

      val dragModifier = if (!hasUsed && revealedCard == null) {
        Modifier.pointerInput(Unit) {
          detectHorizontalDragGestures(
            onDragEnd = {
              val threshold = cardWidthPx * 0.8f
              val targetOffset = if (offsetX > threshold && showRightArrow) {
                viewModel.onCenterDeckSwipe(true)
                cardWidthPx * 1.2f
              } else if (offsetX < -threshold && showLeftArrow) {
                viewModel.onCenterDeckSwipe(false)
                -cardWidthPx * 1.2f
              } else {
                0f
              }

              scope.launch {
                animate(
                  initialValue = offsetX,
                  targetValue = targetOffset,
                  animationSpec = tween(300)
                ) { value, _ -> offsetX = value }
              }
            },
            onDragCancel = {
              scope.launch {
                animate(
                  initialValue = offsetX,
                  targetValue = 0f,
                  animationSpec = tween(300)
                ) { value, _ -> offsetX = value }
              }
            },
            onHorizontalDrag = { change, dragAmount ->
              change.consume()
              val newOffset = offsetX + dragAmount
              if ((showRightArrow && newOffset > 0) || (showLeftArrow && newOffset < 0)) {
                offsetX = newOffset.coerceIn(-cardWidthPx, cardWidthPx)
              }
            }
          )
        }
      } else Modifier


      val rotation by animateFloatAsState(
        targetValue = if (revealedCard != null) 180f else 0f,
        animationSpec = if (revealedCard != null) tween(500) else snap(),
        label = "CardFlip"
      )

      Box(
        modifier = Modifier
          .offset { IntOffset(offsetX.roundToInt(), 0) }
          .size(cardWidth, cardHeight)
          .then(dragModifier)
          .graphicsLayer {
            rotationY = rotation
            cameraDistance = 12f * density.density
          },
        contentAlignment = Alignment.Center
      ) {
        if (rotation <= 90f) {
          // Back Face
          CardBack(modifier = Modifier.fillMaxSize())
        } else {
          // Front Face
          Box(
            modifier = Modifier
              .fillMaxSize()
              .graphicsLayer { rotationY = 180f }
              .background(
                brush = Brush.linearGradient(
                  listOf(
                    Color(0xFF000000),
                    Color(0xFF1E1E1E)
                  )
                ),
                shape = RoundedCornerShape(8.dp)
              )
              .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
              modifier = Modifier.padding(4.dp)
            ) {
              Text(
                text = stringResource(currentCardData.descriptionRes, *currentCardData.formatArgs.toTypedArray()),
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun CardBack(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(Color(0xFF4A148C), Color(0xFF1E1E1E))
        ),
        shape = RoundedCornerShape(8.dp)
      )
      .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(6.dp)
        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
    )
    Text(
      text = "?",
      color = Color.White.copy(alpha = 0.5f),
      fontSize = 32.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

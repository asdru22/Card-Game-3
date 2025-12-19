package com.asdru.cardgame3.view.character

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.data.Popup
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlinx.coroutines.launch

@Composable
fun CharacterCard(
  viewModel: EntityViewModel,
  width: Dp,
  height: Dp,
  canAct: Boolean,
  onCardPositioned: (EntityViewModel, Rect) -> Unit,
  onDragStart: (EntityViewModel, Offset) -> Unit,
  onDrag: (Offset) -> Unit,
  onDragEnd: () -> Unit,
  onDoubleTap: (EntityViewModel) -> Unit,
  highlightColor: Color = Color.Transparent
) {
  val cardShape = RoundedCornerShape(12.dp)

  val animatedOffset by animateOffsetAsState(
    targetValue = viewModel.attackAnimOffset ?: Offset.Zero,
    animationSpec = tween(200),
    label = "AttackAnimation"
  )

  val scaleX = remember { Animatable(1f) }
  val scaleY = remember { Animatable(1f) }
  val rotationZ = remember { Animatable(0f) }

  LaunchedEffect(viewModel.hitAnimTrigger) {
    if (viewModel.hitAnimTrigger > 0) {
      launch {
        scaleX.animateTo(1.2f, tween(100))
        scaleX.animateTo(1f, tween(150))
      }
      launch {
        scaleY.animateTo(0.8f, tween(100))
        scaleY.animateTo(1f, tween(150))
      }
    }
  }

  LaunchedEffect(viewModel.passiveAnimTrigger) {
    if (viewModel.passiveAnimTrigger > 0) {
      launch {
        scaleX.animateTo(0f, tween(150))
        scaleX.animateTo(1f, tween(150))
      }
    }
  }

  LaunchedEffect(viewModel.chargeAnimTrigger) {
    if (viewModel.chargeAnimTrigger > 0) {
      launch {
        rotationZ.animateTo(360f, tween(300))
        rotationZ.snapTo(0f)
      }
    }
  }

  Box(
    modifier = Modifier
      .width(width)
      .height(height)
      .graphicsLayer {
        this.translationX = animatedOffset.x
        this.translationY = animatedOffset.y
        this.scaleX = scaleX.value
        this.scaleY = scaleY.value
        this.rotationZ = rotationZ.value
      }
      .pointerInput(Unit) {
        detectTapGestures(
          onDoubleTap = { if (viewModel.isAlive) onDoubleTap(viewModel) },
        )
      }
      .pointerInput(Unit) {
        detectDragGestures(
          onDragStart = { offset -> onDragStart(viewModel, offset) },
          onDrag = { change, dragAmount ->
            change.consume()
            onDrag(dragAmount)
          },
          onDragEnd = { onDragEnd() },
          onDragCancel = { onDragEnd() }
        )
      }
      .onGloballyPositioned { coordinates ->
        onCardPositioned(viewModel, coordinates.boundsInRoot())
      }
      .then(
        if (highlightColor != Color.Transparent) {
          Modifier.drawBehind {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.color = highlightColor.toArgb()
            frameworkPaint.maskFilter = BlurMaskFilter(
              15.dp.toPx(),
              BlurMaskFilter.Blur.NORMAL
            )
            drawIntoCanvas { canvas ->
              canvas.drawOutline(
                outline = cardShape.createOutline(size, layoutDirection, this),
                paint = paint
              )
            }
          }
        } else Modifier
      )
  ) {
    Card(
      shape = cardShape,
      modifier = Modifier
        .fillMaxSize()
        .then(
          if (canAct) Modifier.border(2.dp, Color.White, cardShape)
          else Modifier
        ),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
      elevation = CardDefaults.cardElevation(8.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
      ) {
        Spacer(modifier = Modifier.height(8.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth(0.75f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.DarkGray),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            painter = painterResource(viewModel.iconRes),
            tint = if (viewModel.isAlive) viewModel.color else Color.Gray,
            contentDescription = viewModel.name.toString(),
            modifier = Modifier
              .fillMaxSize()
              .padding(4.dp),
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        StatsBar(viewModel)

        if (viewModel.effectManager.effects.isNotEmpty()) {
          ActiveEffects(viewModel)
        }
      }
    }

    val isLeftTeam = viewModel.team.team.left
    val alignment = if (isLeftTeam) Alignment.CenterEnd else Alignment.CenterStart
    val hasActiveCharges = viewModel.currentActiveCharges > 0
    val hasPassiveCharges = viewModel.currentPassiveCharges > 0

    if (hasActiveCharges || hasPassiveCharges) {
      val xOffset = if (isLeftTeam) 10.dp else (-10).dp

      Column(
        modifier = Modifier
          .align(alignment)
          .offset(x = xOffset)
          .clip(CircleShape)
          .background(Color(0xFF171717))
          .padding(vertical = 3.dp, horizontal = 3.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        if (hasActiveCharges) {
          ChargeDots(
            current = viewModel.currentActiveCharges,
            max = viewModel.entity.activeAbility.charges,
            activeColor = Color(0xFF66BB6A)
          )
        }

        if (hasActiveCharges && hasPassiveCharges) {
          Spacer(modifier = Modifier.height(4.dp))
        }

        if (hasPassiveCharges) {
          ChargeDots(
            current = viewModel.currentPassiveCharges,
            max = viewModel.entity.passiveAbility.charges,
            activeColor = Color(0xFF42A5F5)
          )
        }
      }
    }

    // Trait Charges (Opposite Side)
    val traitsWithCharges = viewModel.traits.filter { it.maxCharges > 0 }
    if (traitsWithCharges.isNotEmpty() && !viewModel.isAlive) {
      val oppositeAlignment = if (isLeftTeam) Alignment.CenterStart else Alignment.CenterEnd
      val xOffsetTrait = if (isLeftTeam) (-10).dp else 10.dp

      Column(
        modifier = Modifier
          .align(oppositeAlignment)
          .offset(x = xOffsetTrait)
          .clip(CircleShape)
          .background(Color(0xFF171717))
          .padding(vertical = 3.dp, horizontal = 3.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        traitsWithCharges.forEachIndexed { index, trait ->
           ChargeDots(
             current = viewModel.traitCharges[trait.id] ?: 0,
             max = trait.maxCharges,
             activeColor = Color(0xFF9C27B0)
           )
           
           if (index < traitsWithCharges.size - 1) {
             Spacer(modifier = Modifier.height(4.dp))
           }
        }
      }
    }
  }
}

@Composable
fun ChargeDots(current: Int, max: Int, activeColor: Color) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    repeat(max) { index ->
      val isCharged = index < current
      Box(
        modifier = Modifier
          .padding(vertical = 1.dp)
          .size(12.dp)
          .clip(CircleShape)
          .background(if (isCharged) activeColor else Color(0xFF969696))
      )
    }
  }
}

@Composable
fun StatsBar(viewModel: EntityViewModel) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth()
  ) {
    val hp = viewModel.health
    val hpPercent = (hp / viewModel.maxHealth).coerceIn(0f, 1f)

    val barColor = when {
      hpPercent > 0.5f -> Color(0xFF4CAF50)
      hpPercent > 0.2f -> Color(0xFFFFC107)
      else -> Color(0xFFF44336)
    }

    Box(
      modifier = Modifier
        .fillMaxWidth(0.8f)
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(Color.DarkGray)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(hpPercent)
          .fillMaxHeight()
          .background(barColor)
      )
    }
  }
}

@Composable
fun ActiveEffects(viewModel: EntityViewModel) {
  Spacer(modifier = Modifier.height(4.dp))
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
  ) {
    viewModel.effectManager.effects.forEach { effect ->
      Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
          .padding(2.dp)
          .size(32.dp)
      ) {
        Icon(
          painter = painterResource(id = effect.iconRes),
          contentDescription = effect.nameRes.toString(),
          tint = Color.White,
          modifier = Modifier.fillMaxSize()
        )

        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .size(14.dp)
            .clip(CircleShape)
            .background(Color.Black)
        ) {
          Text(
            text = effect.duration.toString(),
            color = Color.White,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 8.sp
          )
        }
      }
    }
  }
}

@Composable
fun PopupView(popup: Popup, parentTranslation: Offset, onComplete: () -> Unit) {
  val offsetY = remember { Animatable(0f) }
  val alpha = remember { Animatable(1f) }

  LaunchedEffect(Unit) {
    launch {
      offsetY.animateTo(-50f, animationSpec = tween(1000))
    }
    launch {
      alpha.animateTo(0f, animationSpec = tween(800, delayMillis = 200))
      onComplete()
    }
  }

  val displayText = if (popup.textRes != null) {
    stringResource(id = popup.textRes)
  } else {
    popup.text ?: ""
  }

  val fontSize = if (popup.isStatus) 14.sp else 28.sp

  val fontStyle = if (popup.isStatus) FontStyle.Italic else FontStyle.Normal
  val fontWeight = if (popup.isStatus) FontWeight.Normal else FontWeight.Bold

  Text(
    text = displayText,
    color = popup.color,
    fontSize = fontSize,
    fontStyle = fontStyle,
    fontWeight = fontWeight,
    modifier = Modifier
      .graphicsLayer {
        translationX = parentTranslation.x
        translationY = parentTranslation.y
      }
      .offset(x = popup.xOffset.dp, y = offsetY.value.dp)
      .alpha(alpha.value)
  )
}
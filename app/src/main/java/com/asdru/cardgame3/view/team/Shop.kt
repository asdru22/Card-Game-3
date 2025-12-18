package com.asdru.cardgame3.view.team

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.asdru.cardgame3.R
import com.asdru.cardgame3.game.item.ShopItem
import com.asdru.cardgame3.view.common.SmartDescriptionText
import com.asdru.cardgame3.viewModel.ShopViewModel

@Composable
fun Shop(
  viewModel: ShopViewModel,
  onDragStart: (ShopItem, Offset) -> Unit,
  onDrag: (Offset) -> Unit,
  onDragEnd: () -> Unit
) {
  val shape = RoundedCornerShape(16.dp)
  var selectedItem by remember { mutableStateOf<ShopItem?>(null) }

  if (selectedItem != null) {
    Dialog(onDismissRequest = { selectedItem = null }) {
      ShopItemDetail(
        item = selectedItem!!,
        onClose = { selectedItem = null }
      )
    }
  }

  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .padding(bottom = 8.dp)
      .width(72.dp)
      .clip(shape)
      .background(Color.Black.copy(alpha = 0.85f))
      .border(1.dp, Color(0xFFFFD700), shape)
      .animateContentSize(
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioLowBouncy,
          stiffness = Spring.StiffnessMedium
        )
      )
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
    ) {

      AnimatedVisibility(
        visible = viewModel.isOpen,
        enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
        exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut()
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          viewModel.items.forEach { item ->
            ShopItemRow(
              item = item,
              canAfford = viewModel.canAfford(item.cost),
              onDragStart = onDragStart,
              onDrag = onDrag,
              onDragEnd = onDragEnd,
              onShowInfo = { selectedItem = item }
            )
            HorizontalDivider(
              modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 6.dp),
              color = Color.Gray.copy(alpha = 0.3f),
              thickness = 1.dp
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) { viewModel.toggle() }
          .padding(4.dp)
      ) {
        Icon(
          painter = painterResource(id = R.drawable.icon_coins),
          contentDescription = "Coins",
          tint = Color(0xFFFFD700),
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "${viewModel.coins}",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }
    }
  }
}

@Composable
fun ShopItemRow(
  item: ShopItem,
  canAfford: Boolean,
  onDragStart: (ShopItem, Offset) -> Unit,
  onDrag: (Offset) -> Unit,
  onDragEnd: () -> Unit,
  onShowInfo: () -> Unit
) {
  var iconCenterGlobal by remember { mutableStateOf(Offset.Zero) }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier
      .padding(vertical = 4.dp)
      .onGloballyPositioned { coordinates ->
        val size = coordinates.size
        val position = coordinates.positionInRoot()
        iconCenterGlobal = Offset(
          x = position.x + size.width / 2f,
          y = position.y + size.height / 2f
        )
      }
      .pointerInput(canAfford) {
        if (canAfford) {
          detectDragGestures(
            onDragStart = { onDragStart(item, iconCenterGlobal) },
            onDrag = { change, dragAmount ->
              change.consume()
              onDrag(dragAmount)
            },
            onDragEnd = { onDragEnd() }
          )
        }
      }

      .pointerInput(Unit) {
        detectTapGestures(
          onDoubleTap = { onShowInfo() }
        )
      }
  ) {
    Icon(
      painter = painterResource(id = item.iconRes),
      contentDescription = item.getName(LocalContext.current),
      tint = if (canAfford) Color.White else Color.White.copy(alpha = 0.3f),
      modifier = Modifier.size(24.dp)
    )

    Spacer(modifier = Modifier.width(4.dp))

    Text(
      text = "${item.cost}",
      color = if (canAfford) Color(0xFFFFD700) else Color.Gray,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp
    )
  }
}

@Composable
fun ShopItemDetail(item: ShopItem, onClose: () -> Unit) {
  val context = LocalContext.current

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
    elevation = CardDefaults.cardElevation(8.dp),
    modifier = Modifier
      .width(300.dp)
      .border(
        1.dp,
        Color(0xFFFFD700).copy(alpha = 0.5f),
        RoundedCornerShape(12.dp)
      )
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(
          painter = painterResource(id = item.iconRes),
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.getName(context),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
          Text(
            text = stringResource(R.string.ui_shop_cost, item.cost),
            color = Color(0xFFFFD700),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
        IconButton(onClick = onClose) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
        }
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 12.dp),
        color = Color.Gray.copy(alpha = 0.3f)
      )

      SmartDescriptionText(translatable = item, textColor = Color.LightGray)
    }
  }
}
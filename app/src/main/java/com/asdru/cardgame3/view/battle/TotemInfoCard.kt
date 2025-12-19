package com.asdru.cardgame3.view.battle

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.R
import com.asdru.cardgame3.data.TotemAbility
import com.asdru.cardgame3.view.common.SmartDescriptionText
import com.asdru.cardgame3.viewModel.TotemViewModel

@Composable
fun TotemInfoCard(
  viewModel: TotemViewModel,
  onClose: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.6f))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {},
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .widthIn(max = 600.dp) // Match CharacterInfoCard width constraint
        .fillMaxWidth()
        .padding(16.dp),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
      elevation = CardDefaults.cardElevation(8.dp)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start
      ) {
        TotemInfoHeader(viewModel, onClose)

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(12.dp))

        // Abilities Body
        Column(modifier = Modifier.fillMaxWidth()) {
          TotemAbilityDisplay(
            context = LocalContext.current,
            label = "Active",
            ability = viewModel.activeAbility,
            color = Color(0xFFE91E63) // Pink
          )
          
          TotemAbilityDisplay(
            context = LocalContext.current,
            label = "Passive",
            ability = viewModel.passiveAbility,
            color = Color(0xFF42A5F5) // Blue
          )
        }
      }
    }
  }
}

@Composable
private fun TotemInfoHeader(
  viewModel: TotemViewModel,
  onClose: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Min),
    verticalAlignment = Alignment.CenterVertically
  ) {
    
    // Stats Pill (Left aligned or integrated?)
    // CharacterInfoCard had Pill - Name - Close or Close - Name - Pill.
    // Let's do: Pill - Spacer - Name (Centered weight) - Close Button
    
    TotemStatsPill(viewModel)
    
    Text(
      text = stringResource(viewModel.totem.name),
      color = Color.White,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 8.dp)
    )

    IconButton(
      onClick = onClose,
      modifier = Modifier.size(32.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "Close",
        tint = Color.White
      )
    }
  }
}

@Composable
private fun TotemStatsPill(viewModel: TotemViewModel) {
  Surface(
    color = Color(0xFF2D2D2D),
    shape = RoundedCornerShape(50),
    modifier = Modifier.clip(RoundedCornerShape(50))
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .height(IntrinsicSize.Min)
    ) {
      // Health
      Icon(
        painter = painterResource(id = R.drawable.icon_health),
        contentDescription = "Health",
        tint = Color(0xFFEF5350),
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "${viewModel.currentHealth.toInt()}/${viewModel.maxHealth.toInt()}",
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )

      VerticalDivider(
        modifier = Modifier
          .padding(horizontal = 8.dp)
          .fillMaxHeight(0.7f),
        color = Color.Gray.copy(alpha = 0.3f),
        thickness = 1.dp
      )

      // Damage
      Icon(
        painter = painterResource(id = R.drawable.icon_attack_damage),
        contentDescription = "Damage",
        tint = Color(0xFFFFCA28),
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "${viewModel.damage.toInt()}",
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun TotemAbilityDisplay(
  context: Context,
  label: String,
  ability: TotemAbility,
  color: Color
) {
  Column(modifier = Modifier.padding(bottom = 12.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = label,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 12.sp,
        style = TextStyle(
          platformStyle = PlatformTextStyle(
            includeFontPadding = false
          )
        ),
        modifier = Modifier
          .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
          .padding(horizontal = 6.dp, vertical = 3.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = ability.getName(context),
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
      )
    }

    SmartDescriptionText(
      translatable = ability,
      textColor = Color.LightGray
    )
  }
}

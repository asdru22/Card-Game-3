package com.asdru.cardgame3.view.character

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.R
import com.asdru.cardgame3.viewModel.SummonViewModel

@Composable
fun SummonInfoCard(
  viewModel: SummonViewModel,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
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
        .widthIn(max = 600.dp)
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
        SummonInfoHeader(viewModel, onClose)

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(12.dp))

        // Summons typically have one main ability
        val context = LocalContext.current
        CharacterAbility(
          context = context,
          label = stringResource(R.string.ui_action),
          ability = viewModel.summon.ability,
          color = Color.Yellow
        )
      }
    }
  }
}

@Composable
private fun SummonInfoHeader(
  viewModel: SummonViewModel,
  onClose: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Min),
    verticalAlignment = Alignment.CenterVertically
  ) {
    val closeBtn = @Composable {
      IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
      }
    }

    val nameTxt = @Composable { modifier: Modifier ->
      Text(
        text = stringResource(viewModel.name),
        color = Color.White,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(horizontal = 8.dp)
      )
    }

    val statsPill = @Composable {
      Surface(
        color = Color(0xFF2D2D2D),
        shape = RoundedCornerShape(50),
        modifier = Modifier.clip(RoundedCornerShape(50))
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          // Health
          Icon(
            painter = painterResource(id = R.drawable.icon_health),
            contentDescription = null,
            tint = Color(0xFFEF5350),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${viewModel.health.toInt()}/${viewModel.maxHealth.toInt()}",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )

          // Damage (if > 0)
          if (viewModel.damage > 0) {
            VerticalDivider(
              modifier = Modifier
                .padding(horizontal = 8.dp)
                .height(12.dp),
              color = Color.Gray.copy(alpha = 0.3f)
            )
            Icon(
              painter = painterResource(id = R.drawable.icon_attack_damage),
              contentDescription = null,
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
    }

    if (viewModel.isLeftTeam) {
      closeBtn()
      Spacer(modifier = Modifier.width(8.dp))
      nameTxt(Modifier.weight(1f))
      statsPill()
    } else {
      statsPill()
      nameTxt(Modifier.weight(1f))
      Spacer(modifier = Modifier.width(8.dp))
      closeBtn()
    }
  }
}
package com.asdru.cardgame3.view.battle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.R
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
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header (Close button + Name)
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = stringResource(viewModel.totem.name),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Icon
                Icon(
                    painter = painterResource(id = viewModel.totem.iconRes),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats (Health & Damage)
                Row(
                   verticalAlignment = Alignment.CenterVertically,
                   modifier = Modifier
                       .fillMaxWidth()
                       .background(Color(0xFF2D2D2D), RoundedCornerShape(8.dp))
                       .padding(12.dp),
                   horizontalArrangement = Arrangement.SpaceAround
                ) {
                   // Health
                   Row(verticalAlignment = Alignment.CenterVertically) {
                       Icon(
                           painter = painterResource(id = R.drawable.icon_health),
                           contentDescription = "Health",
                           tint = Color(0xFFEF5350),
                           modifier = Modifier.size(20.dp)
                       )
                       Spacer(modifier = Modifier.width(8.dp))
                       Text(
                           text = "${viewModel.currentHealth.toInt()}/${viewModel.maxHealth.toInt()}",
                           color = Color.White,
                           fontSize = 16.sp,
                           fontWeight = FontWeight.Bold
                       )
                   }
                   
                   // Damage
                   Row(verticalAlignment = Alignment.CenterVertically) {
                       Icon(
                           painter = painterResource(id = R.drawable.icon_attack_damage),
                           contentDescription = "Damage",
                           tint = Color(0xFFFFCA28),
                           modifier = Modifier.size(20.dp)
                       )
                       Spacer(modifier = Modifier.width(8.dp))
                       Text(
                           text = "${viewModel.damage.toInt()}",
                           color = Color.White,
                           fontSize = 16.sp,
                           fontWeight = FontWeight.Bold
                       )
                   }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                // Ability
                Column(modifier = Modifier.fillMaxWidth()) {
                     Text(
                        text = viewModel.ability.getName(LocalContext.current),
                        color = Color(0xFFE91E63), // Pink for ability
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = viewModel.ability.getDescription(LocalContext.current),
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

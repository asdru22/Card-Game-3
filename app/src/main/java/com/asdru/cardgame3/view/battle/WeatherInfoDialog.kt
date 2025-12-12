package com.asdru.cardgame3.view.battle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.asdru.cardgame3.view.weather.WeatherEvent

@Composable
fun WeatherInfoDialog(
  weather: WeatherEvent,
  onClose: () -> Unit
) {
  Dialog(onDismissRequest = onClose) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .heightIn(max = 400.dp),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = stringResource(id = weather.nameRes),
              color = Color.White,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold
            )
          }

          IconButton(onClick = onClose) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color.Gray
            )
          }
        }

        HorizontalDivider(
          modifier = Modifier.padding(vertical = 12.dp),
          color = Color.Gray.copy(alpha = 0.5f)
        )

        Column(
          modifier = Modifier
            .weight(1f, fill = false)
            .verticalScroll(rememberScrollState())
        ) {
          val desc = if (weather.formatArgs.isNotEmpty()) {
            stringResource(id = weather.descriptionRes, *weather.formatArgs.toTypedArray())
          } else {
            stringResource(id = weather.descriptionRes)
          }

          Text(
            text = desc,
            color = Color.LightGray,
            fontSize = 16.sp,
            lineHeight = 24.sp
          )
        }
      }
    }
  }
}
package com.asdru.cardgame3.view.mainMenu

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.R

enum class HowToPlayTab(@param:StringRes val titleRes: Int) {
  General(R.string.ui_htp_tab_general),
  Shop(R.string.ui_htp_tab_shop),
  Weather(R.string.ui_htp_tab_weather),
  TimedMode(R.string.ui_htp_tab_timed),
  Totems(R.string.ui_htp_tab_totems)
}

@Composable
fun HowToPlayOverlay(
  visible: Boolean,
  onClose: () -> Unit
) {
  var selectedTab by remember { mutableStateOf(HowToPlayTab.General) }

  AnimatedVisibility(
    visible = visible,
    enter = fadeIn(),
    exit = fadeOut(),
    modifier = Modifier.fillMaxSize()
  ) {
    Card(
      colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
      modifier = Modifier
        .fillMaxSize()
        .clickable(enabled = false) {}
    ) {
      Column(
        modifier = Modifier.fillMaxSize()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            HowToPlayTab.entries.forEach { tab ->
              Text(
                text = stringResource(tab.titleRes),
                color = if (selectedTab == tab) Color.Cyan else Color.Gray,
                fontSize = 16.sp,
                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                  .clickable { selectedTab = tab }
                  .padding(4.dp)
              )
            }
          }

          IconButton(onClick = onClose) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color.White
            )
          }
        }

        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
            .padding(bottom = 16.dp)
            .verticalScroll(rememberScrollState())
        ) {
          when (selectedTab) {
            HowToPlayTab.General -> {
              TextSection(R.string.ui_htp_overview, R.string.ui_htp_overview_desc)
              TextSection(R.string.ui_htp_abilities, R.string.ui_htp_abilities_desc)
              TextSection(R.string.ui_htp_rage, R.string.ui_htp_rage_desc)
              TextSection(R.string.ui_htp_effects, R.string.ui_htp_effects_desc)
              TextSection(R.string.ui_htp_traits, R.string.ui_htp_traits_desc)
              TextSection(R.string.ui_htp_charges, R.string.ui_htp_charges_desc)
              TextSection(R.string.ui_htp_character_info, R.string.ui_htp_character_info_desc)
            }
            HowToPlayTab.Shop -> {
              TextSection(R.string.ui_htp_tab_shop, R.string.ui_htp_shop_desc)
            }
            HowToPlayTab.Weather -> {
              TextSection(R.string.ui_htp_tab_weather, R.string.ui_htp_weather_desc)
            }
            HowToPlayTab.TimedMode -> {
              TextSection(R.string.ui_htp_tab_timed, R.string.ui_htp_timed_desc)
            }
            HowToPlayTab.Totems -> {
              TextSection(R.string.ui_htp_tab_totems, R.string.ui_htp_totems_desc)
            }
          }
        }
      }
    }
  }
}

@Composable
fun TextSection(title: Int, text: Int) {
  Spacer(modifier = Modifier.height(24.dp))

  Text(
    text = stringResource(title),
    color = Color.White,
    fontSize = 18.sp,
    fontWeight = FontWeight.Bold
  )

  Spacer(modifier = Modifier.height(8.dp))

  Text(
    text = stringResource(text),
    color = Color.White,
    fontSize = 16.sp,
    lineHeight = 24.sp
  )
}
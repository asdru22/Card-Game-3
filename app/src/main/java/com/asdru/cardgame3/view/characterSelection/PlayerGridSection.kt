package com.asdru.cardgame3.view.characterSelection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.cardgame3.game.entity.Entity
import com.asdru.cardgame3.view.character.CharacterInfoCard
import com.asdru.cardgame3.viewModel.EntityViewModel
import kotlin.reflect.full.createInstance

@Composable
fun PlayerGridSection(
  team: MutableList<Entity>,
  available: List<Entity>,
  color: Color = Color.White,
  isLeft: Boolean
) {
  var infoCharacter by remember { mutableStateOf<Entity?>(null) }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

    LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      contentPadding = PaddingValues(4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(available) { entity ->
        val isSelected = team.any { it::class == entity::class }

        CharacterGridItem(
          entity = entity,
          isSelected = isSelected,
          activeColor = color,
          onSelect = {
            val existing = team.find { it::class == entity::class }
            if (existing != null) {
              team.remove(existing)
            } else if (team.size < 3) {
              val newInstance = entity::class.createInstance()
              team.add(newInstance)
            }
          },
          onInfo = { infoCharacter = entity }
        )
      }
    }

    AnimatedVisibility(
      visible = infoCharacter != null,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      infoCharacter?.let { entity ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .clickable(enabled = true) { }
        ) {
          val tempViewModel = remember(entity) {
            EntityViewModel(entity, isLeft)
          }

          CharacterInfoCard(
            viewModel = tempViewModel,
            onClose = { infoCharacter = null }
          )
        }
      }
    }
  }
}

@Composable
fun CharacterGridItem(
  entity: Entity,
  isSelected: Boolean,
  activeColor: Color,
  onSelect: () -> Unit,
  onInfo: () -> Unit
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
    border = if (isSelected) BorderStroke(2.dp, activeColor) else null,
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(0.85f)
      .clickable { onSelect() }
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          painter = painterResource(id = entity.iconRes),
          tint = entity.color,
          contentDescription = stringResource(id = entity.name),
          modifier = Modifier.size(60.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = stringResource(entity.name),
          color = if (isSelected) Color.White else Color.Gray,
          fontSize = 16.sp,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          maxLines = 1
        )
      }

      IconButton(
        onClick = onInfo,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .size(28.dp)
          .padding(2.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "Info",
          tint = Color.Gray,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}
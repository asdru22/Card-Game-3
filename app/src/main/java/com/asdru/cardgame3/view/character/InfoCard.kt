package com.asdru.cardgame3.view.character

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.asdru.cardgame3.data.DamageType
import com.asdru.cardgame3.game.effect.StatusEffect
import com.asdru.cardgame3.game.trait.Trait
import com.asdru.cardgame3.helper.toRoman
import com.asdru.cardgame3.view.common.SmartDescriptionText
import com.asdru.cardgame3.viewModel.EntityViewModel

@Composable
fun CharacterInfoCard(
  viewModel: EntityViewModel,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
  showAlternates: Boolean = false
) {
  var showRadarGraph by remember { mutableStateOf(false) }

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
      Box(modifier = Modifier.fillMaxWidth()) {

        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.Start
        ) {
          CharacterInfoHeader(
            viewModel = viewModel,
            onClose = onClose,
            onShowRadar = { showRadarGraph = true }
          )

          Spacer(modifier = Modifier.height(12.dp))
          HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
          Spacer(modifier = Modifier.height(12.dp))

          CharacterDetailsBody(viewModel, showAlternates)
        }

        if (showRadarGraph) {
          RadarCard(viewModel) { showRadarGraph = false }
        }
      }
    }
  }
}

@Composable
private fun CharacterInfoHeader(
  viewModel: EntityViewModel,
  onClose: () -> Unit,
  onShowRadar: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Min),
    verticalAlignment = Alignment.CenterVertically
  ) {
    val closeBtn = @Composable { CloseButton(onClose) }


    val nameTxt = @Composable { modifier: Modifier ->
      CharacterName(viewModel.name, onShowRadar, modifier)
    }

    val statsPill = @Composable { CharacterStatsPill(viewModel, onShowRadar) }

    if (viewModel.isLeftTeam) {
      closeBtn()
      Spacer(modifier = Modifier.width(8.dp))

      nameTxt(
        Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
      )

      statsPill()
    } else {
      statsPill()

      nameTxt(
        Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
      )

      Spacer(modifier = Modifier.width(8.dp))
      closeBtn()
    }
  }
}

@Composable
private fun CharacterName(
  nameResId: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var textSize by remember { mutableStateOf(20.sp) }

  Text(
    text = stringResource(nameResId),
    color = Color.White,
    fontSize = textSize,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
    lineHeight = 22.sp,
    maxLines = 1,
    softWrap = false,
    onTextLayout = { textLayoutResult ->
      if (textLayoutResult.didOverflowWidth) {
        textSize *= 0.9f
      }
    },
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable { onClick() }
      .padding(horizontal = 8.dp, vertical = 4.dp)
  )
}

@Composable
private fun CharacterStatsPill(
  viewModel: EntityViewModel,
  onClick: () -> Unit
) {
  Surface(
    color = Color(0xFF2D2D2D),
    shape = RoundedCornerShape(50),
    modifier = Modifier
      .clip(RoundedCornerShape(50))
      .clickable { onClick() }
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
        text = "${viewModel.health.toInt()}/${viewModel.maxHealth.toInt()}",
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )

      if (viewModel.overhealAmount > 0) {
        Text(
          text = "+${viewModel.overhealAmount.toInt()}",
          color = Color(0xFFFFD700),
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(start = 4.dp)
        )
      }

      StatDivider()

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

      StatDivider()

      DamageTypeChip(viewModel.damageType)
    }
  }
}

@Composable
private fun CharacterDetailsBody(
  viewModel: EntityViewModel,
  showAlternates: Boolean
) {
  val context = LocalContext.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Min),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(end = 8.dp)
        .heightIn(max = 250.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // Active Ability
      CharacterAbility(
        context = context,
        label = stringResource(R.string.ui_active),
        ability = viewModel.activeAbility,
        color = Color(0xFF66BB6A),
      )
      if (showAlternates && viewModel.entity.alternateActiveAbilities.isNotEmpty()) {
        viewModel.entity.alternateActiveAbilities.forEach { altAbility ->
          Spacer(modifier = Modifier.height(4.dp))
          CharacterAbility(
            context = context,
            label = stringResource(R.string.ui_active),
            ability = altAbility,
            color = Color(0xFF66BB6A).copy(alpha = 0.7f),
          )
        }
      }

      // Passive Ability
      CharacterAbility(
        context = context,
        label = stringResource(R.string.ui_passive),
        ability = viewModel.entity.passiveAbility,
        color = Color(0xFF42A5F5)
      )

      // Ultimate Ability
      CharacterAbility(
        context = context,
        label = stringResource(R.string.ui_ultimate),
        ability = viewModel.ultimateAbility,
        color = Color(0xFFE91E63)
      )
      if (showAlternates && viewModel.entity.alternateUltimateAbilities.isNotEmpty()) {
        viewModel.entity.alternateUltimateAbilities.forEach { altAbility ->
          Spacer(modifier = Modifier.height(4.dp))
          CharacterAbility(
            context = context,
            label = stringResource(R.string.ui_ultimate),
            ability = altAbility,
            color = Color(0xFFE91E63).copy(alpha = 0.7f),
          )
        }
      }
    }

    if (viewModel.traits.isNotEmpty()) {
      SectionDivider()

      Column(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
          .heightIn(max = 250.dp)
          .verticalScroll(rememberScrollState())
      ) {
        SectionHeader(stringResource(R.string.ui_traits))
        viewModel.traits.forEach {
          Trait(it, context = context)
        }
      }
    }

    if (viewModel.effectManager.effects.isNotEmpty()) {
      SectionDivider()

      Column(
        modifier = Modifier
          .weight(1f)
          .padding(start = 8.dp)
          .heightIn(max = 250.dp)
          .verticalScroll(rememberScrollState())
      ) {
        SectionHeader(stringResource(R.string.ui_effects))
        viewModel.effectManager.effects.forEach {
          Effect(it, context)
        }
      }
    }
  }
}

@Composable
private fun CloseButton(onClose: () -> Unit) {
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

@Composable
private fun StatDivider() {
  VerticalDivider(
    modifier = Modifier
      .padding(horizontal = 8.dp)
      .fillMaxHeight(0.7f),
    color = Color.Gray.copy(alpha = 0.3f),
    thickness = 1.dp
  )
}

@Composable
private fun SectionDivider() {
  VerticalDivider(
    modifier = Modifier
      .fillMaxHeight()
      .padding(vertical = 4.dp),
    color = Color.Gray.copy(alpha = 0.2f),
    thickness = 1.dp
  )
}

@Composable
private fun SectionHeader(text: String) {
  Text(
    text = text,
    color = Color.Gray,
    fontSize = 11.sp,
    fontWeight = FontWeight.Bold,
    modifier = Modifier.padding(bottom = 6.dp)
  )
}


@Composable
fun DamageTypeChip(
  damageType: DamageType,
) {
  Icon(
    painter = painterResource(damageType.iconResId),
    contentDescription = damageType.name,
    tint = damageType.tintColor,
    modifier = Modifier.size(20.dp)
  )
}

@Composable
fun Trait(trait: Trait, context: Context) {
  Column(modifier = Modifier.padding(bottom = 8.dp)) {
    Text(
      text = "• ${trait.getName(context)}",
      color = Color(0xFFFF9800),
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold
    )
    SmartDescriptionText(
      translatable = trait,
      textColor = Color.LightGray
    )
  }
}

@Composable
fun Effect(effect: StatusEffect, context: Context) {
  Column(
    modifier = Modifier.padding(bottom = 8.dp),
    horizontalAlignment = Alignment.Start
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        painter = painterResource(id = effect.iconRes),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = effect.getName(context) + if (effect.multiplier > 1) " " + effect.multiplier.toRoman() else "",
        color = if (effect.isPositive) Color(0xFF00D471) else Color(0xFFBD3BF5),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "x${effect.duration}",
        color = Color.Gray,
        fontSize = 10.sp
      )
    }
    Text(
      text = effect.getDescription(context),
      color = Color.LightGray,
      fontSize = 11.sp,
      lineHeight = 13.sp
    )
  }
}
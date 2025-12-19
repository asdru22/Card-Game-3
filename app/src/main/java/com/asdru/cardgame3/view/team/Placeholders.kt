package com.asdru.cardgame3.view.team

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.min
import com.asdru.cardgame3.viewModel.BattleViewModel
import com.asdru.cardgame3.viewModel.TeamViewModel
import com.asdru.cardgame3.data.Entity

// Placeholder RageBar
@Composable
fun RageBar(
    rage: Float,
    maxRage: Float,
    isTurn: Boolean,
    isDragging: Boolean,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.width(20.dp).background(Color.Red))
}

// Placeholder Shop
@Composable
fun Shop(
    viewModel: Any?, // Type unknown, assuming stub
    onDragStart: (Entity, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    Box(modifier = Modifier.width(100.dp).height(50.dp).background(Color.Yellow))
}

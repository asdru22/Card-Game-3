package com.asdru.cardgame3.view.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.asdru.cardgame3.view.character.popupManager
import com.asdru.cardgame3.viewModel.CharacterViewModel
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.SummonViewModel

@Composable
fun TeamColumn(
    entities: List<EntityViewModel>,
    alignment: Alignment.Horizontal,
    cardWidth: Dp,
    cardHeight: Dp,
    canAct: (EntityViewModel) -> Boolean,
    onCardPositioned: (EntityViewModel, Rect) -> Unit,
    onDragStart: (EntityViewModel, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDoubleTap: (EntityViewModel) -> Unit,
    getHighlightColor: (EntityViewModel) -> Color
) {
    Column(
        horizontalAlignment = alignment
    ) {
        val characters = entities.filterIsInstance<CharacterViewModel>()
        
        characters.forEach { character ->
            // Character Card
            Card(
                entity = character,
                width = cardWidth,
                height = cardHeight,
                onDragStart = { onDragStart(character, it) },
                onDrag = onDrag,
                onDragEnd = onDragEnd,
                onDoubleTap = { onDoubleTap(character) }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Active Summon Card (Next to/Below character in this column layout)
            character.summonManager.activeSummon?.let { summon ->
                 Card(
                    entity = summon,
                    width = cardWidth * 0.8f, // Slightly smaller
                    height = cardHeight * 0.8f,
                    onDragStart = { onDragStart(summon, it) },
                    onDrag = onDrag,
                    onDragEnd = onDragEnd,
                    onDoubleTap = { onDoubleTap(summon) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun Card(
    entity: EntityViewModel,
    width: Dp,
    height: Dp,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDoubleTap: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Text(text = entity.entity.javaClass.simpleName)
    }
}

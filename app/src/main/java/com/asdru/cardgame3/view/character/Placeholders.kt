package com.asdru.cardgame3.view.character

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.asdru.cardgame3.viewModel.EntityViewModel
import com.asdru.cardgame3.viewModel.BattleViewModel
import java.util.UUID

// Placeholder CharacterInfoCard
@Composable
fun CharacterInfoCard(
    viewModel: EntityViewModel,
    onClose: () -> Unit
) {
    Box(modifier = Modifier.size(200.dp).background(Color.Gray))
}

// Placeholder PopupView
data class Popup(val id: UUID = UUID.randomUUID(), val yOffset: Float = 0f)
// Extension for popupManager stub
val EntityViewModel.popupManager: PopupManager
    get() = PopupManager()

class PopupManager {
    val popups = mutableListOf<Popup>()
}

@Composable
fun PopupView(
    popup: Popup,
    parentTranslation: Offset,
    onDismiss: () -> Unit
) {
    Box(modifier = Modifier.size(50.dp).background(Color.White))
}

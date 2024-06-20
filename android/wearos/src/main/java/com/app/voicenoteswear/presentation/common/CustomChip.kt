package com.app.voicenoteswear.presentation.common

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Text
import com.app.voicenoteswear.presentation.theme.Welcome

@Composable
fun CustomChip(
    onCLick: () -> Unit,
    text: String,
) {

    Chip(
        modifier = Modifier.height(32.dp),
        onClick = { onCLick.invoke() },
        enabled = true,
        label = {
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                style = TextStyle.Welcome
            )
        },
    )
}
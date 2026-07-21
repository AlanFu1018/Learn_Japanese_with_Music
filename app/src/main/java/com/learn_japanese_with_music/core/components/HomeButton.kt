package com.learn_japanese_with_music.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

//@Composable
//fun HomeRectangleButton(onClick: () -> Unit) {
//    Box(
//        modifier = Modifier
//            .size(54.dp)
//            .clip(RoundedCornerShape(12.dp))
//            .background(MaterialTheme.colorScheme.primaryContainer)
//            .clickable { onClick() },
//        contentAlignment = Alignment.Center
//    ) {
//        Icon(
//            imageVector = Icons.Default.Menu,
//            contentDescription = "Menu",
//            tint = MaterialTheme.colorScheme.onPrimaryContainer,
//            modifier = Modifier.size(36.dp)
//        )
//    }
//}
@Composable
fun HomeRectangleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledIconButton (
        onClick = onClick,
        modifier = modifier.size(59.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            modifier = Modifier.size(35.dp)
        )
    }
}
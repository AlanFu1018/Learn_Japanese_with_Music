package com.learn_japanese_with_music.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeRectangleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledIconButton (
        onClick = onClick,
        modifier = modifier.size(56.dp),
        //padding(end = 8.dp),
        shape = CircleShape,
            //RoundedCornerShape(8.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
//        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home"
        )
    }
}

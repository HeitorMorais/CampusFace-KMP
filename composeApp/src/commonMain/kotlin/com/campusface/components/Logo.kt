package com.campusface.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import campusface.composeapp.generated.resources.Res
import campusface.composeapp.generated.resources.campusface_logo
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun Logo(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(Res.drawable.campusface_logo),
        contentDescription = "Logo",
        modifier = modifier
    )
}

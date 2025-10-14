package com.campusface.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val LightColorTheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,
    surface = Surface,
    background = Background,
)



@Composable
fun NoteMarkTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorTheme,
        typography = Typography,
        content = content
    )
}
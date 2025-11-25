package com.campusface.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Background = Color(0xFFFFFFFF)
val Primary = Color(0xFF1C1B1F)
val Secondary = Color(0xFF848484)
val Tertiary = Color(0xFFEDEDED)
val Surface = Color(0xFF1C1B1F)

val SurfaceContainer = Color.Transparent
val LightColorTheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,
    surface = Surface,
    background = Background,
    surfaceContainer = SurfaceContainer
)
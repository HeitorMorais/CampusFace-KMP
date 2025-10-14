package com.campusface.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import campusface.composeapp.generated.resources.PublicSans_Bold
import campusface.composeapp.generated.resources.PublicSans_Medium
import campusface.composeapp.generated.resources.PublicSans_Regular
import campusface.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

val PublicSans @Composable get() = FontFamily(
    Font(
        resource = Res.font.PublicSans_Bold,
        weight = FontWeight.Bold
    ),
    Font(
        resource = Res.font.PublicSans_Regular,
        weight = FontWeight.Normal
    ),
    Font(
        resource = Res.font.PublicSans_Medium,
        weight = FontWeight.Medium
    ),
)

val Typography : Typography @Composable get() = Typography(
    bodyLarge = TextStyle(
        fontFamily = PublicSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PublicSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = PublicSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PublicSans,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 36.sp
    ),
    titleSmall = TextStyle(
        fontFamily = PublicSans,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
)
package com.campusface.data

import androidx.compose.runtime.staticCompositionLocalOf

// CompositionLocal para compartilhar token e userId globalmente
val LocalAuthToken = staticCompositionLocalOf<String?> { null }
val LocalUserId = staticCompositionLocalOf<String?> { null }
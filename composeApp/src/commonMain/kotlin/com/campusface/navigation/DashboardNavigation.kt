package com.campusface.navigation

// commonMain/kotlin/DashboardNavigation.kt

import androidx.compose.runtime.Immutable

@Immutable
sealed class DashboardScreen {
    data object Overview : DashboardScreen()
    data object Settings : DashboardScreen()
    data class Reports(val reportId: String) : DashboardScreen()
}
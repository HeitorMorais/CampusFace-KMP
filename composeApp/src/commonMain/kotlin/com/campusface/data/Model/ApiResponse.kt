package com.campusface.data.Model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val message: String,
    val success: Boolean,
    val data: T? = null  // 👈 Nullable para casos onde data pode não vir
)
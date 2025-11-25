package com.campusface.auth

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class User(val id: String, val name: String)

data class AuthState(
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val isLoading: Boolean = false
)

class AuthRepository {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(username: String) {
        // Simulação de login
        _authState.value = AuthState(isAuthenticated = true, user = User("1", username))
    }

    fun logout() {
        _authState.value = AuthState(isAuthenticated = false)
    }
}

// O "Provider" (CompositionLocal)
val LocalAuthRepository = staticCompositionLocalOf<AuthRepository> {
    error("AuthRepository não foi fornecido")
}
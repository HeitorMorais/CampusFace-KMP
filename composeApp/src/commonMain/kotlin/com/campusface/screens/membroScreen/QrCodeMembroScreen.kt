package com.campusface.screens.membroScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// IMPORTS OBRIGATÓRIOS PARA DATA/HORA
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

// Imports do seu projeto
import com.campusface.components.AdaptiveScreenContainer
import com.campusface.data.Repository.GeneratedCodeData
import com.campusface.data.Repository.LocalAuthRepository
import com.campusface.data.Repository.ValidationRepository
import qrgenerator.qrkitpainter.rememberQrKitPainter

// ==========================================
// 1. VIEW MODEL & STATE
// ==========================================

data class QrCodeUiState(
    val isLoading: Boolean = true,
    val codeData: GeneratedCodeData? = null,
    val secondsRemaining: Long = 0,
    val error: String? = null
)

class QrCodeViewModel(
    private val repository: ValidationRepository = ValidationRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrCodeUiState())
    val uiState = _uiState.asStateFlow()

    fun loadQrCode(organizationId: String, token: String?) {
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "Token inválido") }
            return
        }

        if (_uiState.value.codeData != null) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        repository.generateQrCode(
            organizationId = organizationId,
            token = token,
            onSuccess = { data ->
                _uiState.update { it.copy(isLoading = false, codeData = data) }
                startCountdown(data.expirationTime)
            },
            onError = { msg ->
                _uiState.update { it.copy(isLoading = false, error = msg) }
            }
        )
    }

    private fun startCountdown(expirationIsoString: String) {
        viewModelScope.launch {
            try {
                // 1. Converte a string da API para Segundos (Long)
                // Se der erro aqui, é porque a string não é ISO-8601 válida
                val expirationSeconds = Instant.parse(expirationIsoString).epochSeconds

                while (true) {
                    // 2. Pega a hora atual em Segundos (Long)
                    // Clock.System.now() funciona em Android e iOS
                    val nowSeconds = Clock.System.now().epochSeconds

                    // 3. Subtração simples de números inteiros (evita erro de operador)
                    val remaining = expirationSeconds - nowSeconds

                    if (remaining <= 0) {
                        _uiState.update {
                            it.copy(secondsRemaining = 0, error = "Código expirado. Volte e gere novamente.")
                        }
                        break
                    }

                    _uiState.update { it.copy(secondsRemaining = remaining) }
                    delay(1000)
                }
            } catch (e: Exception) {
                println("Erro ao iniciar timer: ${e.message}")
            }
        }
    }
}

// ==========================================
// 2. TELA PRINCIPAL (UI)
// ==========================================

@Composable
fun QrCodeMembroScreen(
    navController: NavHostController,
    // TODO: Receba este ID via navegação para não ficar fixo
    organizationId: String = "ID_DA_ORGANIZACAO",
    viewModel: QrCodeViewModel = viewModel { QrCodeViewModel() }
) {
    val authRepository = LocalAuthRepository.current
    val authState by authRepository.authState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadQrCode(organizationId, authState.token)
    }

    AdaptiveScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                }
                Spacer(Modifier.width(8.dp))
                Text("Acesso via QR Code", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(20.dp))

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Gerando código seguro...", style = MaterialTheme.typography.bodyMedium)
                }

                uiState.error != null -> {
                    Icon(Icons.Default.Refresh, "Erro", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { navController.popBackStack() }, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Voltar")
                    }
                }

                uiState.codeData != null -> {
                    val code = uiState.codeData!!.code
                    val painter = rememberQrKitPainter(data = code)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(8.dp),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = "QR Code",
                                modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                            )
                        }
                    }

                    Text(
                        text = code.chunked(3).joinToString(" "),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(24.dp))

                    if (uiState.secondsRemaining > 0) {
                        Text(
                            "Expira em ${uiState.secondsRemaining} segundos",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (uiState.secondsRemaining < 10) Color.Red else MaterialTheme.colorScheme.onBackground
                        )
                    } else {
                        Text("Código Expirado", style = MaterialTheme.typography.labelLarge, color = Color.Red)
                    }
                }
            }
        }
    }
}
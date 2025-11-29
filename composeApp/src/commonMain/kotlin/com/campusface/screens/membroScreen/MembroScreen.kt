package com.campusface.screens.membroScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Imports do seu projeto
import com.campusface.components.AdaptiveScreenContainer
import com.campusface.navigation.DashboardRoute
import com.campusface.data.Repository.EntryRequest
import com.campusface.data.Repository.EntryRequestRepository
import com.campusface.data.Repository.LocalAuthRepository

// ==========================================
// 1. VIEW MODEL & STATE
// ==========================================

data class MembroUiState(
    val isLoading: Boolean = false,
    val requests: List<EntryRequest> = emptyList(),
    val error: String? = null
)

class MembroViewModel(
    private val repository: EntryRequestRepository = EntryRequestRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MembroUiState())
    val uiState = _uiState.asStateFlow()

    fun fetchMyRequests(token: String?) {
        if (token.isNullOrBlank()) return

        // Inicia Loading
        _uiState.update { it.copy(isLoading = true, error = null) }

        repository.listMyRequests(
            token = token,
            onSuccess = { list ->
                _uiState.update {
                    it.copy(isLoading = false, requests = list)
                }
            },
            onError = { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error)
                }
            }
        )
    }
}

// ==========================================
// 2. TELA PRINCIPAL (UI)
// ==========================================

@Composable
fun MembroScreen(
    navController: NavHostController,
    // Injeção do ViewModel usando lambda factory para evitar erros de reflexão
    viewModel: MembroViewModel = viewModel { MembroViewModel() }
) {
    // 1. Pega o token do repositório local
    val authRepository = LocalAuthRepository.current
    val authState by authRepository.authState.collectAsState()

    // 2. Observa o estado da UI vindo do ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // 3. Busca os dados assim que a tela abre
    LaunchedEffect(Unit) {
        viewModel.fetchMyRequests(authState.token)
    }

    AdaptiveScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Alterado para Top para a lista ocupar o espaço corretamente
            verticalArrangement = Arrangement.Top
        ) {

            // --- Cabeçalho e Botão Adicionar ---
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Hubs que sou membro", style = MaterialTheme.typography.titleMedium)

                Button(
                    onClick = {
                        // Navega para a tela de solicitar entrada em novo Hub
                        navController.navigate(DashboardRoute.AdicionarMembro)
                    },
                    modifier = Modifier
                ) {
                    Text("Adicionar", style = MaterialTheme.typography.labelMedium)
                }
            }

            // --- Conteúdo da Lista ---
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Erro ao carregar:", color = MaterialTheme.colorScheme.error)
                        Text(uiState.error!!, style = MaterialTheme.typography.bodySmall)
                        Button(onClick = { viewModel.fetchMyRequests(authState.token) }) {
                            Text("Tentar Novamente")
                        }
                    }
                }
            } else {
                // Lista de Sucesso
                HubListFromApi(
                    requests = uiState.requests,
                    navController = navController,
                    isValidator = false
                )
            }
        }
    }
}

// ==========================================
// 3. COMPONENTES AUXILIARES
// ==========================================

@Composable
fun HubListFromApi(
    requests: List<EntryRequest>,
    navController: NavHostController,
    isValidator: Boolean
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        if (requests.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "Você ainda não solicitou entrada em nenhum Hub.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            items(requests) { request ->
                RequestCard(
                    request = request,
                    navController = navController,
                    isValidator = isValidator
                )
            }
        }
    }
}

@Composable
fun RequestCard(
    request: EntryRequest,
    navController: NavHostController,
    isValidator: Boolean
) {
    // Lógica de Cores e Texto baseada no Status da API
    val (statusColor, statusText) = when (request.status) {
        "APPROVED" -> Color(0xFF00A12B) to "Ativo"       // Verde
        "PENDING" -> Color(0xFFFFBB00) to "Solicitado"   // Amarelo
        "DENIED" -> Color(0xFFB00020) to "Recusado"      // Vermelho
        else -> Color.Gray to request.status
    }

    // Apenas aprovados podem clicar para ver QR Code
    val isClickable = request.status == "APPROVED"

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(
                if (isClickable) {
                    Modifier.clickable(onClick = {
                        navController.navigate(if (isValidator) DashboardRoute.QrCodeValidador else DashboardRoute.QrCodeMembro)
                    })
                } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp) // Padding ajustado
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nome do Hub (usando hubCode)
            Text(
                text = request.hubCode,
                style = MaterialTheme.typography.bodyLarge
            )

            // Status com bolinha colorida
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusCircle(color = statusColor, tamanho = 10.dp)
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatusCircle(tamanho: Dp = 12.dp, color: Color) {
    Box(
        modifier = Modifier
            .size(tamanho)
            .background(color = color, shape = CircleShape)
    )
}
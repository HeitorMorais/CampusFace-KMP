package com.campusface.screens.membroScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- IMPORTS DO SEU PROJETO ---
import com.campusface.components.AdaptiveScreenContainer
import com.campusface.navigation.DashboardRoute
import com.campusface.data.Repository.LocalAuthRepository
import com.campusface.data.Repository.EntryRequestRepository
import com.campusface.data.Repository.OrganizationRepository
import com.campusface.data.Repository.EntryRequest
import com.campusface.data.Model.Organization
import com.campusface.utils.AppEventBus // Certifique-se de que este arquivo existe

// ==========================================
// 1. VIEW MODEL & STATE
// ==========================================

data class MembroUiState(
    val isLoading: Boolean = false,
    val activeHubs: List<Organization> = emptyList(), // Hubs que já sou membro (Verdes)
    val pendingRequests: List<EntryRequest> = emptyList(), // Solicitações pendentes/recusadas
    val error: String? = null
)

class MembroViewModel(
    private val orgRepo: OrganizationRepository = OrganizationRepository(),
    private val entryRepo: EntryRequestRepository = EntryRequestRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MembroUiState())
    val uiState = _uiState.asStateFlow()

    private var isLoaded = false
    private var savedToken: String? = null

    init {
        // Escuta eventos globais para recarregar a lista automaticamente
        viewModelScope.launch {
            AppEventBus.refreshFlow.collect {
                if (!savedToken.isNullOrBlank()) {
                    fetchAllData(savedToken, forceReload = true)
                }
            }
        }
    }

    fun fetchAllData(token: String?, forceReload: Boolean = false) {
        if (token.isNullOrBlank()) return
        savedToken = token // Salva o token para o refresh automático

        if (isLoaded && !forceReload) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        // 1. Busca Hubs Ativos
        orgRepo.getMyHubs(
            token = token,
            onSuccess = { hubs ->
                // 2. Busca Solicitações (independente do sucesso dos hubs, tentamos carregar o resto)
                fetchRequests(token, hubs)
            },
            onError = { error ->
                // Se falhar os hubs, tenta carregar as solicitações mesmo assim
                fetchRequests(token, emptyList(), error)
            }
        )
    }

    private fun fetchRequests(token: String, currentHubs: List<Organization>, previousError: String? = null) {
        entryRepo.listMyRequests(
            token = token,
            onSuccess = { requests ->
                isLoaded = true
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeHubs = currentHubs,
                        pendingRequests = requests,
                        error = previousError // Mantém o erro da primeira chamada se houver
                    )
                }
            },
            onError = { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeHubs = currentHubs,
                        pendingRequests = emptyList(),
                        error = previousError ?: error
                    )
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
    viewModel: MembroViewModel = viewModel { MembroViewModel() }
) {
    val authRepository = LocalAuthRepository.current
    val authState by authRepository.authState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Carrega tudo ao entrar na tela
    LaunchedEffect(Unit) {
        viewModel.fetchAllData(authState.token)
    }

    AdaptiveScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // --- Cabeçalho e Botão Adicionar ---
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Meus Hubs", style = MaterialTheme.typography.titleMedium)

                Button(
                    onClick = {
                        // Navega para tela de solicitar entrada passando a role "MEMBER"
                        navController.navigate(DashboardRoute.AdicionarMembro(role = "MEMBER"))
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
            } else {
                UnifiedHubList(
                    activeHubs = uiState.activeHubs,
                    pendingRequests = uiState.pendingRequests,
                    navController = navController,
                    isValidator = false,
                    error = uiState.error,
                    onRetry = { viewModel.fetchAllData(authState.token, true) }
                )
            }
        }
    }
}

// ==========================================
// 3. COMPONENTES AUXILIARES (Cards e Lista)
// ==========================================

@Composable
fun UnifiedHubList(
    activeHubs: List<Organization>,
    pendingRequests: List<EntryRequest>,
    navController: NavHostController,
    isValidator: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {

        // Se houver erro de rede, mostra aviso no topo com botão de retry
        if (error != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Erro ao carregar: $error", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    IconButton(onClick = onRetry) {
                        Icon(Icons.Default.Refresh, "Recarregar")
                    }
                }
            }
        }

        // 1. HUBs ATIVOS (Vindos do OrganizationRepository)
        if (activeHubs.isNotEmpty()) {
            items(activeHubs) { org ->
                UnifiedCard(
                    title = org.name,
                    subtitle = org.hubCode,
                    status = "Ativo",
                    statusColor = Color(0xFF00A12B), // Verde
                    isClickable = true,
                    onClick = {
                        // Se for membro, vai para a tela de QR Code passando o ID
                        if (isValidator) {
                            navController.navigate(DashboardRoute.QrCodeValidador)
                        } else {
                            navController.navigate(DashboardRoute.QrCodeMembro(organizationId = org.id))
                        }
                    }
                )
            }
        }

        // 2. SOLICITAÇÕES (Vindas do EntryRequestRepository)
        // Filtramos APPROVED para não duplicar visualmente (pois já devem estar na lista de ativos acima)
        val visibleRequests = pendingRequests.filter { it.status != "APPROVED" }

        if (visibleRequests.isNotEmpty()) {
            items(visibleRequests) { req ->
                val (color, text) = when(req.status) {
                    "PENDING" -> Color(0xFFFFBB00) to "Solicitado" // Amarelo
                    "DENIED" -> Color(0xFFB00020) to "Recusado"    // Vermelho
                    else -> Color.Gray to req.status
                }

                UnifiedCard(
                    title = req.hubCode, // EntryRequest só tem o código do hub
                    subtitle = "Aguardando aprovação",
                    status = text,
                    statusColor = color,
                    isClickable = false, // Pendente não gera QR Code
                    onClick = {}
                )
            }
        }

        // Estado Vazio Global
        if (activeHubs.isEmpty() && visibleRequests.isEmpty() && error == null) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "Nenhum hub ou solicitação encontrada.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun UnifiedCard(
    title: String,
    subtitle: String,
    status: String,
    statusColor: Color,
    isClickable: Boolean,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(
                if (isClickable) Modifier.clickable { onClick() } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(statusColor, CircleShape)
                )
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
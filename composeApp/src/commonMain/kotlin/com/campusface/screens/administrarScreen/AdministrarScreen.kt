package com.campusface.screens.administrarScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Imports do seu projeto
import com.campusface.components.AdaptiveScreenContainer
import com.campusface.data.Model.Organization
import com.campusface.data.Repository.LocalAuthRepository
import com.campusface.data.Repository.OrganizationRepository
import com.campusface.navigation.DashboardRoute

// ==========================================
// 1. VIEW MODEL & STATE
// ==========================================

data class AdministrarUiState(
    val isLoading: Boolean = false,
    val adminHubs: List<Organization> = emptyList(), // Lista filtrada (Só onde sou Admin)
    val error: String? = null
)

class AdministrarViewModel(
    private val repository: OrganizationRepository = OrganizationRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdministrarUiState())
    val uiState = _uiState.asStateFlow()

    private var isLoaded = false

    fun fetchAdminHubs(token: String?, currentUserId: String?) {
        if (token.isNullOrBlank() || currentUserId.isNullOrBlank()) return

        // Evita recarregar se já tiver dados (opcional, para economizar rede)
        if (isLoaded) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        repository.getMyHubs(
            token = token,
            onSuccess = { allHubs ->
                isLoaded = true

                // LÓGICA DE FILTRAGEM:
                // O endpoint retorna tudo. Aqui filtramos apenas onde meu ID está na lista de 'admins'.
                val onlyAdminHubs = allHubs.filter { org ->
                    org.admins.any { user -> user.id == currentUserId }
                }

                _uiState.update {
                    it.copy(isLoading = false, adminHubs = onlyAdminHubs)
                }
            },
            onError = { errorMsg ->
                _uiState.update {
                    it.copy(isLoading = false, error = errorMsg)
                }
            }
        )
    }

    fun refresh(token: String?, userId: String?) {
        isLoaded = false
        fetchAdminHubs(token, userId)
    }
}

// ==========================================
// 2. TELA PRINCIPAL (UI)
// ==========================================

@Composable
fun AdministrarScreen(
    navController: NavHostController,
    viewModel: AdministrarViewModel = viewModel { AdministrarViewModel() }
) {
    // 1. Pega Token e ID do Usuário Logado
    val authRepository = LocalAuthRepository.current
    val authState by authRepository.authState.collectAsState()

    // 2. Observa estado da tela
    val uiState by viewModel.uiState.collectAsState()

    // 3. Carrega dados ao entrar
    LaunchedEffect(Unit) {
        viewModel.fetchAdminHubs(authState.token, authState.user?.id)
    }

    AdaptiveScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Cabeçalho ---
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Hubs que administro", style = MaterialTheme.typography.titleMedium)

                Button(
                    onClick = {
                        navController.navigate(DashboardRoute.CriarHub)
                    },
                ) {
                    Text("Criar", style = MaterialTheme.typography.labelMedium)
                }
            }

            // --- Conteúdo ---
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Erro: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                            IconButton(onClick = { viewModel.refresh(authState.token, authState.user?.id) }) {
                                Icon(Icons.Default.Refresh, "Tentar Novamente")
                            }
                        }
                    }
                }

                uiState.adminHubs.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Você ainda não administra nenhum Hub.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(uiState.adminHubs) { org ->
                            HubAdminCard(
                                organization = org,
                                onHubClick = { hubId ->
                                    navController.navigate(
                                        DashboardRoute.DetalhesHub(hubId = hubId)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. COMPONENTES (CARD)
// ==========================================

@Composable
fun HubAdminCard(
    organization: Organization,
    onHubClick: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = { onHubClick(organization.id) }),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = organization.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = organization.hubCode,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Groups,
                    contentDescription = "Membros",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                // CÁLCULO CORRIGIDO: Soma o tamanho de todas as listas de usuários
                val totalPessoas = organization.members.size +
                        organization.admins.size +
                        organization.validators.size

                Text(text = "$totalPessoas", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
package com.campusface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Imports do seu projeto
import com.campusface.components.AdaptiveScreenContainer
import com.campusface.data.Model.User
import com.campusface.data.Repository.LocalAuthRepository
import com.campusface.data.Repository.UserRepository
import com.campusface.screens.administrarScreen.buildImageUrl // Helper de URL
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes

// ==========================================
// 1. VIEW MODEL & STATE
// ==========================================

data class PerfilUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

class MeuPerfilViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState = _uiState.asStateFlow()

    // Carregar Perfil (GET)
    fun loadUserProfile(userId: String, token: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            userRepository.getUser(userId, token)
                .onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, user = user) }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message) }
                }
        }
    }

    // Salvar Alterações (Texto e/ou Imagem)
    fun saveChanges(
        token: String,
        newName: String,
        newEmail: String,
        newDoc: String,
        newImageBytes: ByteArray?
    ) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // 1. Se tiver imagem nova, envia a imagem (PATCH)
                if (newImageBytes != null) {
                    val resultImg = userRepository.updateProfileImage(newImageBytes, token)
                    // Se falhar a imagem, para e mostra erro
                    if (resultImg.isFailure) throw resultImg.exceptionOrNull()!!

                    // Atualiza localmente com o resultado da imagem
                    resultImg.onSuccess { u -> _uiState.update { it.copy(user = u) } }
                }

                // 2. Envia os dados de texto (PUT)
                // Enviamos sempre para garantir consistência
                val resultText = userRepository.updateUserData(newName, newEmail, newDoc, token)

                resultText.onSuccess { updatedUser ->
                    _uiState.update {
                        it.copy(isLoading = false, isSuccess = true, user = updatedUser)
                    }
                }.onFailure { throw it }

            } catch (e: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Erro desconhecido") }
            }
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}

// ==========================================
// 2. TELA PRINCIPAL (UI)
// ==========================================

@Composable
fun MeuPerfilScreen(
    viewModel: MeuPerfilViewModel = viewModel { MeuPerfilViewModel() }
) {
    // 1. Contexto de Autenticação
    val authRepository = LocalAuthRepository.current
    val authState by authRepository.authState.collectAsState()

    // 2. Estado da Tela
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // 3. Campos Editáveis
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var document by remember { mutableStateOf("") }

    // 4. Imagem
    var newImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var selectedImagePreview by remember { mutableStateOf<Any?>(null) }

    // Flag para evitar sobrescrever o que o usuário está digitando se a tela recompor
    var isDataLoaded by remember { mutableStateOf(false) }

    // 5. Carrega dados ao entrar
    LaunchedEffect(Unit) {
        if (authState.user != null && !authState.token.isNullOrBlank()) {
            viewModel.loadUserProfile(authState.user!!.id, authState.token!!)
        }
    }

    // 6. Preenche os campos quando chegam da API (apenas na primeira vez)
    LaunchedEffect(uiState.user) {
        if (uiState.user != null && !isDataLoaded) {
            nome = uiState.user!!.fullName
            email = uiState.user!!.email
            document = uiState.user!!.document ?: ""
            isDataLoaded = true
        }
    }

    // 7. Feedback de Sucesso
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar("Perfil atualizado com sucesso!")
            viewModel.resetSuccess()
            newImageBytes = null // Limpa a seleção pendente
            selectedImagePreview = null
        }
    }

    // 8. Feedback de Erro
    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
    }

    // 9. Seletor de Arquivos
    val launcher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        if (file != null) {
            selectedImagePreview = file
            scope.launch { newImageBytes = file.readBytes() }
        }
    }

    // Habilita botão se mudou texto OU imagem
    val hasTextChanges = uiState.user?.let {
        it.fullName != nome || it.email != email || (it.document ?: "") != document
    } ?: false
    val hasImageChanges = newImageBytes != null

    AdaptiveScreenContainer {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { padding ->

            if (uiState.isLoading && !isDataLoaded) {
                // Loading inicial
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 30.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(40.dp))

                    // --- AVATAR CLICÁVEL ---
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0F0F0))
                            .border(2.dp, Color(0xFFE0E0E0), CircleShape)
                            .clickable { launcher.launch() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImagePreview != null) {
                            AsyncImage(
                                model = selectedImagePreview,
                                contentDescription = "Nova foto",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (!uiState.user?.faceImageId.isNullOrBlank()) {
                            AsyncImage(
                                model = buildImageUrl(uiState.user?.faceImageId),
                                contentDescription = "Foto atual",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(60.dp))
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .padding(6.dp)
                        ) {
                            Icon(Icons.Default.AddAPhoto, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text("Toque na foto para alterar", fontSize = 12.sp, color = Color.Gray)

                    Spacer(Modifier.height(30.dp))

                    // --- CAMPOS EDITÁVEIS ---

                    PerfilInput(
                        value = nome,
                        onValueChange = { nome = it },
                        placeholder = "Nome Completo"
                    )
                    Spacer(Modifier.height(15.dp))

                    PerfilInput(
                        value = document,
                        onValueChange = { document = it },
                        placeholder = "Documento"
                    )
                    Spacer(Modifier.height(15.dp))

                    PerfilInput(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "E-mail"
                    )

                    Spacer(Modifier.height(40.dp))

                    // --- BOTÃO SALVAR ---
                    Button(
                        onClick = {
                            if (authState.user != null) {
                                viewModel.saveChanges(
                                    token = authState.token!!,
                                    newName = nome,
                                    newEmail = email,
                                    newDoc = document,
                                    newImageBytes = newImageBytes
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            disabledContainerColor = Color.Gray
                        ),
                        // Habilita se houve mudança e não está carregando
                        enabled = !uiState.isLoading && (hasTextChanges || hasImageChanges)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                "Salvar Alterações",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    // --- SAIR ---
                    Spacer(Modifier.height(16.dp))
                    TextButton(onClick = { authRepository.logout() }) {
                        Text("Sair da conta", color = Color.Red)
                    }
                }
            }
        }
    }
}

// --- INPUT FIELD ---
@Composable
fun PerfilInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = ""
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F1F1)),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = Color(0xFF9E9E9E),
                modifier = Modifier.padding(start = 20.dp),
                fontSize = 16.sp
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            )
        )
    }
}
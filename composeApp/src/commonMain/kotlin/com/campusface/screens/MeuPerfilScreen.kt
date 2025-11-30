package com.campusface.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import campusface.composeapp.generated.resources.Res
import campusface.composeapp.generated.resources.profile_placeholder
import com.campusface.components.AdaptiveScreenContainer
import com.campusface.data.Model.User
import com.campusface.data.Repository.UserRepository
import com.campusface.data.LocalAuthToken  // 👈 IMPORTA
import com.campusface.data.LocalUserId     // 👈 IMPORTA
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun MeuPerfilScreen() {
    // 👇 CONSOME TOKEN E USERID DO COMPOSITIONLOCAL
    val token = LocalAuthToken.current
    val userId = LocalUserId.current

    // Validação: se não tiver token ou userId, não renderiza nada
    if (token == null || userId == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Erro: Usuário não autenticado")
        }
        return
    }

    // 👇 CRIA O REPOSITORY COM O TOKEN
    val repository = remember(token) { UserRepository(token) }

    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }

    var nome by remember { mutableStateOf("") }
    var document by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        isLoading = true
        println("🔍 DEBUG: Buscando usuário com ID: $userId")
        repository.getUser(userId)
            .onSuccess {
                println("✅ DEBUG: Usuário recebido: $it")
                user = it
                nome = it.fullName
                document = it.document
                email = it.email
                println("✅ DEBUG: Campos atualizados - nome: $nome, document: $document, email: $email")
            }
            .onFailure {
                println("❌ DEBUG: Erro ao buscar usuário: ${it.message}")
                error = it.message
            }
        isLoading = false
    }

    AdaptiveScreenContainer {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading && user == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(Modifier.height(40.dp))

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable {
                                // TODO: Abrir seletor de imagem
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.profile_placeholder),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Foto de perfil",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )

                    Spacer(Modifier.height(30.dp))

                    PerfilInput(
                        value = nome,
                        onValueChange = { nome = it },
                        placeholder = "Nome completo"
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

                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                // TODO: Adicionar endpoint de atualização
                                showSuccess = true
                                isLoading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                "Salvar alterações",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Mensagens de erro e sucesso
            if (error != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = Color(0xFFE53935)
                ) {
                    Text(error ?: "Erro desconhecido")
                }
            }

            if (showSuccess) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showSuccess = false
                }
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Text("Alterações salvas com sucesso!")
                }
            }
        }
    }
}

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
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            ),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 16.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
                innerTextField()
            }
        )
    }
}
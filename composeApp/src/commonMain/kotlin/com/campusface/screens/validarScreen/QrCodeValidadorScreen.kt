package com.campusface.screens.validarScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusface.components.AdaptiveScreenContainer
import com.campusface.isCameraSupported

@Composable
fun QrCodeValidadorScreen(navController : NavHostController) {
    AdaptiveScreenContainer(){
        Column(modifier = Modifier.fillMaxSize()){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar para lista")
                }
                Spacer(Modifier.width(8.dp))
                if(isCameraSupported()) {
                    Text("Scanner disponivel")
                } else {
                    Text("Scanner indisponivel")
                }

//                        @Composable
//                        fun QrCodeScannerScreen() {
//                            var scannedCode by remember { mutableStateOf("Nenhum código escaneado") }
//                            var flashlightOn by remember { mutableStateOf(false) }
//
//                            // 1. Você deve primeiro verificar e solicitar a permissão de CÂMERA aqui.
//                            // ...
//
//                            // Coluna para organizar o scanner e o resultado
//                            Column(
//                                modifier = Modifier.fillMaxSize(),
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                // O Composable principal do scanner
//                                QrScanner(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(300.dp) // Defina o tamanho da área de visualização da câmera
//                                        .clip(RoundedCornerShape(8.dp)), // Opcional: para cantos arredondados
//
//                                    flashlightOn = flashlightOn, // Controla se a lanterna está ligada
//                                    launchGallery = false, // Define se deve permitir a seleção de imagem da galeria
//
//                                    // Callback chamado quando um QR Code é escaneado com sucesso
//                                    onCompletion = { qrCodeValue ->
//                                        scannedCode = qrCodeValue // Armazena o valor do código
//                                        Log.d("QrScanner", "QR Code lido: $qrCodeValue")
//                                    },
//
//                                    // Callback chamado em caso de falha (ex: erro de câmera)
//                                    onFailure = { errorMessage ->
//                                        Log.e("QrScanner", "Falha no scanner: $errorMessage")
//                                    },
//
//                                    // Callback para lidar com a seleção da galeria (se launchGallery for true)
//                                    onGalleryCallBackHandler = { isOpen ->
//                                        // Trata a abertura/fechamento da galeria
//                                    }
//                                )
//
//                                // Exibe o resultado do scan
//                                Text(
//                                    text = "Resultado do Scan: $scannedCode",
//                                    modifier = Modifier.padding(16.dp)
//                                )
//
//                                // Botão opcional para ligar/desligar a lanterna
//                                Button(onClick = { flashlightOn = !flashlightOn }) {
//                                    Text(if (flashlightOn) "Desligar Lanterna" else "Ligar Lanterna")
//                                }
//                            }
//                        }
            }

        }
    }
}
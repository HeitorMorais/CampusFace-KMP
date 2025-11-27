package com.campusface.screens.validarScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusface.components.AdaptiveScreenContainer
import com.campusface.isCameraSupported
import kotlinx.coroutines.delay
import qrscanner.CameraLens
import qrscanner.OverlayShape
import qrscanner.QrScanner


@Composable
fun QrCodeValidadorScreen(navController : NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    // val clipboardManager: Clipboard = LocalClipboardManager.current as Clipboard
    val snackbarHostState = remember { SnackbarHostState() }
    val zoomLevels = listOf(1f, 2f, 3f)
    var selectedZoomIndex = 0
    var memberCode = "teste.com"
    var qrCodeURL by remember { mutableStateOf("") }
    var flashlightOn by remember { mutableStateOf(false) }
    var openImagePicker by remember { mutableStateOf(false) }
    var overlayShape by remember { mutableStateOf(OverlayShape.Square) }
    var cameraLens by remember { mutableStateOf(CameraLens.Back) }
    var currentZoomLevel by remember { mutableStateOf(zoomLevels[selectedZoomIndex]) }
    AdaptiveScreenContainer(){
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar para lista", modifier = Modifier
                        .background(
                            color = Color.White, // 🔑
                            shape = RoundedCornerShape(8.dp)
                        ))
                }
                Spacer(Modifier.width(8.dp))
                if(isCameraSupported()) {
                    Column(modifier = Modifier.fillMaxSize()){
                        if(qrCodeURL==memberCode) TextoSucesso(texto = "Acesso Permitido", navController = navController)
                    QrScanner(
                        modifier = Modifier.fillMaxSize(),
                        flashlightOn = flashlightOn,
                        cameraLens = cameraLens,
                        openImagePicker = openImagePicker,
                        onCompletion = { qrCodeURL = it },
                        zoomLevel = currentZoomLevel,
                        maxZoomLevel = 3f,
                        imagePickerHandler = { openImagePicker = it },
                        onFailure = {
                        },
                        overlayShape = overlayShape
                    )
                    }

                } else {
                    Text("Scanner só está disponível em ambiente mobile")
                }
            }

        }
    }
}

@Composable
fun TextoSucesso(texto: String, duracaoMillis: Long = 2000L, navController: NavHostController) {
    var isVisible by remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        delay(duracaoMillis)
        isVisible = false
        navController.popBackStack()
    }

    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut()
    ) {
        Text(text = texto, style = MaterialTheme.typography.labelMedium,color = Color.Green,
            modifier = Modifier
                .background(
                    color = Color.White, // 🔑
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(5.dp)
        )
    }

    // Se você não quiser animação, poderia usar apenas um `if`:
    /*
    if (isVisible) {
        Text(text = texto)
    }
    */
}
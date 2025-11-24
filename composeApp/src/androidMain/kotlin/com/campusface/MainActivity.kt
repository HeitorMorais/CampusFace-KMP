// composeApp/src/androidMain/java/com/campusface/MainActivity.kt
package com.campusface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat // 🚨 IMPORTE ISSO!
import com.campusface.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        // 🚨 PASSO 1: Configurar a janela para desenhar abaixo das barras do sistema
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

                App() // Seu composable principal
            }
        }
    }

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
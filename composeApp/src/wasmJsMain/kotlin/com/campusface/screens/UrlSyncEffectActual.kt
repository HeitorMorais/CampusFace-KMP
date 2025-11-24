package com.campusface.screens

// wasmJsMain/kotlin/UrlSyncEffectActual.kt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.campusface.navigation.DashboardScreen
import kotlinx.browser.window

@OptIn(ExperimentalWasmJsInterop::class)
@Composable
actual fun UrlSyncEffect(
    currentScreen: DashboardScreen,
    onScreenChangedByUrl: (DashboardScreen) -> Unit
) {
    // 1. Lógica para ATUALIZAR a URL quando o estado interno muda (onNavigate)
    LaunchedEffect(currentScreen) {
        val path = when (currentScreen) {

            // --- DESTINOS EXISTENTES ---
            DashboardScreen.Membro -> "membro"

            // --- NOVOS DESTINOS (Adicionar, Administrar, Validar, Perfil) ---
            DashboardScreen.AdicionarMembro -> "membro/adicionar"
            DashboardScreen.Administrar -> "administrar"
            DashboardScreen.Validar -> "validar"
            DashboardScreen.MeuPerfil -> "meuperfil"


            // 🚨 NOVO DESTINO DE DETALHE DO HUB COM PARÂMETRO
            is DashboardScreen.DetalhesHub -> "administrar/hub/${currentScreen.hubId}"
            else -> {}
        }
        window.history.pushState(null, "", "#$path")
    }

    // 2. Lógica para LER a URL quando a página carrega ou o histórico muda
    LaunchedEffect(Unit) {
        fun parsePath(path: String): DashboardScreen {
            // Remove a barra inicial, se houver, para facilitar a comparação
            val cleanPath = path.trimStart('/')

            return when {

                // --- DESTINOS SIMPLES ---
                cleanPath == "membro" -> DashboardScreen.Membro
                cleanPath == "membro/adicionar" -> DashboardScreen.AdicionarMembro
                cleanPath == "administrar" -> DashboardScreen.Administrar
                cleanPath == "validar" -> DashboardScreen.Validar
                cleanPath == "meuperfil" -> DashboardScreen.MeuPerfil


                // 🚨 Verifica a rota DetalhesHub
                cleanPath.startsWith("administrar/hub/") -> {
                    val hubId = cleanPath.substringAfter("administrar/hub/")
                    DashboardScreen.DetalhesHub(hubId)
                }

                // --- FALLBACK ---
                else -> DashboardScreen.Administrar // Define um destino padrão se nada for encontrado
            }
        }

        // Leitura inicial
        val initialPath = window.location.hash.substringAfter("#")
        onScreenChangedByUrl(parsePath(initialPath))

        // Leitura do botão Back/Forward
        window.onpopstate = {
            val newPath = window.location.hash.substringAfter("#")
            onScreenChangedByUrl(parsePath(newPath))
        }
    }
}
package com.campusface.components

// commonMain/kotlin/components/MainContent.kt

import com.campusface.screens.membroScreen.AdicionarMembroScreen
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campusface.navigation.DashboardScreen
import com.campusface.screens.administrarScreen.AdministrarScreen
import com.campusface.screens.membroScreen.MembroScreen
import com.campusface.screens.MeuPerfilScreen
import com.campusface.screens.ValidarScreen
import com.campusface.screens.administrarScreen.HubDetailsWithTabs


@Composable
fun MainContent(currentScreen: DashboardScreen, onNavigate: (DashboardScreen) -> Unit) {
    // 1. O Crossfade preenche o espaço disponível
    Crossfade(
        targetState = currentScreen,
        label = "Dashboard Screen Transition",
        modifier = Modifier.fillMaxSize()
    ) { screen ->


        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.widthIn(max = 650.dp),
                contentAlignment = Alignment.Center
            ) {
                when (screen) {
                    is DashboardScreen.Membro -> {
                        MembroScreen(onAdicionarClick = {
                            onNavigate(DashboardScreen.AdicionarMembro)
                        })
                    }
                    is DashboardScreen.AdicionarMembro -> {
                        AdicionarMembroScreen(
                            onVoltarParaMembro = {
                                onNavigate(DashboardScreen.Membro)
                            }
                        )
                    }
                    is DashboardScreen.Administrar -> {
                        AdministrarScreen(
                            onCriarClick = {},
                            // 🚨 NOVO CALLBACK: Conecta o Hub clicado (usando o ID) à rota DetalhesHub
                            onHubClick = { hub ->
                                onNavigate(DashboardScreen.DetalhesHub(hub.id.toString())) // Transforma o Int ID em String para a rota

                    })}

                    is DashboardScreen.DetalhesHub -> {
                        HubDetailsWithTabs(
                            hubId = screen.hubId,
                            onVoltar = { onNavigate(DashboardScreen.Administrar) } // Volta para a lista
                        )
                    }


                    is DashboardScreen.Validar -> ValidarScreen()
                    is DashboardScreen.MeuPerfil -> MeuPerfilScreen()
                }
            }
        }
    }
}



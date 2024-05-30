package com.ucne.registro_tecnicos.presentation.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ucne.registro_tecnicos.data.repository.ServicioRepository
import com.ucne.registro_tecnicos.data.repository.TecnicoRepository
import com.ucne.registro_tecnicos.data.repository.TipoTecnicoRepository
import com.ucne.registro_tecnicos.presentation.servicio.ServicioListScreen
import com.ucne.registro_tecnicos.presentation.servicio.ServicioScreen
import com.ucne.registro_tecnicos.presentation.servicio.ServicioViewModel
import com.ucne.registro_tecnicos.presentation.tecnico.TecnicoListScreen
import com.ucne.registro_tecnicos.presentation.tecnico.TecnicoScreen
import com.ucne.registro_tecnicos.presentation.tecnico.TecnicoViewModel
import com.ucne.registro_tecnicos.presentation.tipo_tecnico.TipoTecnicoListScreen
import com.ucne.registro_tecnicos.presentation.tipo_tecnico.TipoTecnicoScreen
import com.ucne.registro_tecnicos.presentation.tipo_tecnico.TipoTecnicoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
@Composable
fun RegistroTecnicosNavHost(
    navController: NavHostController,
    repository: TecnicoRepository,
    tipoRepository: TipoTecnicoRepository,
    scope: CoroutineScope,
    drawerState: DrawerState,
    servicioRepository: ServicioRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.TecnicoList
    ) {
        composable<Screen.TecnicoList> {
            TecnicoListScreen(
                viewModel = viewModel {
                    TecnicoViewModel(repository, tipoRepository,0)
                },
                onVerTecnico = {
                    navController.navigate(Screen.Tecnico(it.tecnicoId ?: 0))
                },
                onAddTecnico = {
                    navController.navigate(Screen.Tecnico(0))
                },
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
        composable<Screen.Tecnico> {
            val args = it.toRoute<Screen.Tecnico>()
            TecnicoScreen(
                viewModel = viewModel {
                    TecnicoViewModel(repository, tipoRepository, args.tecnicoId)
                },
                goBack = { navController.navigate(Screen.TecnicoList) },
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
        composable<Screen.TipoTecnicoList> {
            TipoTecnicoListScreen(
                viewModel = viewModel { TipoTecnicoViewModel(tipoRepository, 0) },
                onVerTipoTecnico = {
                    navController.navigate(Screen.TipoTecnico(it.tipoId ?: 0))
                },
                onAddTipoTecnico = {
                    navController.navigate(Screen.TipoTecnico(0))
                },
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
        composable<Screen.TipoTecnico> {
            val args = it.toRoute<Screen.TipoTecnico>()
            TipoTecnicoScreen(
                viewModel = viewModel {
                    TipoTecnicoViewModel(tipoRepository, args.tipoId)
                },
                goBackTipoTecnicoList = { navController.navigate(Screen.TipoTecnicoList) },
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
        composable<Screen.ServicioList> {
            ServicioListScreen(
                viewModel = viewModel {
                    ServicioViewModel(servicioRepository, repository,0)
                },
                onVerServicio = {
                    navController.navigate(Screen.Servicio(it.servicioId ?: 0))
                },
                onAddServicio = {
                    navController.navigate(Screen.Servicio(0))
                },
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
        composable<Screen.Servicio> {
            val args = it.toRoute<Screen.Servicio>()
            ServicioScreen(
                viewModel = viewModel {
                    ServicioViewModel(servicioRepository, repository, args.servicioId)
                },
                goBackServicioList = { navController.navigate(Screen.ServicioList) },
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    }
}
sealed class Screen {
    @Serializable
    object TecnicoList : Screen()
    @Serializable
    data class Tecnico(val tecnicoId: Int) : Screen()
    @Serializable
    object TipoTecnicoList : Screen()
    @Serializable
    data class TipoTecnico(val tipoId: Int) : Screen()
    @Serializable
    object ServicioList : Screen()
    @Serializable
    data class Servicio(val servicioId: Int) : Screen()
}
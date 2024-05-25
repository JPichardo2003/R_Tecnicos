package com.ucne.registro_tecnicos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import com.ucne.registro_tecnicos.data.local.database.TecnicoDb
import com.ucne.registro_tecnicos.data.repository.ServicioRepository
import com.ucne.registro_tecnicos.data.repository.TecnicoRepository
import com.ucne.registro_tecnicos.data.repository.TipoTecnicoRepository
import com.ucne.registro_tecnicos.presentation.components.NavigationDrawer
import com.ucne.registro_tecnicos.presentation.servicio.ServicioListScreen
import com.ucne.registro_tecnicos.presentation.servicio.ServicioScreen
import com.ucne.registro_tecnicos.presentation.servicio.ServicioViewModel
import com.ucne.registro_tecnicos.presentation.tecnico.TecnicoListScreen
import com.ucne.registro_tecnicos.presentation.tecnico.TecnicoScreen
import com.ucne.registro_tecnicos.presentation.tecnico.TecnicoViewModel
import com.ucne.registro_tecnicos.presentation.tipo_tecnico.TipoTecnicoListScreen
import com.ucne.registro_tecnicos.presentation.tipo_tecnico.TipoTecnicoScreen
import com.ucne.registro_tecnicos.presentation.tipo_tecnico.TipoTecnicoViewModel
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    private lateinit var tecnicoDb: TecnicoDb
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tecnicoDb = Room.databaseBuilder(
            this,
            TecnicoDb::class.java,
            "Tecnico.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        val repository = TecnicoRepository(tecnicoDb.tecnicoDao())
        val tipoRepository = TipoTecnicoRepository(tecnicoDb.tipoTecnicoDao())
        val servicioRepository = ServicioRepository(tecnicoDb.servicioDao())
        enableEdgeToEdge()
        setContent {
            Registro_TecnicosTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                NavigationDrawer(
                    navTecnicoList = { navController.navigate(Screen.TecnicoList)},
                    navTipoTecnicoList = { navController.navigate(Screen.TipoTecnicoList)},
                    navServicioList = { navController.navigate(Screen.ServicioList)},
                    drawerState = drawerState
                ){
                    NavHost(
                        navController = navController,
                        startDestination = Screen.TecnicoList
                    ) {
                        composable<Screen.TecnicoList>{
                            TecnicoListScreen(
                                viewModel = viewModel { TecnicoViewModel(repository, tipoRepository,0)},
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
                                viewModel = viewModel { TecnicoViewModel(repository, tipoRepository, args.tecnicoId) },
                                goBack = {navController.navigate(Screen.TecnicoList)},
                                openDrawer = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            )
                        }
                        composable<Screen.TipoTecnicoList> {
                            TipoTecnicoListScreen(
                                viewModel = viewModel { TipoTecnicoViewModel(tipoRepository,0) },
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
                                viewModel = viewModel { TipoTecnicoViewModel(tipoRepository, args.tipoId) },
                                goBackTipoTecnicoList = {navController.navigate(Screen.TipoTecnicoList)},
                                openDrawer = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            )
                        }
                        composable<Screen.ServicioList> {
                            ServicioListScreen(
                                viewModel = viewModel { ServicioViewModel(servicioRepository, repository ,0) },
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
                                viewModel = viewModel { ServicioViewModel(servicioRepository, repository, args.servicioId) },
                                goBackServicioList = {navController.navigate(Screen.ServicioList)},
                                openDrawer = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            )
                        }
                    }
                }

            }
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
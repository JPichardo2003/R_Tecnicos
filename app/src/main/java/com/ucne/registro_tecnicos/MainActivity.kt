package com.ucne.registro_tecnicos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import com.ucne.registro_tecnicos.data.local.database.TecnicoDb
import com.ucne.registro_tecnicos.data.repository.TecnicoRepository
import com.ucne.registro_tecnicos.presentation.tecnico.TecnicoListScreen
import com.ucne.registro_tecnicos.presentation.tecnico.TecnicoScreen
import com.ucne.registro_tecnicos.presentation.tecnico.TecnicoViewModel
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme
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
        enableEdgeToEdge()
        setContent {
            Registro_TecnicosTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.TecnicoList
                ) {
                    composable<Screen.TecnicoList>{
                        TecnicoListScreen(
                            viewModel = viewModel { TecnicoViewModel(repository, 0)},
                            onVerTecnico = {
                                navController.navigate(Screen.Tecnico(it.tecnicoId ?: 0))
                            },
                            onAddTecnico = {
                                navController.navigate(Screen.Tecnico(0))
                            },
                            navController = navController
                        )
                    }
                    composable<Screen.Tecnico> {
                        val args = it.toRoute<Screen.Tecnico>()
                        TecnicoScreen(
                            viewModel = viewModel { TecnicoViewModel(repository, args.tecnicoId) },
                            navController = navController
                        )
                    }
                }
            }
                /*Surface {
                    val viewModel: TecnicoViewModel = viewModel(
                        factory = TecnicoViewModel.provideFactory(repository)
                    )
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(8.dp)
                        ) {

                            TecnicoScreen(viewModel = viewModel)
                            TecnicoListScreen(viewModel = viewModel,
                                onVerTecnico = {
                                }
                            )
                        }
                    }
                }
            }*/
        }
    }
}

sealed class Screen {
    @Serializable
    object TecnicoList : Screen()
    @Serializable
    data class Tecnico(val tecnicoId: Int) : Screen()
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    Registro_TecnicosTheme {

    }
}
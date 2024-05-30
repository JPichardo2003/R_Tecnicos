package com.ucne.registro_tecnicos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.ucne.registro_tecnicos.data.local.database.TecnicoDb
import com.ucne.registro_tecnicos.data.repository.ServicioRepository
import com.ucne.registro_tecnicos.data.repository.TecnicoRepository
import com.ucne.registro_tecnicos.data.repository.TipoTecnicoRepository
import com.ucne.registro_tecnicos.presentation.components.NavigationDrawer
import com.ucne.registro_tecnicos.presentation.navigation.RegistroTecnicosNavHost
import com.ucne.registro_tecnicos.presentation.navigation.Screen
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme

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
                    RegistroTecnicosNavHost(navController, repository, tipoRepository,
                        scope, drawerState, servicioRepository
                    )
                }
            }
        }
    }
}
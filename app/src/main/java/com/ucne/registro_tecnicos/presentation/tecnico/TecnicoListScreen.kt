package com.ucne.registro_tecnicos.presentation.tecnico

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ucne.registro_tecnicos.data.local.entities.TecnicoEntity
import com.ucne.registro_tecnicos.presentation.components.FloatingButton
import com.ucne.registro_tecnicos.presentation.components.NavigationDrawer
import com.ucne.registro_tecnicos.presentation.components.TopAppBar
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme
import kotlinx.coroutines.launch

@Composable
fun TecnicoListScreen(
    viewModel: TecnicoViewModel,
    onVerTecnico: (TecnicoEntity) -> Unit,
    onAddTecnico: () -> Unit,
    navController: NavHostController
) {
    val tecnicos by viewModel.tecnicos.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    NavigationDrawer(navController = navController, drawerState = drawerState){
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopAppBar(
                title = "Técnicos",
                onMenuClick = { scope.launch { drawerState.open() } }
            )},
            floatingActionButton = { FloatingButton(onAddTecnico) }
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .padding(it)
            ) {
                TecnicoListBody(
                    tecnicos = tecnicos,
                    onVerTecnico = onVerTecnico
                )
            }
        }
    }
}
@Composable
fun TecnicoListBody(
    tecnicos: List<TecnicoEntity>,
    onVerTecnico: (TecnicoEntity) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "ID", modifier = Modifier.weight(0.10f))
        Text(text = "Nombre", modifier = Modifier.weight(0.220f))
        Text(text = "SueldoHora", modifier = Modifier.weight(0.25f))
        Text(text = "Tipo", modifier = Modifier.weight(0.25f))
    }

    Divider()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(tecnicos) { tecnico ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVerTecnico(tecnico) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = tecnico.tecnicoId.toString(), modifier = Modifier.weight(0.12f))
                Text(text = tecnico.nombre.toString(), modifier = Modifier.weight(0.300f))
                Text(text = tecnico.sueldoHora.toString(), modifier = Modifier.weight(0.25f))
                Text(text = tecnico.tipo.toString(), modifier = Modifier.weight(0.30f))
            }
        }
    }
}
@Preview
@Composable
private fun TecnicoListPreview() {
    val tecnicos = listOf(
        TecnicoEntity(
            tecnicoId = 1,
            nombre = "Julio Pichardo",
            sueldoHora = 54.0,
            tipo = "Tecnico"
        )
    )
    Registro_TecnicosTheme {
        TecnicoListBody(tecnicos = tecnicos,
            onVerTecnico = {}
        )
    }
}

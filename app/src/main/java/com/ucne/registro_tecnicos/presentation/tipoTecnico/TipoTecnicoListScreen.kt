package com.ucne.registro_tecnicos.presentation.tipoTecnico

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
import com.ucne.registro_tecnicos.data.local.entities.TipoTecnicoEntity
import com.ucne.registro_tecnicos.presentation.components.FloatingButton
import com.ucne.registro_tecnicos.presentation.components.NavigationDrawer
import com.ucne.registro_tecnicos.presentation.components.TopAppBar
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme
import kotlinx.coroutines.launch

@Composable
fun TipoTecnicoListScreen(
    viewModel: TipoTecnicoViewModel,
    onVerTipoTecnico: (TipoTecnicoEntity) -> Unit,
    onAddTipoTecnico: () -> Unit,
    navController: NavHostController
) {
    val tiposTecnicos by viewModel.tipoTecnicos.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    NavigationDrawer(navController = navController, drawerState = drawerState){
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopAppBar(
                title = "Tipos Técnicos",
                onMenuClick = { scope.launch { drawerState.open() } }
            )},
            floatingActionButton = { FloatingButton(onAddTipoTecnico) }
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .padding(it)
            ) {
                TipoTecnicoListBody(
                    tiposTecnicos = tiposTecnicos,
                    onVerTipoTecnico = onVerTipoTecnico,
                )
            }
        }

    }
}
@Composable
fun TipoTecnicoListBody(
    tiposTecnicos: List<TipoTecnicoEntity>,
    onVerTipoTecnico: (TipoTecnicoEntity) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "ID", modifier = Modifier.weight(0.06f))
        Text(text = "Descripción", modifier = Modifier.weight(0.200f))
    }

    Divider()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(tiposTecnicos) { tipoTecnico ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVerTipoTecnico(tipoTecnico) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = tipoTecnico.tipoId.toString(), modifier = Modifier.weight(0.10f))
                Text(text = tipoTecnico.descripcion.toString(), modifier = Modifier.weight(0.370f))
            }
        }
    }
}
@Preview
@Composable
private fun TipoTecnicoListPreview() {
    val tiposTecnicos = listOf(
        TipoTecnicoEntity(
            tipoId = 1,
            descripcion = "Computadoras"
        )
    )
    Registro_TecnicosTheme {
        TipoTecnicoListBody(tiposTecnicos = tiposTecnicos,
            onVerTipoTecnico = {}
        )
    }
}

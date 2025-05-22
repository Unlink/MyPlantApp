package sk.duracik.myaiapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import sk.duracik.myaiapplication.repository.PlantRepository
import sk.duracik.myaiapplication.ui.components.PlantCard
import sk.duracik.myaiapplication.ui.theme.MyAIApplicationTheme
import sk.duracik.myaiapplication.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onPlantClick: (plantId: Int) -> Unit = {},
    onAddPlantClick: () -> Unit = {}
) {
    // Zbieranie stavu z ViewModelu
    val plants by homeViewModel.plants.collectAsState(emptyList())

    // State pre potvrdzovacie dialógové okno
    var showDeleteDialog by remember { mutableStateOf(false) }
    var plantToDelete by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Moja zbierka rastlín",
                        maxLines = 1
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPlantClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Pridať rastlinu"
                )
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(plants) { plant ->
                PlantCard(
                    plant = plant,
                    onClick = { onPlantClick(plant.id) },
                    onLongClick = {
                        plantToDelete = plant.id
                        showDeleteDialog = true
                    }
                )
            }
        }
    }

    // Potvrdzovacie dialógové okno pre vymazanie rastliny
    if (showDeleteDialog && plantToDelete != null) {
        val plantName = plants.find { it.id == plantToDelete }?.name ?: "rastlinu"

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                plantToDelete = null
            },
            title = { Text("Vymazať rastlinu") },
            text = { Text("Naozaj chcete vymazať rastlinu '$plantName'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        plantToDelete?.let { homeViewModel.deletePlant(it) }
                        showDeleteDialog = false
                        plantToDelete = null
                    }
                ) {
                    Text("Vymazať")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        plantToDelete = null
                    }
                ) {
                    Text("Zrušiť")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MyAIApplicationTheme {
        HomeScreen()
    }
}


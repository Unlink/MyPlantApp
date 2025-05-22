package sk.duracik.myaiapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import sk.duracik.myaiapplication.ui.components.ImagePicker
import sk.duracik.myaiapplication.ui.theme.MyAIApplicationTheme
import sk.duracik.myaiapplication.viewmodel.AddPlantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    addPlantViewModel: AddPlantViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onPlantAdded: () -> Unit = {}
) {
    val isSaving by addPlantViewModel.isSaving.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Pridať novú rastlinu")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Späť"
                        )
                    }
                },
                actions = {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 16.dp)
                        )
                    } else {
                        IconButton(
                            onClick = {
                                addPlantViewModel.savePlant(onSuccess = onPlantAdded)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Uložiť"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Názov rastliny
            OutlinedTextField(
                value = addPlantViewModel.nameState.value,
                onValueChange = { addPlantViewModel.updateName(it) },
                label = { Text("Názov rastliny") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            // Sekcia s fotkami - novým prístupom
            ImagePicker(
                imageUrls = addPlantViewModel.imageUrlsState.value,
                onImageAdded = { imageUrl -> addPlantViewModel.addImage(imageUrl) },
                onImageRemoved = { imageUrl -> addPlantViewModel.removeImage(imageUrl) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            // Popis rastliny
            Text(
                text = "Popis",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            OutlinedTextField(
                value = addPlantViewModel.descriptionState.value,
                onValueChange = { addPlantViewModel.updateDescription(it) },
                label = { Text("Popis rastliny") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )

            // Tlačidlo na uloženie
            Button(
                onClick = { addPlantViewModel.savePlant(onSuccess = onPlantAdded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Text("Uložiť rastlinu")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPlantScreenPreview() {
    MyAIApplicationTheme {
        AddPlantScreen()
    }
}

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

            // Sekcia s fotkami
            Text(
                text = "Fotografie",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // V skutočnej aplikácii by sme tu implementovali upload fotografií.
            // Pre demo účely však poskytneme niektoré predefinované obrázky, ktoré si používateľ môže vybrať.
            val sampleImages = listOf(
                "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921",
                "https://images.unsplash.com/photo-1509423350716-97f9360b4e09",
                "https://images.unsplash.com/photo-1603436326446-58a9002a0a13",
                "https://images.unsplash.com/photo-1614594075929-b4b3bcc43665",
                "https://images.unsplash.com/photo-1637967886160-fd0748161114",
                "https://images.unsplash.com/photo-1459411552884-841db9b3cc2a"
            )

            // Zobrazenie vybraných fotografií
            if (addPlantViewModel.imageUrlsState.value.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(addPlantViewModel.imageUrlsState.value) { imageUrl ->
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    androidx.compose.foundation.Image(
                                        painter = rememberAsyncImagePainter(imageUrl),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                // Tlačidlo na odstránenie obrázka
                                IconButton(
                                    onClick = { addPlantViewModel.removeImage(imageUrl) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Odstrániť obrázok",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Tlačidlo pre výber z ukážkových obrázkov
            Text(
                text = "Vybrať obrázok rastliny",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(sampleImages) { imageUrl ->
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                addPlantViewModel.addImage(imageUrl)
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            androidx.compose.foundation.Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

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

package sk.duracik.myaiapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import sk.duracik.myaiapplication.PlantApplication
import sk.duracik.myaiapplication.R
import sk.duracik.myaiapplication.model.Plant
import sk.duracik.myaiapplication.ui.theme.MyAIApplicationTheme
import sk.duracik.myaiapplication.viewmodel.PlantDetailViewModel
import sk.duracik.myaiapplication.ui.components.ImagePicker
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlantDetailScreen(
    plantId: Int,
    plantDetailViewModel: PlantDetailViewModel = viewModel(
        factory = PlantDetailViewModel.PlantDetailViewModelFactory(
            (LocalContext.current.applicationContext as PlantApplication).repository
        )
    ),
    onNavigateBack: () -> Unit,
    onNavigateToWateringHistory: (Int) -> Unit = {}
) {
    val context = LocalContext.current

    // Pri vstupe na obrazovku načítame dáta o rastline
    LaunchedEffect(plantId) {
        plantDetailViewModel.loadPlant(plantId)
    }

    // Zbieranie stavu z ViewModelu
    val plant by plantDetailViewModel.plantState.collectAsState()

    // Ak je plant null, ešte dáta nemáme, môžeme zobraziť napr. loading indikátor
    plant?.let { currentPlant ->
        // Stav pre zobrazenie dialogu na správu fotiek
        val showImageDialog = remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = currentPlant.name,
                            maxLines = 1
                        )
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
                        IconButton(onClick = { showImageDialog.value = true }) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Spravovať fotky"
                            )
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
            ) {
                // Názov rastliny
                Text(
                    text = currentPlant.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )

                // Horizontálny pager pre obrázky s použitím novej Compose API
                val pagerState = rememberPagerState { currentPlant.imageUrls.size }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = currentPlant.imageUrls[page],
                                error = painterResource(id = R.drawable.ic_plant_placeholder)
                            ),
                            contentDescription = "${currentPlant.name} - fotka ${page + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Jednoduchý indikátor pre obrázky
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) {
                        repeat(currentPlant.imageUrls.size) { index ->
                            val color = if (pagerState.currentPage == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .width(8.dp)
                                    .height(8.dp)
                                    .background(color = color, shape = androidx.compose.foundation.shape.CircleShape)
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        // Dátum pridania
                        Text(
                            text = "Informácie o rastline",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Dátum pridania",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Dátum pridania do zbierky",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = currentPlant.dateAdded.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Počet dní vlastníctva
                        val daysOwned = ChronoUnit.DAYS.between(currentPlant.dateAdded, LocalDate.now())
                        Text(
                            text = "V zbierke: $daysOwned dní",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Sekcia o zalievaní
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Zalievanie",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )

                                val wateringText = when {
                                    currentPlant.lastWatering == null -> "Rastlina ešte nebola zaliata"
                                    currentPlant.daysSinceLastWatering == 0L -> "Zalievaná dnes"
                                    currentPlant.daysSinceLastWatering == 1L -> "Zalievaná včera"
                                    else -> "Naposledy zaliata pred ${currentPlant.daysSinceLastWatering} dňami"
                                }

                                // Riadok s informáciou o zalievaní
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    val wateringColor = when {
                                        currentPlant.lastWatering == null -> MaterialTheme.colorScheme.error
                                        currentPlant.daysSinceLastWatering > 7 -> Color(0xFFFF6D00)
                                        else -> Color(0xFF2E7D32)
                                    }

                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = "Stav zalievania",
                                        tint = wateringColor
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = wateringText,
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    // Ak máme históriu zalievania, zobrazíme dátum posledného zalievania
                                    if (currentPlant.lastWatering != null) {

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Ikona pre zobrazenie histórie zalievania (bez oramovania)
                                        IconButton(
                                            onClick = { onNavigateToWateringHistory(currentPlant.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DateRange,
                                                contentDescription = "História zalievania",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    Button (
                                        onClick = {
                                            plantDetailViewModel.waterPlant()
                                            Toast.makeText(context, "Rastlina bola zaliata", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WaterDrop,
                                            contentDescription = "Zaliať rastlinu"
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Zaliať")
                                    }
                                }


                            }

                        }

                        // Popis rastliny
                        Text(
                            text = "Popis",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )

                        // Používame popis z modelu namiesto statického textu
                        val description = if (currentPlant.description.isNotBlank()) {
                            currentPlant.description
                        } else {
                            "Táto ${currentPlant.name.lowercase()} je skvelým doplnkom každej domácnosti. " +
                            "Poskytuje príjemný vzhľad a zlepšuje kvalitu vzduchu. " +
                            "Túto rastlinu máte vo svojej zbierke už $daysOwned dní."
                        }

                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            // Dialog na správu fotiek
            if (showImageDialog.value) {
                Dialog(onDismissRequest = { showImageDialog.value = false }) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 4.dp
                    ) {
                        ImagePicker(
                            imageUrls = currentPlant.imageUrls,
                            onImageAdded = { imageUrl ->
                                plantDetailViewModel.addPhoto(imageUrl)
                            },
                            onImageRemoved = { imageUrl ->
                                plantDetailViewModel.removePhoto(imageUrl)
                            },
                            modifier = Modifier.padding(16.dp)
                        )
                        // Zavrieť dialóg po akcii
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(onClick = { showImageDialog.value = false }) {
                                Text("Zavrieť")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun PlantDetailScreenPreview() {
    MyAIApplicationTheme {
        PlantDetailScreen(
            plantId = 1,
            onNavigateBack = {}
        )
    }
}

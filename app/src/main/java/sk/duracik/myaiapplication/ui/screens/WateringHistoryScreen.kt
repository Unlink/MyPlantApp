package sk.duracik.myaiapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import sk.duracik.myaiapplication.model.Watering
import sk.duracik.myaiapplication.ui.theme.MyAIApplicationTheme
import sk.duracik.myaiapplication.viewmodel.WateringHistoryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.ui.platform.LocalContext
import sk.duracik.myaiapplication.PlantApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WateringHistoryScreen(
    plantId: Int,
    wateringHistoryViewModel: WateringHistoryViewModel = viewModel(
        factory = WateringHistoryViewModel.WateringHistoryViewModelFactory(
            (LocalContext.current.applicationContext as PlantApplication).repository
        )
    ),
    onNavigateBack: () -> Unit
) {
    // Načítanie histórie zalievania pri vstupe na obrazovku
    LaunchedEffect(plantId) {
        wateringHistoryViewModel.loadWateringHistory(plantId)
    }

    val plant by wateringHistoryViewModel.plant.collectAsState()
    val wateringHistory by wateringHistoryViewModel.wateringHistory.collectAsState()
    val today = LocalDate.now()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = plant?.name?.let { "História zalievania: $it" } ?: "História zalievania"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Späť"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (wateringHistory.isEmpty()) {
            // Zobrazenie informácie ak rastlina nemá žiadne záznamy o zalievaní
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Žiadne záznamy",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Žiadne záznamy o zalievaní",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Táto rastlina ešte nebola zaliata alebo záznamy o zalievaní nie sú dostupné.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            // Zobrazenie zoznamu záznamov o zalievaní
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Počet záznamov: ${wateringHistory.size}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                items(wateringHistory) { watering ->
                    WateringHistoryItem(watering, today)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WateringHistoryItem(watering: Watering, today: LocalDate) {
    val daysSince = ChronoUnit.DAYS.between(watering.date, today)
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val formattedDate = watering.date.format(dateFormatter)

    val relativeTimeText = when(daysSince) {
        0L -> "Dnes"
        1L -> "Včera"
        else -> "Pred $daysSince dňami"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = "Zaliatie",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = relativeTimeText,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

package sk.duracik.myaiapplication.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import sk.duracik.myaiapplication.R
import sk.duracik.myaiapplication.model.Plant
import sk.duracik.myaiapplication.model.Watering
import sk.duracik.myaiapplication.ui.theme.MyAIApplicationTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlantCard(
    plant: Plant,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = plant.primaryImageUrl,
                        error = painterResource(id = R.drawable.ic_plant_placeholder)
                    ),
                    contentDescription = plant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = plant.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Pridanie zobrazenia počtu dní vlastníctva
            val daysOwned = ChronoUnit.DAYS.between(plant.dateAdded, LocalDate.now())
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Počet dní vlastníctva",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "V zbierke $daysOwned dní",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Pridanie zobrazenia informácií o zalievaní
            val daysSinceWatering = plant.daysSinceLastWatering
            val wateringText = when {
                daysSinceWatering == -1L -> "Nikdy nezalievaná"
                daysSinceWatering == 0L -> "Zalievaná dnes"
                daysSinceWatering == 1L -> "Zalievaná včera"
                else -> "Zalievaná pred $daysSinceWatering dňami"
            }

            // Farba ikony zalievania sa mení podľa stavu zalievania
            val wateringColor = when {
                daysSinceWatering == -1L -> MaterialTheme.colorScheme.error  // Nikdy nezalievaná
                daysSinceWatering > 7 -> Color(0xFFFF6D00)  // Viac ako týždeň - oranžová
                else -> Color(0xFF2E7D32)  // Nedávno zalievaná - zelená
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = "Posledné zaliatie",
                    tint = wateringColor,
                    modifier = Modifier.width(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = wateringText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlantCardPreview() {
    MyAIApplicationTheme {
        PlantCard(
            plant = Plant(
                id = 1,
                name = "Aloe Vera",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921",
                    "https://images.unsplash.com/photo-1509423350716-97f9360b4e09"
                ),
                dateAdded = LocalDate.now().minusDays(120),
                wateringRecords = listOf(
                    Watering(
                        id = 1,
                        plantId = 1,
                        date = LocalDateTime.now().minusDays(3)
                    )
                )
            )
        )
    }
}


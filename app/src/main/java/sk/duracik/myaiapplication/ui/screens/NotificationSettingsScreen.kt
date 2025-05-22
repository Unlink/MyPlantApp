package sk.duracik.myaiapplication.ui.screens

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import sk.duracik.myaiapplication.PlantApplication
import sk.duracik.myaiapplication.viewmodel.NotificationSettingsViewModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = viewModel(
        factory = NotificationSettingsViewModel.Factory(
            LocalContext.current.applicationContext as PlantApplication
        )
    )
) {
    val notificationDays by viewModel.notificationDays.collectAsState()
    val notificationHour by viewModel.notificationHour.collectAsState()
    val notificationMinute by viewModel.notificationMinute.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val formattedTime by viewModel.formattedTime.collectAsState()

    val scope = rememberCoroutineScope()
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nastavenia notifikácií") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Späť")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Povolenie/zakázanie notifikácií
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Povoliť upozornenia na zalievanie",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { enabled ->
                        viewModel.setNotificationsEnabled(enabled)
                    }
                )
            }

            // Nastavenie počtu dní medzi notifikáciami
            if (notificationsEnabled) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Dni medzi upozorneniami",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Upozornenia na zalievanie dostanete každých $notificationDays ${getDaysText(notificationDays)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = notificationDays.toFloat(),
                        onValueChange = { days ->
                            viewModel.setNotificationDays(days.toInt())
                        },
                        valueRange = 1f..14f,
                        steps = 12,
                        thumb = { SliderDefaults.Thumb(interactionSource = remember { MutableInteractionSource() }) }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "1 deň", style = MaterialTheme.typography.bodySmall)
                        Text(text = "14 dní", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Nastavenie času notifikácie
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Čas notifikácie",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Notifikácie sa zobrazia o $formattedTime",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Nastaviť čas notifikácie")
                    }
                }
            }
        }
    }

    // Time Picker dialóg
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = notificationHour,
            initialMinute = notificationMinute
        )

        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onConfirm = {
                scope.launch {
                    viewModel.setNotificationTime(
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    showTimePicker = false
                }
            },
            timePickerState = timePickerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    timePickerState: TimePickerState
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Vyberte čas notifikácie") },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Zrušiť")
            }
        }
    )
}

private fun getDaysText(days: Int): String {
    return when {
        days == 1 -> "deň"
        days in 2..4 -> "dni"
        else -> "dní"
    }
}

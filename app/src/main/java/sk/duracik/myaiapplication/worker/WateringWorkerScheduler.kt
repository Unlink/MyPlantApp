package sk.duracik.myaiapplication.worker

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import sk.duracik.myaiapplication.data.preferences.NotificationPreferencesManager
import sk.duracik.myaiapplication.data.repository.PlantRepository
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Trieda zodpovedná za plánovanie worker-a pre kontrolu zalievania rastlín
 */
class WateringWorkerScheduler(
    private val context: Context,
    private val preferencesManager: NotificationPreferencesManager,
    private val plantRepository: PlantRepository
) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Spustí plánovanie workera, ktorý sa bude spúšťať každý deň v nastavenom čase
     */
    fun setupWorker() {
        // Zruší všetky existujúce workery s týmto tagom
        workManager.cancelAllWorkByTag(PlantWateringWorker.WORKER_TAG)

        // Sleduje zmeny v nastaveniach notifikácií a preplánovať worker podľa potreby
        CoroutineScope(Dispatchers.IO).launch {
            // Sleduj zmeny v nastavení času notifikácií a preplánovaj worker
            preferencesManager.notificationsEnabledFlow.collectLatest { enabled ->
                if (enabled) {
                    scheduleDailyWorker()
                } else {
                    workManager.cancelAllWorkByTag(PlantWateringWorker.WORKER_TAG)
                }
            }
        }
    }

    /**
     * Naplánuje worker na každý deň v nastavenom čase
     */
    private suspend fun scheduleDailyWorker() {
        // Získaj nastavený čas pre notifikácie
        val hour = preferencesManager.notificationHourFlow.collectLatest { hour ->
            val minute = preferencesManager.notificationMinuteFlow.collectLatest { minute ->
                scheduleAtTime(hour, minute)
            }
        }
    }

    /**
     * Naplánuje worker na konkrétny čas
     */
    private fun scheduleAtTime(hour: Int, minute: Int) {
        // Vypočítaj čas do najbližšej notifikácie
        val now = LocalDateTime.now()
        var nextRun = now.withHour(hour).withMinute(minute).withSecond(0)

        // Ak už dnešný čas prešiel, nastav na zajtra
        if (nextRun.isBefore(now)) {
            nextRun = nextRun.plusDays(1)
        }

        // Vypočítaj oneskorenie v minútach
        val delayInMinutes = java.time.Duration.between(now, nextRun).toMinutes()

        // Nastav vstupné parametre pre worker
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        // Vytvor vstupné dáta pre worker
        val inputData = Data.Builder()
            .build()

        // Nastav periodické spúštanie každých 24 hodín s počiatočným oneskorením
        val workRequest = PeriodicWorkRequestBuilder<PlantWateringWorker>(
            24, TimeUnit.HOURS
        ).setConstraints(constraints)
            .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
            .setInputData(inputData)
            .addTag(PlantWateringWorker.WORKER_TAG)
            .build()

        // Registruj work request do WorkManager-a
        workManager.enqueueUniquePeriodicWork(
            PlantWateringWorker.WORKER_TAG,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}

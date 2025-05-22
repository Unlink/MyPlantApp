package sk.duracik.myaiapplication.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import sk.duracik.myaiapplication.MainActivity
import sk.duracik.myaiapplication.PlantApplication
import sk.duracik.myaiapplication.R
import sk.duracik.myaiapplication.data.preferences.NotificationPreferencesManager
import sk.duracik.myaiapplication.data.repository.PlantRepository

/**
 * Worker pre kontrolu potreby zalievania rastlín a zobrazenie notifikácie
 */
class PlantWateringWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    // Get dependencies from the application
    private val repository: PlantRepository = (context.applicationContext as PlantApplication).repository
    private val preferencesManager: NotificationPreferencesManager = (context.applicationContext as PlantApplication).preferencesManager

    companion object {
        const val WORKER_TAG = "PLANT_WATERING_WORKER"
        const val NOTIFICATION_CHANNEL_ID = "WATERING_NOTIFICATION_CHANNEL"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Skontroluj, či sú notifikácie povolené
            val notificationsEnabled = preferencesManager.notificationsEnabledFlow.first()
            if (!notificationsEnabled) {
                return@withContext Result.success()
            }

            // Získaj počet dní pre notifikácie
            val notificationDays = preferencesManager.notificationDaysFlow.first()

            // Získaj všetky rastliny
            val plants = repository.allPlants.first()

            // Skontroluj, či máme aspoň jednu rastlinu
            if (plants.isEmpty()) {
                return@withContext Result.success()
            }

            // Skontroluj, či existuje rastlina, ktorá nebola polievaná dlhšie ako nastavený počet dní
            val plantsNeedingWatering = plants.filter { plant ->
                plant.daysSinceLastWatering >= notificationDays || plant.daysSinceLastWatering < 0 // < 0 znamená, že rastlina nebola ešte nikdy polievaná
            }

            if (plantsNeedingWatering.isNotEmpty()) {
                // Vytvor a zobraz notifikáciu
                showWateringNotification(plantsNeedingWatering.size)
            }

            Result.success()
        } catch (e: Exception) {
            // V prípade chyby skúsime operáciu neskôr znova
            Result.retry()
        }
    }

    /**
     * Zobrazenie notifikácie o potrebe zalievania rastlín
     */
    private fun showWateringNotification(plantsCount: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Vytvor notifikačný kanál (požadované pre Android 8.0+)
        createNotificationChannel(notificationManager)

        // Vytvor intent, ktorý otvorí aplikáciu po kliknutí na notifikáciu
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Text notifikácie
        val contentTitle = if (plantsCount == 1) {
            "Jedna rastlina potrebuje poliať"
        } else {
            "$plantsCount rastlín potrebuje poliať"
        }

        // Vytvor notifikáciu
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Musíš mať túto ikonu vo svojich zdrojoch
            .setContentTitle(contentTitle)
            .setContentText("Nezabudni na svoje rastliny!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Zobraz notifikáciu
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Vytvorenie notifikačného kanála pre Android 8.0+
     */
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Zalievanie rastlín"
            val description = "Notifikácie o potrebe zalievania rastlín"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                this.description = description
            }

            notificationManager.createNotificationChannel(channel)
        }
    }
}

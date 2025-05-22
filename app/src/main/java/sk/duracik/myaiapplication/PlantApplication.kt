package sk.duracik.myaiapplication

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import sk.duracik.myaiapplication.data.DataInitializer
import sk.duracik.myaiapplication.data.local.PlantDatabase
import sk.duracik.myaiapplication.data.preferences.NotificationPreferencesManager
import sk.duracik.myaiapplication.data.repository.PlantRepository
import sk.duracik.myaiapplication.worker.WateringWorkerScheduler

class PlantApplication : Application() {
    // Scope pre aplikáciu - bude existovať počas celého života aplikácie
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Lazy inicializácia databázy a repozitára - vytvorí sa až pri prvom použití
    val database by lazy { PlantDatabase.getDatabase(this) }
    val repository by lazy {
        PlantRepository(
            plantDao = database.plantDao(),
            plantImageDao = database.plantImageDao(),
            wateringDao = database.wateringDao()
        )
    }

    // Notification preferences manager
    val preferencesManager by lazy { NotificationPreferencesManager(this) }

    // Scheduler for watering notifications worker
    val wateringWorkerScheduler by lazy {
        WateringWorkerScheduler(
            this,
            preferencesManager,
            repository
        )
    }

    // Data inicializátor pre prepopuláciu databázy pri prvom spustení
    private val dataInitializer by lazy { DataInitializer(repository, applicationScope) }

    override fun onCreate() {
        super.onCreate()

        // Inicializácia databázy ukážkovými dátami pri štarte aplikácie
        dataInitializer.populateDatabase()

        // Setup the watering notification worker
        wateringWorkerScheduler.setupWorker()
    }
}

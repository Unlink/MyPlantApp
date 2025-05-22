package sk.duracik.myaiapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import sk.duracik.myaiapplication.data.preferences.NotificationPreferencesManager
import sk.duracik.myaiapplication.PlantApplication

/**
 * ViewModel pre obrazovku nastavení notifikácií
 */
class NotificationSettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = NotificationPreferencesManager(application)
    private val wateringWorkerScheduler = (application as PlantApplication).wateringWorkerScheduler

    // Stav počtu dní medzi notifikáciami
    private val _notificationDays = MutableStateFlow(3)
    val notificationDays: StateFlow<Int> = _notificationDays

    // Stav hodiny pre notifikácie
    private val _notificationHour = MutableStateFlow(9)
    val notificationHour: StateFlow<Int> = _notificationHour

    // Stav minúty pre notifikácie
    private val _notificationMinute = MutableStateFlow(0)
    val notificationMinute: StateFlow<Int> = _notificationMinute

    // Stav, či sú notifikácie povolené
    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    // Formátovaný čas pre UI
    private val _formattedTime = MutableStateFlow("09:00")
    val formattedTime: StateFlow<String> = _formattedTime

    init {
        loadPreferences()
        // Aktualizácia formátovaného času pri zmene hodiny alebo minúty
        viewModelScope.launch {
            combine(
                _notificationHour,
                _notificationMinute
            ) { hour, minute ->
                formatTime(hour, minute)
            }.collect { formattedTime ->
                _formattedTime.value = formattedTime
            }
        }
    }

    /**
     * Načítanie uložených preferencií z DataStore
     */
    private fun loadPreferences() {
        viewModelScope.launch {
            preferencesManager.notificationDaysFlow.collect { days ->
                _notificationDays.value = days
            }
        }
        viewModelScope.launch {
            preferencesManager.notificationHourFlow.collect { hour ->
                _notificationHour.value = hour
            }
        }
        viewModelScope.launch {
            preferencesManager.notificationMinuteFlow.collect { minute ->
                _notificationMinute.value = minute
            }
        }
        viewModelScope.launch {
            preferencesManager.notificationsEnabledFlow.collect { enabled ->
                _notificationsEnabled.value = enabled
            }
        }
    }

    /**
     * Nastavenie počtu dní medzi notifikáciami
     */
    fun setNotificationDays(days: Int) {
        viewModelScope.launch {
            _notificationDays.value = days
            preferencesManager.setNotificationDays(days)
            // Pri zmene nastavení aktualizujeme worker
            wateringWorkerScheduler.setupWorker()
        }
    }

    /**
     * Nastavenie času pre notifikácie (hodina, minúta)
     */
    fun setNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            _notificationHour.value = hour
            _notificationMinute.value = minute
            preferencesManager.setNotificationTime(hour, minute)
            // Pri zmene času aktualizujeme worker
            wateringWorkerScheduler.setupWorker()
        }
    }

    /**
     * Povolenie alebo zakázanie notifikácií
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _notificationsEnabled.value = enabled
            preferencesManager.setNotificationsEnabled(enabled)
            // Pri zapnutí/vypnutí notifikácií aktualizujeme worker
            wateringWorkerScheduler.setupWorker()
        }
    }

    /**
     * Formátovanie času pre zobrazenie v UI (napr. "09:05")
     */
    private fun formatTime(hour: Int, minute: Int): String {
        return String.format("%02d:%02d", hour, minute)
    }

    /**
     * Factory pre vytvorenie ViewModel s parametrami
     */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotificationSettingsViewModel::class.java)) {
                return NotificationSettingsViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

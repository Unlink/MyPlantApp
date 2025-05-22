package sk.duracik.myaiapplication.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Rozšírenie kontextu pre prístup k DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_preferences")

/**
 * Trieda pre správu nastavení notifikácií uložených v DataStore
 */
class NotificationPreferencesManager(private val context: Context) {

    companion object {
        // Kľúče pre DataStore
        private val NOTIFICATION_DAYS = intPreferencesKey("notification_days")
        private val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        private val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    // Predvolené hodnoty
    private val DEFAULT_NOTIFICATION_DAYS = 3
    private val DEFAULT_NOTIFICATION_HOUR = 9
    private val DEFAULT_NOTIFICATION_MINUTE = 0
    private val DEFAULT_NOTIFICATIONS_ENABLED = true

    /**
     * Získanie počtu dní medzi notifikáciami
     */
    val notificationDaysFlow: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[NOTIFICATION_DAYS] ?: DEFAULT_NOTIFICATION_DAYS
        }

    /**
     * Získanie hodiny pre notifikácie
     */
    val notificationHourFlow: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[NOTIFICATION_HOUR] ?: DEFAULT_NOTIFICATION_HOUR
        }

    /**
     * Získanie minúty pre notifikácie
     */
    val notificationMinuteFlow: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[NOTIFICATION_MINUTE] ?: DEFAULT_NOTIFICATION_MINUTE
        }

    /**
     * Získanie stavu, či sú notifikácie povolené
     */
    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: DEFAULT_NOTIFICATIONS_ENABLED
        }

    /**
     * Nastavenie počtu dní medzi notifikáciami
     */
    suspend fun setNotificationDays(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_DAYS] = days
        }
    }

    /**
     * Nastavenie času pre notifikácie (hodina, minúta)
     */
    suspend fun setNotificationTime(hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_HOUR] = hour
            preferences[NOTIFICATION_MINUTE] = minute
        }
    }

    /**
     * Povolenie alebo zakázanie notifikácií
     */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
}

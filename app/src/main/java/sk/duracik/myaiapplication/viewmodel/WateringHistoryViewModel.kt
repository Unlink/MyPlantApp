package sk.duracik.myaiapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sk.duracik.myaiapplication.data.repository.PlantRepository
import sk.duracik.myaiapplication.model.Plant
import sk.duracik.myaiapplication.model.Watering

class WateringHistoryViewModel(private val repository: PlantRepository) : ViewModel() {

    // Stav pre aktuálnu rastlinu
    private val _plant = MutableStateFlow<Plant?>(null)
    val plant: StateFlow<Plant?> = _plant.asStateFlow()

    // Stav pre históriu zalievania
    private val _wateringHistory = MutableStateFlow<List<Watering>>(emptyList())
    val wateringHistory: StateFlow<List<Watering>> = _wateringHistory.asStateFlow()

    // Funkcia na načítanie histórie zalievania pre konkrétnu rastlinu
    fun loadWateringHistory(plantId: Int) {
        viewModelScope.launch {
            // Najprv načítame samotnú rastlinu pre zobrazenie názvu
            val plantData = repository.getPlant(plantId)
            _plant.value = plantData

            // Potom načítame záznamy o zalievaní
            plantData?.let { plant ->
                // Zoradenie od najnovších po najstaršie záznamy
                _wateringHistory.value = plant.wateringRecords.sortedByDescending { it.date }
            }
        }
    }

    // Factory trieda pre vytvorenie ViewModelu s repozitárom
    class WateringHistoryViewModelFactory(private val repository: PlantRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WateringHistoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WateringHistoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

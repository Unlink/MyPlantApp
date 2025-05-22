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

class PlantDetailViewModel(private val repository: PlantRepository) : ViewModel() {

    // Stav pre aktuálne zobrazenú rastlinu
    private val _plantState = MutableStateFlow<Plant?>(null)
    val plantState: StateFlow<Plant?> = _plantState.asStateFlow()

    // Funkcia na načítanie detailu rastliny podľa ID
    fun loadPlant(plantId: Int) {
        viewModelScope.launch {
            _plantState.value = repository.getPlant(plantId)
        }
    }

    // Pridanie fotky k rastline
    fun addPhoto(imageUrl: String) {
        val plant = _plantState.value
        if (plant != null) {
            viewModelScope.launch {
                repository.addPlantImage(plant.id, imageUrl)
                // Po pridaní obnovíme detail rastliny
                loadPlant(plant.id)
            }
        }
    }

    // Odstránenie fotky podľa URL
    fun removePhoto(imageUrl: String) {
        val plant = _plantState.value
        if (plant != null) {
            viewModelScope.launch {
                repository.removePlantImage(plant.id, imageUrl)
                // Po odstránení obnovíme detail rastliny
                loadPlant(plant.id)
            }
        }
    }

    // Pridanie záznamu o zalievaní rastliny
    fun waterPlant() {
        val plant = _plantState.value
        if (plant != null) {
            viewModelScope.launch {
                repository.addWatering(plant.id)
                // Po zaliatí obnovíme detail rastliny
                loadPlant(plant.id)
            }
        }
    }

    // Factory trieda pre vytvorenie ViewModelu s repozitárom
    class PlantDetailViewModelFactory(private val repository: PlantRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlantDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlantDetailViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

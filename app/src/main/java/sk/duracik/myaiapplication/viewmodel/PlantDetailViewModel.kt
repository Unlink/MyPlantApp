package sk.duracik.myaiapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sk.duracik.myaiapplication.model.Plant
import sk.duracik.myaiapplication.repository.PlantRepository

class PlantDetailViewModel : ViewModel() {

    // Stav pre aktuálne zobrazenú rastlinu
    private val _plantState = MutableStateFlow<Plant?>(null)
    val plantState: StateFlow<Plant?> = _plantState.asStateFlow()

    // Funkcia na načítanie detailu rastliny podľa ID
    fun loadPlant(plantId: Int) {
        viewModelScope.launch {
            // V reálnej aplikácii by sme tu volali API alebo databázu
            _plantState.value = PlantRepository.plants.find { it.id == plantId }
        }
    }
}

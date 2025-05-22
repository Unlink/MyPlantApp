package sk.duracik.myaiapplication.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import sk.duracik.myaiapplication.model.Plant
import sk.duracik.myaiapplication.repository.PlantRepository

class HomeViewModel : ViewModel() {

    // Stav obsahujúci zoznam rastlín
    private val _plants = MutableStateFlow(PlantRepository.plants)
    val plants: StateFlow<List<Plant>> = _plants.asStateFlow()

    // Funkcia na filtrovanie rastlín (môže byť rozšírená v budúcnosti)
    fun filterPlants(query: String = "") {
        if (query.isEmpty()) {
            _plants.value = PlantRepository.plants
        } else {
            _plants.value = PlantRepository.plants.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
    }
}

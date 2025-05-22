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

class HomeViewModel(private val repository: PlantRepository) : ViewModel() {

    // Stav obsahujúci zoznam rastlín - teraz načítaný z databázy
    val plants = repository.allPlants

    // Funkcia na filtrovanie rastlín (môže byť implementovaná neskôr)

    // Factory trieda pre vytvorenie ViewModelu s repozitárom
    class HomeViewModelFactory(private val repository: PlantRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

package sk.duracik.myaiapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sk.duracik.myaiapplication.data.repository.PlantRepository
import sk.duracik.myaiapplication.model.Plant
import sk.duracik.myaiapplication.util.ImageHelper

class HomeViewModel(
    private val repository: PlantRepository,
    application: Application
) : AndroidViewModel(application) {

    // Pomocná trieda pre prácu s obrázkami
    private val imageHelper = ImageHelper(application.applicationContext)

    // Stav obsahujúci zoznam rastlín - teraz načítaný z databázy
    val plants = repository.allPlants

    /**
     * Vymaže rastlinu podľa ID vrátane jej fyzických súborov obrázkov
     */
    fun deletePlant(plantId: Int) {
        viewModelScope.launch {
            // Najprv získame objekt rastliny
            val plant = repository.getPlant(plantId)
            plant?.let {
                // Vymažeme fyzické súbory obrázkov
                deleteImageFiles(it.imageUrls)
                // Potom vymažeme rastlinu z databázy
                repository.deletePlant(it)
            }
        }
    }

    /**
     * Vymaže fyzické súbory obrázkov
     */
    private fun deleteImageFiles(imageUrls: List<String>) {
        for (imageUrl in imageUrls) {
            // Voláme nami implementovanú metódu z ImageHelper
            imageHelper.deleteImageFile(imageUrl)
        }
    }

    // Funkcia na filtrovanie rastlín (môže byť implementovaná neskôr)

    // Factory trieda pre vytvorenie ViewModelu s repozitárom a kontextom aplikácie
    class HomeViewModelFactory(
        private val repository: PlantRepository,
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

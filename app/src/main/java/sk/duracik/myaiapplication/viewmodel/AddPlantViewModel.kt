package sk.duracik.myaiapplication.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sk.duracik.myaiapplication.data.repository.PlantRepository
import sk.duracik.myaiapplication.model.Plant
import java.time.LocalDate

class AddPlantViewModel(private val repository: PlantRepository) : ViewModel() {

    // Stav pre novú rastlinu
    private val _nameState = mutableStateOf("")
    val nameState = _nameState

    private val _descriptionState = mutableStateOf("")
    val descriptionState = _descriptionState

    private val _imageUrlsState = mutableStateOf(listOf<String>())
    val imageUrlsState = _imageUrlsState

    // Stav pre ukladanie
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    // Pridanie novej fotky do zoznamu
    fun addImage(imageUrl: String) {
        _imageUrlsState.value = _imageUrlsState.value + imageUrl
    }

    // Odstránenie fotky zo zoznamu
    fun removeImage(imageUrl: String) {
        _imageUrlsState.value = _imageUrlsState.value.filter { it != imageUrl }
    }

    // Aktualizácia názvu
    fun updateName(name: String) {
        _nameState.value = name
    }

    // Aktualizácia popisu
    fun updateDescription(description: String) {
        _descriptionState.value = description
    }

    // Funkcia na uloženie novej rastliny
    fun savePlant(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Kontrola, či máme aspoň jeden obrázok a názov
            if (_nameState.value.isBlank() || _imageUrlsState.value.isEmpty()) {
                return@launch
            }

            _isSaving.value = true

            try {
                // Vytvorenie novej rastliny a uloženie do databázy cez repozitár
                val newPlant = Plant(
                    id = 0, // ID bude automaticky vygenerované databázou
                    name = _nameState.value,
                    imageUrls = _imageUrlsState.value,
                    dateAdded = LocalDate.now(),
                    description = _descriptionState.value
                )

                // Uloženie do databázy
                repository.insertPlant(newPlant)

                // Resetujeme stav
                _nameState.value = ""
                _descriptionState.value = ""
                _imageUrlsState.value = listOf()

                onSuccess()
            } finally {
                _isSaving.value = false
            }
        }
    }

    // Resetovanie stavu formulára
    fun resetForm() {
        _nameState.value = ""
        _descriptionState.value = ""
        _imageUrlsState.value = listOf()
    }

    // Factory trieda pre vytvorenie ViewModelu s repozitárom
    class AddPlantViewModelFactory(private val repository: PlantRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddPlantViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddPlantViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

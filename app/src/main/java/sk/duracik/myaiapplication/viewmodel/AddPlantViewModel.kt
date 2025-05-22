package sk.duracik.myaiapplication.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sk.duracik.myaiapplication.model.Plant
import sk.duracik.myaiapplication.repository.PlantRepository
import java.time.LocalDate

class AddPlantViewModel : ViewModel() {

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
                // V skutočnej aplikácii by sme tu volali API alebo databázu
                // Pre demo účely vytvoríme novú rastlinu s nasledujúcim ID
                val maxId = PlantRepository.plants.maxOfOrNull { it.id } ?: 0
                val newPlant = Plant(
                    id = maxId + 1,
                    name = _nameState.value,
                    imageUrls = _imageUrlsState.value,
                    dateAdded = LocalDate.now()
                )

                // Tu by sme normálne uložili rastlinu do databázy
                // Simulácia krátkeho oneskorenia
                kotlinx.coroutines.delay(500)

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
}

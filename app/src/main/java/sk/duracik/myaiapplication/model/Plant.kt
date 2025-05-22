package sk.duracik.myaiapplication.model

import java.time.LocalDate

data class Plant(
    val id: Int,
    val name: String,
    val imageUrls: List<String>,
    val dateAdded: LocalDate,
    val description: String = "" // Pridané pole pre popis rastliny
) {
    // Pomocná vlastnosť pre prvý obrázok - bude sa zobrazovať na HomeScreen
    val primaryImageUrl: String
        get() = imageUrls.firstOrNull() ?: ""
}

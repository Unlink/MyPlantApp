package sk.duracik.myaiapplication.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class Plant(
    val id: Int,
    val name: String,
    val imageUrls: List<String>,
    val dateAdded: LocalDate,
    val description: String = "", // Pridané pole pre popis rastliny
    val wateringRecords: List<Watering> = emptyList() // Pridané záznamy o zalievaní
) {
    // Pomocná vlastnosť pre prvý obrázok - bude sa zobrazovať na HomeScreen
    val primaryImageUrl: String
        get() = imageUrls.firstOrNull() ?: ""

    // Dátum posledného zalievania
    val lastWatering: LocalDateTime?
        get() = wateringRecords.maxByOrNull { it.date }?.date

    // Počet dní od posledného zalievania
    val daysSinceLastWatering: Long
        get() = lastWatering?.let { ChronoUnit.DAYS.between(it, LocalDateTime.now()) } ?: -1
}

package sk.duracik.myaiapplication.model

import java.time.LocalDate

data class Plant(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val dateAdded: LocalDate
)

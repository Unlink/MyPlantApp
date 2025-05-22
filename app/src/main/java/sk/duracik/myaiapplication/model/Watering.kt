package sk.duracik.myaiapplication.model

import java.time.LocalDate

data class Watering(
    val id: Int = 0,
    val plantId: Int,
    val date: LocalDate = LocalDate.now()
)

package sk.duracik.myaiapplication.model

import java.time.LocalDateTime

data class Watering(
    val id: Int = 0,
    val plantId: Int,
    val date: LocalDateTime = LocalDateTime.now()
)

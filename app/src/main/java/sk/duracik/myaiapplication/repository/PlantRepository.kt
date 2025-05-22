package sk.duracik.myaiapplication.repository

import sk.duracik.myaiapplication.model.Plant
import java.time.LocalDate

object PlantRepository {
    val plants = listOf(
        Plant(
            id = 1,
            name = "Aloe Vera",
            imageUrl = "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921",
            dateAdded = LocalDate.now().minusDays(120)
        ),
        Plant(
            id = 2,
            name = "Monstera",
            imageUrl = "https://images.unsplash.com/photo-1614594975525-e45190c55d0b",
            dateAdded = LocalDate.now().minusDays(45)
        ),
        Plant(
            id = 3,
            name = "Kaktus",
            imageUrl = "https://images.unsplash.com/photo-1459411552884-841db9b3cc2a",
            dateAdded = LocalDate.now().minusDays(280)
        ),
        Plant(
            id = 4,
            name = "Fikus",
            imageUrl = "https://images.unsplash.com/photo-1602923668104-8f9e03e77e62",
            dateAdded = LocalDate.now().minusDays(10)
        ),
        Plant(
            id = 5,
            name = "Orchidea",
            imageUrl = "https://images.unsplash.com/photo-1566907225712-46a63eda4481",
            dateAdded = LocalDate.now().minusDays(75)
        ),
        Plant(
            id = 6,
            name = "Sukulent",
            imageUrl = "https://images.unsplash.com/photo-1446071103084-c257b5f70672",
            dateAdded = LocalDate.now().minusDays(190)
        ),
    )
}

package sk.duracik.myaiapplication.repository

import sk.duracik.myaiapplication.model.Plant
import java.time.LocalDate

object PlantRepository {
    val plants = listOf(
        Plant(
            id = 1,
            name = "Aloe Vera",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921",
                "https://images.unsplash.com/photo-1509423350716-97f9360b4e09",
                "https://images.unsplash.com/photo-1603436326446-58a9002a0a13"
            ),
            dateAdded = LocalDate.now().minusDays(120)
        ),
        Plant(
            id = 2,
            name = "Monstera",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1614594075929-b4b3bcc43665",
                "https://images.unsplash.com/photo-1637967886160-fd0748161114",
                "https://images.unsplash.com/photo-1636901942218-0ddd36c86eb6"
            ),
            dateAdded = LocalDate.now().minusDays(45)
        ),
        Plant(
            id = 3,
            name = "Kaktus",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1459411552884-841db9b3cc2a",
                "https://images.unsplash.com/photo-1551888419-7b7a520fe0ca",
                "https://images.unsplash.com/photo-1485955900006-10f4d324d411"
            ),
            dateAdded = LocalDate.now().minusDays(280)
        ),
        Plant(
            id = 4,
            name = "Fikus",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1602923668104-8f9e03e77e62",
                "https://images.unsplash.com/photo-1599598177991-ec67b5c37318",
                "https://images.unsplash.com/photo-1627472839507-f775954a5555"
            ),
            dateAdded = LocalDate.now().minusDays(10)
        ),
        Plant(
            id = 5,
            name = "Orchidea",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1566907225712-46a63eda4481",
                "https://images.unsplash.com/photo-1524598171353-ce84a157ed05",
                "https://images.unsplash.com/photo-1610776763760-7d17c017642c"
            ),
            dateAdded = LocalDate.now().minusDays(75)
        ),
        Plant(
            id = 6,
            name = "Sukulent",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1446071103084-c257b5f70672",
                "https://images.unsplash.com/photo-1520302630591-fd1c66edc19d",
                "https://images.unsplash.com/photo-1509937528035-ad76254b0356"
            ),
            dateAdded = LocalDate.now().minusDays(190)
        ),
    )
}

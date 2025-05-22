package sk.duracik.myaiapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import sk.duracik.myaiapplication.model.Plant
import java.time.LocalDate

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val imageUrls: String, // Uložené ako JSON string
    val dateAdded: String, // Uložené ako reťazec vo formáte ISO
    val description: String = "" // Pridané pre uloženie popisu
) {
    companion object {
        fun fromPlant(plant: Plant): PlantEntity {
            return PlantEntity(
                id = plant.id,
                name = plant.name,
                imageUrls = plant.imageUrls.joinToString(","),
                dateAdded = plant.dateAdded.toString(),
                description = plant.description
            )
        }

        fun toPlant(entity: PlantEntity): Plant {
            return Plant(
                id = entity.id,
                name = entity.name,
                imageUrls = entity.imageUrls.split(","),
                dateAdded = LocalDate.parse(entity.dateAdded),
                description = entity.description
            )
        }
    }
}

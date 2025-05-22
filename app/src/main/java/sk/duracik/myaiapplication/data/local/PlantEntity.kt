package sk.duracik.myaiapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import sk.duracik.myaiapplication.model.Plant
import sk.duracik.myaiapplication.model.Watering
import java.time.LocalDate

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val dateAdded: String, // Uložené ako reťazec vo formáte ISO
    val description: String = "" // Popis rastliny
) {
    companion object {
        fun fromPlant(plant: Plant): PlantEntity {
            return PlantEntity(
                id = plant.id,
                name = plant.name,
                dateAdded = plant.dateAdded.toString(),
                description = plant.description
            )
        }

        fun toPlant(entity: PlantEntity, imageUrls: List<String> = emptyList(), wateringRecords: List<Watering> = emptyList()): Plant {
            return Plant(
                id = entity.id,
                name = entity.name,
                imageUrls = imageUrls,
                dateAdded = LocalDate.parse(entity.dateAdded),
                description = entity.description,
                wateringRecords = wateringRecords
            )
        }
    }
}


package sk.duracik.myaiapplication.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import sk.duracik.myaiapplication.model.Watering
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "waterings",
    foreignKeys = [
        ForeignKey(
            entity = PlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plantId")]
)
data class WateringEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val plantId: Int,
    val date: String // Uložené ako reťazec vo formáte ISO
) {
    companion object {
        fun fromWatering(watering: Watering): WateringEntity {
            return WateringEntity(
                id = watering.id,
                plantId = watering.plantId,
                date = watering.date.toString()
            )
        }

        fun toWatering(entity: WateringEntity): Watering {
            return try {
                // Skúsi najprv parsovať ako LocalDateTime
                Watering(
                    id = entity.id,
                    plantId = entity.plantId,
                    date = LocalDateTime.parse(entity.date)
                )
            } catch (e: Exception) {
                // Ak sa to nepodarí, predpokladá LocalDate a konvertuje ho na LocalDateTime v čase 00:00
                Watering(
                    id = entity.id,
                    plantId = entity.plantId,
                    date = LocalDate.parse(entity.date).atStartOfDay()
                )
            }
        }
    }
}

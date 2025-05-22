package sk.duracik.myaiapplication.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entita pre ukladanie obrázkov rastlín
 * Každý záznam obsahuje URL jedného obrázka a referenciu na rastlinu, ku ktorej patrí
 */
@Entity(
    tableName = "plant_images",
    foreignKeys = [
        ForeignKey(
            entity = PlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE // Ak vymažeme rastlinu, vymažú sa aj jej obrázky
        )
    ],
    indices = [Index("plantId")] // Index pre rýchlejšie vyhľadávanie
)
data class PlantImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val plantId: Int, // Referencia na rastlinu
    val imageUrl: String, // URL obrázka
    val sortOrder: Int = 0 // Poradie obrázka v galérii
)

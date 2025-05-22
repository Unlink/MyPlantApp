package sk.duracik.myaiapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantImageDao {
    @Query("SELECT * FROM plant_images WHERE plantId = :plantId ORDER BY sortOrder ASC")
    fun getImagesForPlant(plantId: Int): Flow<List<PlantImageEntity>>

    @Query("SELECT * FROM plant_images WHERE plantId = :plantId ORDER BY sortOrder ASC")
    suspend fun getImagesForPlantSync(plantId: Int): List<PlantImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: PlantImageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<PlantImageEntity>)

    @Update
    suspend fun updateImage(image: PlantImageEntity)

    @Delete
    suspend fun deleteImage(image: PlantImageEntity)

    @Query("DELETE FROM plant_images WHERE plantId = :plantId")
    suspend fun deleteImagesForPlant(plantId: Int)
}

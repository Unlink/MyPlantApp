package sk.duracik.myaiapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WateringDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatering(wateringEntity: WateringEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterings(wateringEntities: List<WateringEntity>)

    @Delete
    suspend fun deleteWatering(wateringEntity: WateringEntity)

    @Query("DELETE FROM waterings WHERE plantId = :plantId")
    suspend fun deleteWateringsForPlant(plantId: Int)

    @Query("SELECT * FROM waterings WHERE plantId = :plantId ORDER BY date DESC")
    suspend fun getWateringsForPlant(plantId: Int): List<WateringEntity>
}

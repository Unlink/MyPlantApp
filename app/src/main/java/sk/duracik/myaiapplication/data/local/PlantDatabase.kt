package sk.duracik.myaiapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [PlantEntity::class, PlantImageEntity::class],
    version = 2, // Zvýšenie verzie databázy, keďže meníme schému
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class PlantDatabase : RoomDatabase() {

    abstract fun plantDao(): PlantDao
    abstract fun plantImageDao(): PlantImageDao

    companion object {
        @Volatile
        private var INSTANCE: PlantDatabase? = null

        fun getDatabase(context: Context): PlantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantDatabase::class.java,
                    "plant_database"
                )
                    .fallbackToDestructiveMigration() // Pri zmene schémy sa zmaže stará databáza
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


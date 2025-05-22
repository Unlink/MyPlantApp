package sk.duracik.myaiapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [PlantEntity::class, PlantImageEntity::class, WateringEntity::class],
    version = 3, // Zvýšenie verzie databázy, keďže pridávame novú entitu
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class PlantDatabase : RoomDatabase() {

    abstract fun plantDao(): PlantDao
    abstract fun plantImageDao(): PlantImageDao
    abstract fun wateringDao(): WateringDao

    companion object {
        // Migrácia z verzie 2 na verziu 3 (pridávame tabuľku waterings)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Vytvorenie novej tabuľky waterings
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `waterings` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`plantId` INTEGER NOT NULL, " +
                            "`date` TEXT NOT NULL, " +
                            "FOREIGN KEY(`plantId`) REFERENCES `plants`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                // Vytvorenie indexu pre rýchle vyhľadávanie podľa plantId
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_waterings_plantId` ON `waterings` (`plantId`)"
                )
            }
        }

        @Volatile
        private var INSTANCE: PlantDatabase? = null

        fun getDatabase(context: Context): PlantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantDatabase::class.java,
                    "plant_database"
                )
                    // Použitie vlastnej migrácie namiesto fallbackToDestructiveMigration
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


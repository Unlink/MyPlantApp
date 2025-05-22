package sk.duracik.myaiapplication.data.local

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * TypeConverter pre konverziu medzi LocalDate a String v Room databáze
 */
class DateConverters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(dateString: String?): LocalDateTime? {
        return dateString?.let { LocalDateTime.parse(it) }
    }
}

package ph.edu.comteq.pesobud.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {

    // Convert LocalDate to String for database storage
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.toString()
    }

    // Convert String from database to LocalDate
    @TypeConverter
    fun toLocalDate(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    // Convert Category enum to String for database storage
    @TypeConverter
    fun fromCategory(category: Category): String {
        return category.name
    }

    // Convert String from database to Category enum
    @TypeConverter
    fun toCategory(value: String): Category {
        return Category.valueOf(value)
    }
}

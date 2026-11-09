package ph.edu.comteq.pesobud.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Main Room database class for PesoBud
@Database(entities = [Expense::class, Budget::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PesoBudDatabase : RoomDatabase() {

    // DAO getters
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: PesoBudDatabase? = null

        // Safe singleton instance getter
        fun getDatabase(context: Context): PesoBudDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PesoBudDatabase::class.java,
                    "pesobud_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

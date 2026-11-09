package ph.edu.comteq.pesobud.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

// Data access methods for expenses table
@Dao
interface ExpenseDao {

    // Add a new expense
    @Insert
    suspend fun insert(expense: Expense)

    // Update existing expense
    @Update
    suspend fun update(expense: Expense)

    // Delete an expense
    @Delete
    suspend fun delete(expense: Expense)

    // Find expense by ID
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: Int): Expense?

    // Get all expenses sorted by date and id
    @Query("SELECT * FROM expenses ORDER BY date DESC, id DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    // Get expenses between two dates for monthly total
    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC, id DESC")
    fun getExpensesBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>>
}

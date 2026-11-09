package ph.edu.comteq.pesobud.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Data access methods for budget table
@Dao
interface BudgetDao {

    // Save or update monthly budget
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudget(budget: Budget)

    // Observe current monthly budget
    @Query("SELECT * FROM budget WHERE id = 1")
    fun getBudget(): Flow<Budget?>
}

package ph.edu.comteq.pesobud.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

// Table for storing individual expenses
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val category: Category,
    val note: String,
    val date: LocalDate
)

package ph.edu.comteq.pesobud.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Table for storing monthly budget limit
@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey
    val id: Int = 1,
    val amount: Double
)

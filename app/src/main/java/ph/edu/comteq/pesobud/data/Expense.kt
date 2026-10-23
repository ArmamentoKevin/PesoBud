package ph.edu.comteq.pesobud.data

data class Expense(
    val id: String,
    val amount: Double,
    val category: Category,
    val note: String,
    val date: String
)

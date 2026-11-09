package ph.edu.comteq.pesobud

import android.app.Application
import ph.edu.comteq.pesobud.data.BudgetRepository
import ph.edu.comteq.pesobud.data.ExpenseRepository
import ph.edu.comteq.pesobud.data.PesoBudDatabase

// Application class holding database and repository instances
class PesoBudApplication : Application() {

    // Database singleton instance
    val database by lazy { PesoBudDatabase.getDatabase(this) }

    // Expense repository instance
    val expenseRepository by lazy { ExpenseRepository(database.expenseDao()) }

    // Budget repository instance
    val budgetRepository by lazy { BudgetRepository(database.budgetDao()) }
}

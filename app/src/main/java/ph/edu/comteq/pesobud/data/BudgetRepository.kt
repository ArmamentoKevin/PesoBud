package ph.edu.comteq.pesobud.data

import kotlinx.coroutines.flow.Flow

// Repository for budget database operations
class BudgetRepository(private val budgetDao: BudgetDao) {

    // Stream current monthly budget
    val budget: Flow<Budget?> = budgetDao.getBudget()

    // Save or update monthly budget
    suspend fun upsertBudget(budget: Budget) {
        budgetDao.upsertBudget(budget)
    }
}

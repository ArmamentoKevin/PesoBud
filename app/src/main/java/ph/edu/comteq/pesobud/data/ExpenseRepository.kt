package ph.edu.comteq.pesobud.data

import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

// Repository for expense database operations
class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // Stream all expenses sorted by date descending
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    // Get expenses for a specific month
    fun getExpensesForMonth(yearMonth: YearMonth): Flow<List<Expense>> {
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        return expenseDao.getExpensesBetween(startDate, endDate)
    }

    // Insert a new expense
    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    // Update an existing expense
    suspend fun update(expense: Expense) {
        expenseDao.update(expense)
    }

    // Delete an expense
    suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }

    // Find expense by ID
    suspend fun getById(id: Int): Expense? {
        return expenseDao.getById(id)
    }
}

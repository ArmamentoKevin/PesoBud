package ph.edu.comteq.pesobud.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ph.edu.comteq.pesobud.data.Budget
import ph.edu.comteq.pesobud.data.BudgetRepository
import ph.edu.comteq.pesobud.data.BudgetSummary
import ph.edu.comteq.pesobud.data.Category
import ph.edu.comteq.pesobud.data.Expense
import ph.edu.comteq.pesobud.data.ExpenseRepository
import ph.edu.comteq.pesobud.data.calculateBudgetSummary
import java.time.LocalDate
import java.time.YearMonth

// UI state holder for Home screen
data class HomeUiState(
    val totalSpentThisMonth: Double = 0.0,
    val budgetSummary: BudgetSummary = calculateBudgetSummary(0.0, null),
    val recentExpenses: List<Expense> = emptyList()
)

// ViewModel for Home screen managing overview data
class HomeViewModel(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    // Current month expenses flow
    private val currentMonthExpensesFlow = expenseRepository.getExpensesForMonth(YearMonth.now())

    // All expenses flow to take 5 most recent
    private val allExpensesFlow = expenseRepository.allExpenses

    // Budget flow
    private val budgetFlow = budgetRepository.budget

    // Combine flows into HomeUiState
    val uiState: StateFlow<HomeUiState> = combine(
        currentMonthExpensesFlow,
        allExpensesFlow,
        budgetFlow
    ) { monthlyExpenses, allExpenses, budget ->
        val totalSpentThisMonth = monthlyExpenses.sumOf { it.amount }
        val budgetAmount = budget?.amount
        val summary = calculateBudgetSummary(totalSpentThisMonth, budgetAmount)
        val recentExpenses = allExpenses.take(5)

        HomeUiState(
            totalSpentThisMonth = totalSpentThisMonth,
            budgetSummary = summary,
            recentExpenses = recentExpenses
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    // TEMPORARY DEBUG ACTION: Insert sample expense and set sample budget 5000 for testing
    fun insertDebugSampleData() {
        viewModelScope.launch {
            budgetRepository.upsertBudget(Budget(id = 1, amount = 5000.0))
            expenseRepository.insert(
                Expense(
                    amount = 150.0,
                    category = Category.FOOD,
                    note = "Jollibee (Debug)",
                    date = LocalDate.now()
                )
            )
        }
    }
}

// Factory for creating HomeViewModel with repository dependencies
class HomeViewModelFactory(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(expenseRepository, budgetRepository) as T
    }
}

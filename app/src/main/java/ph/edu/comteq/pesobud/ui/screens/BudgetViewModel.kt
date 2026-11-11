package ph.edu.comteq.pesobud.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ph.edu.comteq.pesobud.data.Budget
import ph.edu.comteq.pesobud.data.BudgetRepository
import ph.edu.comteq.pesobud.data.BudgetSummary
import ph.edu.comteq.pesobud.data.ExpenseRepository
import ph.edu.comteq.pesobud.data.calculateBudgetSummary
import java.time.YearMonth

// UI state holder for Budget screen
data class BudgetUiState(
    val budgetInputText: String = "",
    val budgetSummary: BudgetSummary = calculateBudgetSummary(0.0, null),
    val budgetError: String? = null
)

// ViewModel managing monthly budget input, validation, and status calculations
class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    expenseRepository: ExpenseRepository
) : ViewModel() {

    // Null means the user has not typed anything in the input field yet
    private val _budgetInputText = MutableStateFlow<String?>(null)
    private val _budgetError = MutableStateFlow<String?>(null)

    // Shared flow to trigger snackbar message on save
    private val _saveEvent = MutableSharedFlow<Unit>()
    val saveEvent: SharedFlow<Unit> = _saveEvent.asSharedFlow()

    // Current month expenses flow
    private val currentMonthExpensesFlow = expenseRepository.getExpensesForMonth(YearMonth.now())

    // Combine budget and monthly expenses into BudgetUiState
    val uiState: StateFlow<BudgetUiState> = combine(
        budgetRepository.budget,
        currentMonthExpensesFlow,
        _budgetInputText,
        _budgetError
    ) { budget, monthlyExpenses, inputText, inputError ->
        val spent = monthlyExpenses.sumOf { it.amount }
        val summary = calculateBudgetSummary(spent, budget?.amount)

        // Show saved budget while input is null, otherwise show exactly what user typed
        val displayInput = if (inputText == null) {
            if (budget != null && budget.amount > 0) {
                if (budget.amount % 1.0 == 0.0) budget.amount.toInt().toString() else budget.amount.toString()
            } else {
                ""
            }
        } else {
            inputText
        }

        BudgetUiState(
            budgetInputText = displayInput,
            budgetSummary = summary,
            budgetError = inputError
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetUiState()
    )

    // Update budget text input
    fun onBudgetInputChange(newInput: String) {
        _budgetInputText.update { newInput }
        _budgetError.update { null }
    }

    // Validate input and save budget to Room database
    fun saveBudget() {
        val rawInput = _budgetInputText.value
        val textToValidate = if (rawInput == null) {
            val currentBudget = uiState.value.budgetSummary.budget
            if (currentBudget > 0) {
                if (currentBudget % 1.0 == 0.0) currentBudget.toInt().toString() else currentBudget.toString()
            } else {
                ""
            }
        } else {
            rawInput
        }

        val trimmed = textToValidate.trim()
        val regex = Regex("""^\d+(\.\d{1,2})?$""")

        if (trimmed.isBlank()) {
            _budgetError.update { "Budget amount is required" }
            return
        }

        if (!regex.matches(trimmed)) {
            val decimals = trimmed.substringAfter('.', "")
            if (trimmed.contains('.') && decimals.length > 2) {
                _budgetError.update { "At most 2 decimal places allowed" }
            } else {
                _budgetError.update { "Invalid budget amount" }
            }
            return
        }

        val amount = trimmed.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _budgetError.update { "Budget must be greater than 0" }
            return
        }

        if (amount > 1_000_000.0) {
            _budgetError.update { "Budget cannot exceed ₱ 1,000,000.00" }
            return
        }

        viewModelScope.launch {
            budgetRepository.upsertBudget(Budget(id = 1, amount = amount))
            _saveEvent.emit(Unit)
        }
    }
}

// Factory for creating BudgetViewModel
class BudgetViewModelFactory(
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BudgetViewModel(budgetRepository, expenseRepository) as T
    }
}

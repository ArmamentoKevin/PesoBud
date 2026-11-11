package ph.edu.comteq.pesobud.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ph.edu.comteq.pesobud.data.Category
import ph.edu.comteq.pesobud.data.Expense
import ph.edu.comteq.pesobud.data.ExpenseRepository
import java.time.LocalDate

// Form state for adding or editing an expense
data class AddEditExpenseUiState(
    val amountText: String = "",
    val selectedCategory: Category = Category.FOOD,
    val noteText: String = "",
    val date: LocalDate = LocalDate.now(),
    val amountError: String? = null,
    val dateError: String? = null,
    val isEditMode: Boolean = false,
    val isDatePickerOpen: Boolean = false
)

// ViewModel managing form input, validation, saving, and deleting
class AddEditExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
    private val expenseId: Int? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditExpenseUiState(isEditMode = expenseId != null))
    val uiState: StateFlow<AddEditExpenseUiState> = _uiState.asStateFlow()

    // Shared flow to trigger snackbar message on save or update
    private val _saveEvent = MutableSharedFlow<Unit>()
    val saveEvent: SharedFlow<Unit> = _saveEvent.asSharedFlow()

    // Shared flow to safely navigate back if expense ID is not found
    private val _notFoundEvent = MutableSharedFlow<Unit>()
    val notFoundEvent: SharedFlow<Unit> = _notFoundEvent.asSharedFlow()

    // Hold the loaded expense object for delete and undo
    var loadedExpense: Expense? = null
        private set

    init {
        if (expenseId != null) {
            viewModelScope.launch {
                val existing = expenseRepository.getById(expenseId)
                if (existing != null) {
                    loadedExpense = existing
                    _uiState.update {
                        it.copy(
                            amountText = existing.amount.toString(),
                            selectedCategory = existing.category,
                            noteText = existing.note,
                            date = existing.date
                        )
                    }
                } else {
                    _notFoundEvent.emit(Unit)
                }
            }
        }
    }

    // Update amount input
    fun onAmountChange(newAmount: String) {
        _uiState.update { it.copy(amountText = newAmount, amountError = null) }
    }

    // Update category selection
    fun onCategoryChange(newCategory: Category) {
        _uiState.update { it.copy(selectedCategory = newCategory) }
    }

    // Update note input
    fun onNoteChange(newNote: String) {
        _uiState.update { it.copy(noteText = newNote) }
    }

    // Update date selection
    fun onDateChange(newDate: LocalDate) {
        _uiState.update { it.copy(date = newDate, dateError = null) }
    }

    // Toggle date picker dialog visibility
    fun setDatePickerOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isDatePickerOpen = isOpen) }
    }

    // Delete expense and pass deleted expense object to callback
    fun deleteExpense(onDeleted: (Expense) -> Unit) {
        val expenseToDelete = loadedExpense
        if (expenseToDelete != null) {
            viewModelScope.launch {
                expenseRepository.delete(expenseToDelete)
                onDeleted(expenseToDelete)
            }
        }
    }

    // Validate inputs and save expense
    fun saveExpense(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        var isValid = true

        val trimmedAmount = currentState.amountText.trim()
        val amountRegex = Regex("""^\d+(\.\d{1,2})?$""")

        var amountError: String? = null
        if (trimmedAmount.isBlank()) {
            amountError = "Amount is required"
            isValid = false
        } else if (!amountRegex.matches(trimmedAmount)) {
            val decimals = trimmedAmount.substringAfter('.', "")
            amountError = if (trimmedAmount.contains('.') && decimals.length > 2) {
                "At most 2 decimal places allowed"
            } else {
                "Invalid amount number"
            }
            isValid = false
        } else {
            val amountNumber = trimmedAmount.toDoubleOrNull()
            if (amountNumber == null || amountNumber <= 0) {
                amountError = "Amount must be greater than 0"
                isValid = false
            }
        }

        // Date validation
        var dateError: String? = null
        if (currentState.date.isAfter(LocalDate.now())) {
            dateError = "Date cannot be in the future"
            isValid = false
        }

        if (!isValid) {
            _uiState.update {
                it.copy(amountError = amountError, dateError = dateError)
            }
            return
        }

        val validAmount = trimmedAmount.toDouble()

        // Save expense through repository
        viewModelScope.launch {
            val newExpense = Expense(
                id = expenseId ?: 0,
                amount = validAmount,
                category = currentState.selectedCategory,
                note = currentState.noteText.trim(),
                date = currentState.date
            )

            if (expenseId == null) {
                expenseRepository.insert(newExpense)
            } else {
                expenseRepository.update(newExpense)
            }

            _saveEvent.emit(Unit)
            onSuccess()
        }
    }
}

// Factory for creating AddEditExpenseViewModel
class AddEditExpenseViewModelFactory(
    private val expenseRepository: ExpenseRepository,
    private val expenseId: Int? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddEditExpenseViewModel(expenseRepository, expenseId) as T
    }
}

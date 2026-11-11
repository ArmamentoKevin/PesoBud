package ph.edu.comteq.pesobud.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import ph.edu.comteq.pesobud.data.Category
import ph.edu.comteq.pesobud.data.Expense
import ph.edu.comteq.pesobud.data.ExpenseRepository

// UI state for History screen
data class HistoryUiState(
    val searchQuery: String = "",
    val selectedCategory: Category? = null,
    val filteredExpenses: List<Expense> = emptyList(),
    val isEmptyData: Boolean = true,
    val isEmptyFilterResult: Boolean = false
)

// ViewModel managing history search and category filtering
class HistoryViewModel(
    expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<Category?>(null)

    // Combine all expenses from repository with search and filter state
    val uiState: StateFlow<HistoryUiState> = combine(
        expenseRepository.allExpenses,
        _searchQuery,
        _selectedCategory
    ) { allExpenses, query, categoryFilter ->
        val trimmedQuery = query.trim()
        val isEmptyData = allExpenses.isEmpty()

        val filtered = if (isEmptyData) {
            emptyList()
        } else {
            allExpenses.filter { expense ->
                val matchesCategory = categoryFilter == null || expense.category == categoryFilter
                val matchesSearch = trimmedQuery.isBlank() || expense.note.contains(trimmedQuery, ignoreCase = true)
                matchesCategory && matchesSearch
            }
        }

        val isEmptyFilterResult = !isEmptyData && filtered.isEmpty()

        HistoryUiState(
            searchQuery = query,
            selectedCategory = categoryFilter,
            filteredExpenses = filtered,
            isEmptyData = isEmptyData,
            isEmptyFilterResult = isEmptyFilterResult
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState()
    )

    // Update search query text
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.update { newQuery }
    }

    // Clear search text
    fun clearSearchQuery() {
        _searchQuery.update { "" }
    }

    // Update or toggle category filter
    fun onCategorySelect(category: Category?) {
        _selectedCategory.update { category }
    }
}

// Factory for creating HistoryViewModel
class HistoryViewModelFactory(
    private val expenseRepository: ExpenseRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryViewModel(expenseRepository) as T
    }
}

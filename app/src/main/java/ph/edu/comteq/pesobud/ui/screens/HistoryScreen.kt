package ph.edu.comteq.pesobud.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.rememberNavController
import ph.edu.comteq.pesobud.data.Category
import ph.edu.comteq.pesobud.data.SampleData
import ph.edu.comteq.pesobud.navigation.BottomNavigationBar
import ph.edu.comteq.pesobud.navigation.Screen
import ph.edu.comteq.pesobud.ui.components.EmptyStateView
import ph.edu.comteq.pesobud.ui.components.ExpenseItemRow
import ph.edu.comteq.pesobud.ui.theme.GreenPrimary
import ph.edu.comteq.pesobud.ui.theme.PesoBudTheme

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    initialOpenAddForm: Boolean = false
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<Category?>(null) } // null means "All"
    var forceEmptyState by remember { mutableStateOf(false) }

    var showAddEditForm by remember { mutableStateOf(initialOpenAddForm) }
    var editingExpenseId by remember { mutableStateOf<String?>(null) }

    val categories = listOf("All") + Category.entries.map { it.displayName }

    val filteredExpenses = remember(searchQuery, selectedCategoryFilter, forceEmptyState) {
        if (forceEmptyState) {
            emptyList()
        } else {
            SampleData.expenses.filter { expense ->
                val matchesCategory = selectedCategoryFilter == null || expense.category == selectedCategoryFilter
                val matchesSearch = searchQuery.isBlank() || expense.note.contains(searchQuery, ignoreCase = true) || expense.category.displayName.contains(searchQuery, ignoreCase = true)
                matchesCategory && matchesSearch
            }
        }
    }

    // Modal Add / Edit Expense Overlay inside History Screen
    if (showAddEditForm) {
        Dialog(
            onDismissRequest = { showAddEditForm = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AddEditExpenseScreen(
                expenseId = editingExpenseId,
                onNavigateBack = { showAddEditForm = false }
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title Row with Add Button and Empty State Demo Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Expense History",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Add Expense Button placed inside HistoryScreen
                Button(
                    onClick = {
                        editingExpenseId = null
                        showAddEditForm = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Toggle for demonstration of empty state
                IconButton(
                    onClick = { forceEmptyState = !forceEmptyState }
                ) {
                    Icon(
                        imageVector = if (forceEmptyState) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = "Toggle Empty State View",
                        tint = if (forceEmptyState) Color(0xFFEF4444) else GreenPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by note...", color = Color(0xFF94A3B8)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF64748B)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenPrimary,
                unfocusedBorderColor = Color(0xFFE2E8F0)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter by Category Header
        Text(
            text = "Filter by Category",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF334155)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { categoryName ->
                val isSelected = if (categoryName == "All") selectedCategoryFilter == null else selectedCategoryFilter?.displayName == categoryName
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedCategoryFilter = if (categoryName == "All") null else Category.fromDisplayName(categoryName)
                    },
                    label = { Text(text = categoryName, fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF1F5F9),
                        labelColor = Color(0xFF475569)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = null
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Expenses List or Empty State
        if (filteredExpenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView()
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(filteredExpenses, key = { it.id }) { expense ->
                    ExpenseItemRow(
                        expense = expense,
                        onClick = {
                            editingExpenseId = expense.id
                            showAddEditForm = true
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    PesoBudTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    selectedRoute = Screen.History.route
                )
            }
        ) { innerPadding ->
            HistoryScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

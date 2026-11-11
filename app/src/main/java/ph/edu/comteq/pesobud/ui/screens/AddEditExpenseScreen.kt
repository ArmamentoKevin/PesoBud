package ph.edu.comteq.pesobud.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ph.edu.comteq.pesobud.PesoBudApplication
import ph.edu.comteq.pesobud.data.Category
import ph.edu.comteq.pesobud.data.Expense
import ph.edu.comteq.pesobud.data.formatDate
import ph.edu.comteq.pesobud.ui.components.DeleteConfirmationDialog
import ph.edu.comteq.pesobud.ui.theme.GreenPrimary
import ph.edu.comteq.pesobud.ui.theme.PesoBudTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// Restrict date picker selection to today or past dates
@OptIn(ExperimentalMaterial3Api::class)
object PastAndTodaySelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val selectedDate = Instant.ofEpochMilli(utcTimeMillis)
            .atZone(ZoneId.of("UTC"))
            .toLocalDate()
        return !selectedDate.isAfter(LocalDate.now())
    }
}

@Composable
fun AddEditExpenseScreen(
    expenseId: Int? = null,
    onNavigateBack: () -> Unit,
    onExpenseSaved: () -> Unit = {},
    onExpenseUpdated: () -> Unit = {},
    onExpenseDeleted: (Expense) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AddEditExpenseViewModel = viewModel(
        factory = AddEditExpenseViewModelFactory(
            (LocalContext.current.applicationContext as PesoBudApplication).expenseRepository,
            expenseId
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Safely navigate back if expense ID does not exist
    LaunchedEffect(Unit) {
        viewModel.notFoundEvent.collect {
            onNavigateBack()
        }
    }

    AddEditExpenseContent(
        uiState = uiState,
        onAmountChange = viewModel::onAmountChange,
        onCategoryChange = viewModel::onCategoryChange,
        onNoteChange = viewModel::onNoteChange,
        onDateChange = viewModel::onDateChange,
        setDatePickerOpen = viewModel::setDatePickerOpen,
        onSaveClick = {
            if (uiState.isEditMode) {
                viewModel.saveExpense {
                    onExpenseUpdated()
                    onNavigateBack()
                }
            } else {
                viewModel.saveExpense {
                    onExpenseSaved()
                    onNavigateBack()
                }
            }
        },
        onConfirmDelete = {
            viewModel.deleteExpense { deleted ->
                onExpenseDeleted(deleted)
                onNavigateBack()
            }
        },
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseContent(
    uiState: AddEditExpenseUiState,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (Category) -> Unit,
    onNoteChange: (String) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    setDatePickerOpen: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onConfirmDelete: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDropdownOpen by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Start date picker at currently selected date
    val initialSelectedDateMillis = remember(uiState.date) {
        uiState.date.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    }

    // DatePicker state restricting future dates and showing current date
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        selectableDates = PastAndTodaySelectableDates
    )

    // Synchronize datePickerState when uiState.date updates from async load
    LaunchedEffect(uiState.date) {
        datePickerState.selectedDateMillis = initialSelectedDateMillis
    }

    // Show delete popup when delete button is pressed
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirmDelete = {
                showDeleteDialog = false
                onConfirmDelete()
            }
        )
    }

    // Material 3 DatePicker Dialog
    if (uiState.isDatePickerOpen) {
        DatePickerDialog(
            onDismissRequest = { setDatePickerOpen(false) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val pickedDate = Instant.ofEpochMilli(selectedMillis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            onDateChange(pickedDate)
                        }
                        setDatePickerOpen(false)
                    }
                ) {
                    Text("OK", color = GreenPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { setDatePickerOpen(false) }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = if (uiState.isEditMode) "Edit Expense" else "Add Expense"
                    Text(
                        text = titleText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Amount input field
            Text(
                text = "Amount",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.amountText,
                onValueChange = onAmountChange,
                placeholder = { Text("e.g. 150.00", color = Color(0xFF94A3B8)) },
                leadingIcon = {
                    Text(
                        text = "₱",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                isError = uiState.amountError != null,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    errorBorderColor = Color(0xFFEF4444)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.amountError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.amountError,
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Category select field
            Text(
                text = "Category",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.selectedCategory.displayName,
                    onValueChange = { },
                    readOnly = true,
                    leadingIcon = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(uiState.selectedCategory.backgroundColor)
                        ) {
                            Icon(
                                imageVector = uiState.selectedCategory.icon,
                                contentDescription = null,
                                tint = uiState.selectedCategory.color,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select category"
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDropdownOpen = true }
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { isDropdownOpen = true }
                )

                DropdownMenu(
                    expanded = isDropdownOpen,
                    onDismissRequest = { isDropdownOpen = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Category.entries.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(category.backgroundColor)
                                    ) {
                                        Icon(
                                            imageVector = category.icon,
                                            contentDescription = null,
                                            tint = category.color,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = category.displayName, fontSize = 15.sp)
                                }
                            },
                            onClick = {
                                onCategoryChange(category)
                                isDropdownOpen = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Note input field
            Text(
                text = "Note (Optional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.noteText,
                onValueChange = onNoteChange,
                placeholder = { Text("e.g. Lunch at school", color = Color(0xFF94A3B8)) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Date input field (read-only, opens DatePicker on click)
            Text(
                text = "Date",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = formatDate(uiState.date),
                    onValueChange = {},
                    readOnly = true,
                    isError = uiState.dateError != null,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Date",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Date"
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        errorBorderColor = Color(0xFFEF4444)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { setDatePickerOpen(true) }
                )
            }
            if (uiState.dateError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.dateError,
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Action buttons
            if (uiState.isEditMode) {
                Button(
                    onClick = onSaveClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "Update",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFEF4444)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delete",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = onSaveClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "Save",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddExpenseScreenPreview() {
    PesoBudTheme {
        AddEditExpenseContent(
            uiState = AddEditExpenseUiState(),
            onAmountChange = {},
            onCategoryChange = {},
            onNoteChange = {},
            onDateChange = {},
            setDatePickerOpen = {},
            onSaveClick = {},
            onConfirmDelete = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditExpenseScreenPreview() {
    PesoBudTheme {
        AddEditExpenseContent(
            uiState = AddEditExpenseUiState(isEditMode = true),
            onAmountChange = {},
            onCategoryChange = {},
            onNoteChange = {},
            onDateChange = {},
            setDatePickerOpen = {},
            onSaveClick = {},
            onConfirmDelete = {},
            onNavigateBack = {}
        )
    }
}

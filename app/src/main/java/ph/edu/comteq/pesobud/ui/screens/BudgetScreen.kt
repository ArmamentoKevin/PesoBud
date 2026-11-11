package ph.edu.comteq.pesobud.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ph.edu.comteq.pesobud.PesoBudApplication
import ph.edu.comteq.pesobud.data.BudgetStatus
import ph.edu.comteq.pesobud.data.BudgetSummary
import ph.edu.comteq.pesobud.data.formatMoney
import ph.edu.comteq.pesobud.navigation.BottomNavigationBar
import ph.edu.comteq.pesobud.navigation.Screen
import ph.edu.comteq.pesobud.ui.components.BudgetWarningBanner
import ph.edu.comteq.pesobud.ui.theme.GreenPrimary
import ph.edu.comteq.pesobud.ui.theme.PesoBudTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onNavigateBack: () -> Unit = {},
    onBudgetSaved: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModelFactory(
            (LocalContext.current.applicationContext as PesoBudApplication).budgetRepository,
            (LocalContext.current.applicationContext as PesoBudApplication).expenseRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Trigger save callback when saveEvent is emitted
    LaunchedEffect(Unit) {
        viewModel.saveEvent.collect {
            onBudgetSaved()
        }
    }

    BudgetScreenContent(
        uiState = uiState,
        onBudgetInputChange = viewModel::onBudgetInputChange,
        onSaveBudgetClick = viewModel::saveBudget,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreenContent(
    uiState: BudgetUiState,
    onBudgetInputChange: (String) -> Unit,
    onSaveBudgetClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val summary = uiState.budgetSummary
    val isNoBudget = summary.status == BudgetStatus.NO_BUDGET

    // Donut chart progress color based on status
    val chartColor = when (summary.status) {
        BudgetStatus.OVER_BUDGET -> Color(0xFFEF4444)
        BudgetStatus.NEAR_LIMIT -> Color(0xFFF59E0B)
        else -> GreenPrimary
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Monthly Budget",
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

            // Set Your Monthly Budget Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Adjust,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Set Your Monthly Budget",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.budgetInputText,
                        onValueChange = onBudgetInputChange,
                        placeholder = { Text("0.00", color = Color(0xFF94A3B8)) },
                        leadingIcon = {
                            Text(
                                text = "₱",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        isError = uiState.budgetError != null,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            errorBorderColor = Color(0xFFEF4444)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (uiState.budgetError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.budgetError,
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onSaveBudgetClick,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Save Budget",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Current Month Section
            Text(
                text = "Current Month",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Warning banner if near limit or over budget
            BudgetWarningBanner(budgetSummary = summary)
            if (summary.status == BudgetStatus.NEAR_LIMIT || summary.status == BudgetStatus.OVER_BUDGET) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isNoBudget) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp)
                    ) {
                        Text(
                            text = "No budget set yet.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Set a monthly budget above to start tracking.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Circular Ring Progress
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(120.dp)
                        ) {
                            Canvas(modifier = Modifier.size(110.dp)) {
                                val strokeWidth = 12.dp.toPx()
                                val sweepFraction = (summary.percentageUsed / 100f).coerceAtMost(1f)

                                // Background Ring
                                drawArc(
                                    color = Color(0xFFE2E8F0),
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                // Progress Arc
                                drawArc(
                                    color = chartColor,
                                    startAngle = -90f,
                                    sweepAngle = 360f * sweepFraction,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${summary.percentageUsed}%",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "used",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        // Spent, Remaining, Budget Stats
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Spent",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = formatMoney(summary.spent),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }

                            Column {
                                Text(
                                    text = if (summary.status == BudgetStatus.OVER_BUDGET) "Over Budget" else "Remaining",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                                val remainingText = if (summary.status == BudgetStatus.OVER_BUDGET) {
                                    "Over by ${formatMoney(-summary.remaining)}"
                                } else {
                                    formatMoney(summary.remaining)
                                }
                                Text(
                                    text = remainingText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (summary.status == BudgetStatus.OVER_BUDGET) Color(0xFFEF4444) else GreenPrimary
                                )
                            }

                            Column {
                                Text(
                                    text = "Budget",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = formatMoney(summary.budget),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetScreenPreview() {
    PesoBudTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    selectedRoute = Screen.Budget.route
                )
            }
        ) { innerPadding ->
            BudgetScreenContent(
                uiState = BudgetUiState(),
                onBudgetInputChange = {},
                onSaveBudgetClick = {},
                onNavigateBack = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

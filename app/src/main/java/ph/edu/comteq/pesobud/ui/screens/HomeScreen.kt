package ph.edu.comteq.pesobud.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import ph.edu.comteq.pesobud.data.SampleData
import ph.edu.comteq.pesobud.navigation.BottomNavigationBar
import ph.edu.comteq.pesobud.navigation.Screen
import ph.edu.comteq.pesobud.ui.components.BudgetSummaryCard
import ph.edu.comteq.pesobud.ui.components.ExpenseItemRow
import ph.edu.comteq.pesobud.ui.theme.GreenPrimary
import ph.edu.comteq.pesobud.ui.theme.PesoBudTheme

@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Greeting & Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hi, Jeriko!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Here's your monthly overview.",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }

                IconButton(onClick = { /* Settings action */ }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Monthly Budget Card
            BudgetSummaryCard(
                budget = SampleData.monthlyBudget,
                spent = SampleData.totalSpent,
                remaining = SampleData.remainingBudget
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Expenses Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Expenses",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                TextButton(onClick = onNavigateToHistory) {
                    Text(
                        text = "View all",
                        color = GreenPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (SampleData.expenses.isEmpty()) {
            item {
                Text(
                    text = "No recent expenses yet.",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        } else {
            items(SampleData.expenses, key = { it.id }) { expense ->
                ExpenseItemRow(
                    expense = expense,
                    onClick = onNavigateToHistory
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PesoBudTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    selectedRoute = Screen.Home.route
                )
            }
        ) { innerPadding ->
            HomeScreen(
                onNavigateToHistory = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

package ph.edu.comteq.pesobud.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import ph.edu.comteq.pesobud.ui.screens.AddEditExpenseScreen
import ph.edu.comteq.pesobud.ui.screens.BudgetScreen
import ph.edu.comteq.pesobud.ui.screens.HistoryScreen
import ph.edu.comteq.pesobud.ui.screens.HomeScreen
import ph.edu.comteq.pesobud.ui.screens.SplashScreen
import ph.edu.comteq.pesobud.ui.theme.PesoBudTheme

@Composable
fun NavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val shouldShowBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.History.route,
        Screen.Budget.route
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    onAddExpenseClick = {
                        navController.navigate(Screen.AddExpense.route)
                    }
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToHistory = {
                        navController.navigate(Screen.History.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateToAddExpense = {
                        navController.navigate(Screen.AddExpense.route)
                    },
                    onNavigateToEditExpense = { expenseId ->
                        navController.navigate(Screen.EditExpense.createRoute(expenseId))
                    }
                )
            }

            composable(Screen.Budget.route) {
                BudgetScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AddExpense.route) {
                AddEditExpenseScreen(
                    expenseId = null,
                    onNavigateBack = { navController.popBackStack() },
                    onExpenseSaved = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Expense saved")
                        }
                    }
                )
            }

            composable(
                route = Screen.EditExpense.route,
                arguments = listOf(navArgument("expenseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getInt("expenseId")
                AddEditExpenseScreen(
                    expenseId = expenseId,
                    onNavigateBack = { navController.popBackStack() },
                    onExpenseSaved = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Expense saved")
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavGraphPreview() {
    PesoBudTheme {
        NavGraph()
    }
}

package ph.edu.comteq.pesobud.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import ph.edu.comteq.pesobud.PesoBudApplication
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
    val context = LocalContext.current
    val application = context.applicationContext as? PesoBudApplication

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
                    },
                    onNavigateToEditExpense = { expenseId ->
                        navController.navigate(Screen.EditExpense.createRoute(expenseId))
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
                    onExpenseUpdated = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Expense updated")
                        }
                    },
                    onExpenseDeleted = { deletedExpense ->
                        coroutineScope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Expense deleted",
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                application?.expenseRepository?.insert(deletedExpense)
                            }
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

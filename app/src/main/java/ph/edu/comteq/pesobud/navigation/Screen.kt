package ph.edu.comteq.pesobud.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    data object Splash : Screen("splash")
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object History : Screen("history", "History", Icons.Default.History)
    data object Budget : Screen("budget", "Budget", Icons.Default.AccountBalanceWallet)

    companion object {
        val bottomBarScreens = listOf(Home, History, Budget)
    }
}

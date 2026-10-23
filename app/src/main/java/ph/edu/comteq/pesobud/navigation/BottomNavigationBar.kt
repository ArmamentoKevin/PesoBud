package ph.edu.comteq.pesobud.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ph.edu.comteq.pesobud.ui.theme.GreenPrimary
import ph.edu.comteq.pesobud.ui.theme.PesoBudTheme

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    selectedRoute: String? = null
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val activeRoute = selectedRoute ?: navBackStackEntry?.destination?.route ?: Screen.Home.route

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home tab
            BottomNavItem(
                screen = Screen.Home,
                isSelected = activeRoute == Screen.Home.route,
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            // History tab
            BottomNavItem(
                screen = Screen.History,
                isSelected = activeRoute == Screen.History.route,
                onClick = {
                    navController.navigate(Screen.History.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            // Budget tab
            BottomNavItem(
                screen = Screen.Budget,
                isSelected = activeRoute == Screen.Budget.route,
                onClick = {
                    navController.navigate(Screen.Budget.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (isSelected) GreenPrimary else Color(0xFF94A3B8)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        screen.icon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = screen.title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        screen.title?.let { title ->
            Text(
                text = title,
                color = color,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    PesoBudTheme {
        BottomNavigationBar(
            navController = rememberNavController(),
            selectedRoute = Screen.Home.route
        )
    }
}

package com.whyaji.warehousestockapp.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.whyaji.warehousestockapp.ui.screen.main.navigation.CheckoutScreen
import com.whyaji.warehousestockapp.ui.screen.main.navigation.HomeScreen
import com.whyaji.warehousestockapp.ui.screen.main.navigation.ProfileScreen

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: Any
)

@Composable
fun BottomNavigationBar(navController: NavHostController, indexBottomNavbar: (Int) -> Unit = {}) {
    val navigationItems = listOf(
        NavigationItem(
            title = "Home",
            icon = Icons.Default.Home,
            route = HomeScreen
        ),
        NavigationItem(
            title = "Check Out",
            icon = Icons.Default.ShoppingCart,
            route = CheckoutScreen
        ),
        NavigationItem(
            title = "Profile",
            icon = Icons.Default.Person,
            route = ProfileScreen
        ),
    )

    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar(
        containerColor = Color.White
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    indexBottomNavbar(index)
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = {
                    Text(
                        item.title,
                        color = if (index == selectedNavigationIndex.intValue)
                            Color.Black
                        else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )

            )
        }
    }
}
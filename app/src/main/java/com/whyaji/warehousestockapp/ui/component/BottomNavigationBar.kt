package com.whyaji.warehousestockapp.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.whyaji.warehousestockapp.ui.screen.main.navigation.CheckoutScreen
import com.whyaji.warehousestockapp.ui.screen.main.navigation.HomeScreen
import com.whyaji.warehousestockapp.ui.screen.main.navigation.ProfileScreen
import com.whyaji.warehousestockapp.viewmodel.MainViewModel

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int? = null,
    val route: Any
)

@Composable
fun BottomNavigationBar(
    viewModel: MainViewModel,
    navController: NavHostController,
    indexBottomNavbar: (Int) -> Unit = {}
) {
    val cartItems = viewModel.cartItems.collectAsState()
    val cartItemRefresh = viewModel.cartItemRefresh.collectAsState()

    LaunchedEffect(cartItemRefresh.value) {
        if (cartItemRefresh.value) {
            viewModel.getAllCartItems()
            viewModel.setCartItemRefresh(false)
        }
    }

    val cartItemsCount = cartItems.value.sumOf {
        it.cartItem.quantity
    }

    val navigationItems = listOf(
        NavigationItem(
            title = "Home",
            icon = Icons.Default.Home,
            badgeCount = null,
            route = HomeScreen
        ),
        NavigationItem(
            title = "Check Out",
            icon = Icons.Default.ShoppingCart,
            badgeCount = cartItemsCount,
            route = CheckoutScreen
        ),
        NavigationItem(
            title = "Profile",
            icon = Icons.Default.Person,
            badgeCount = null,
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
                    BadgedBox(
                        badge = {
                            if (item.badgeCount != null && item.badgeCount > 0) {
                                Badge {
                                    Text(text = item.badgeCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(imageVector = item.icon, contentDescription = item.title)
                    }
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
package com.whyaji.warehousestockapp.ui.screen.main.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.whyaji.warehousestockapp.ui.component.BottomNavigationBar
import com.whyaji.warehousestockapp.ui.screen.main.screen.additem.AddItemScreen
import com.whyaji.warehousestockapp.ui.screen.main.screen.checkout.CheckoutScreen
import com.whyaji.warehousestockapp.ui.screen.main.screen.home.HomeScreen
import com.whyaji.warehousestockapp.ui.screen.main.screen.profile.ProfileScreen
import com.whyaji.warehousestockapp.viewmodel.MainViewModel
import kotlinx.serialization.Serializable

@Serializable
data object HomeScreen

@Serializable
data object CheckoutScreen

@Serializable
data object AddItemScreen

@Serializable
data object ProfileScreen

@Composable
fun MainNavigation(mainViewModel: MainViewModel, navigateTo: (Any) -> Unit, backPress: () -> Unit) {
    val navController = rememberNavController()

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->

        val graph =
            navController.createGraph(startDestination = HomeScreen) {
                composable<HomeScreen> {
                    HomeScreen(mainViewModel, navigateTo)
                }
                composable<CheckoutScreen> {
                    CheckoutScreen()
                }
                composable<AddItemScreen> {
                    AddItemScreen()
                }
                composable<ProfileScreen> {
                    ProfileScreen(mainViewModel)
                }
            }
        NavHost(
            navController = navController,
            graph = graph,
            modifier = Modifier.padding(innerPadding)
        )

    }
}
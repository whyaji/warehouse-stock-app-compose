package com.whyaji.warehousestockapp.ui.screen.main.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.whyaji.warehousestockapp.ui.component.BottomNavigationBar
import com.whyaji.warehousestockapp.ui.navigation.AddItemScreen
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
data object ProfileScreen

@Composable
fun MainNavigation(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    var currentIndex = remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                mainViewModel, navController,
                {
                    currentIndex.intValue = it
                },
            )
        },
        floatingActionButton = @Composable {
            if (currentIndex.intValue == 0) {
                FloatingActionButton(
                    onClick = { mainViewModel.setNavigateTo(AddItemScreen) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
            }
        }
    ) { paddingValues ->

        val graph =
            navController.createGraph(startDestination = HomeScreen) {
                composable<HomeScreen> {
                    HomeScreen(mainViewModel)
                }
                composable<CheckoutScreen> {
                    CheckoutScreen(mainViewModel)
                }
                composable<ProfileScreen> {
                    ProfileScreen(mainViewModel)
                }
            }

        NavHost(
            navController = navController,
            graph = graph,
            modifier = Modifier.padding(paddingValues)
        )

    }
}
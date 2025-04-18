package com.whyaji.warehousestockapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.whyaji.warehousestockapp.ui.screen.auth.LoginScreen
import com.whyaji.warehousestockapp.ui.screen.detail.DetailScreen
import com.whyaji.warehousestockapp.ui.screen.main.navigation.MainNavigation
import com.whyaji.warehousestockapp.ui.screen.additem.AddItemScreen
import com.whyaji.warehousestockapp.ui.screen.updateitem.UpdateItemScreen
import com.whyaji.warehousestockapp.viewmodel.MainViewModel
import kotlinx.serialization.Serializable

@Serializable
data object LoginScreen

@Serializable
data object MainScreen

@Serializable
data class DetailScreen(
    val itemId: Int
)

@Serializable
data class UpdateItemScreen(
    val itemId: Int
)

@Serializable
data object AddItemScreen

@Composable
fun Navigation(mainViewModel: MainViewModel, isAuthenticated: Boolean) {
    val navController = rememberNavController()

    // On Login
    val loginState = mainViewModel.loginState.collectAsState()
    LaunchedEffect(loginState.value) {
        if (loginState.value is MainViewModel.LoginState.Success) {
            navController.navigate(MainScreen) {
                navController.popBackStack()
            }
        }
    }

    // On Logout
    val logoutState = mainViewModel.logoutState.collectAsState()
    LaunchedEffect(logoutState.value) {
        if (logoutState.value is MainViewModel.LogoutState.Success) {
            navController.navigate(LoginScreen) {
                navController.popBackStack()
            }
        }
    }

    val isBackStack = mainViewModel.backStack.collectAsState().value
    LaunchedEffect(isBackStack) {
        if (isBackStack) {
            navController.popBackStack()
            mainViewModel.setBackStack(false)
        }
    }

    val routeNavigate = mainViewModel.routeNavigate.collectAsState().value
    LaunchedEffect(routeNavigate) {
        if (routeNavigate != null) {
            navController.navigate(routeNavigate)
            mainViewModel.setNavigateTo(null)
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (!isAuthenticated) LoginScreen else MainScreen,
    ) {
        composable<LoginScreen> {
            LoginScreen(viewModel = mainViewModel)
        }
        composable<MainScreen> {
            MainNavigation(mainViewModel)
        }
        composable<DetailScreen> {
            val args = it.toRoute<DetailScreen>()
            DetailScreen(mainViewModel, itemId = args.itemId)
        }
        composable<UpdateItemScreen> {
            val args = it.toRoute<UpdateItemScreen>()
            UpdateItemScreen(mainViewModel, itemId = args.itemId)
        }
        composable<AddItemScreen> {
            AddItemScreen(mainViewModel)
        }
    }
}
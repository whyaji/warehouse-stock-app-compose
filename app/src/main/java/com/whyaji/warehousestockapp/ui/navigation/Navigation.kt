package com.whyaji.warehousestockapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.whyaji.warehousestockapp.ui.screen.auth.LoginScreen
import com.whyaji.warehousestockapp.ui.screen.main.navigation.MainNavigation
import com.whyaji.warehousestockapp.viewmodel.MainViewModel
import kotlinx.serialization.Serializable

@Serializable
data object LoginScreen

@Serializable
data object MainScreen

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
    }
}
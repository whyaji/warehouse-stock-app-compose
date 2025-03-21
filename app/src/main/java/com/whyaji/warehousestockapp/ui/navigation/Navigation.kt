package com.whyaji.warehousestockapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.whyaji.warehousestockapp.ui.screen.auth.LoginScreen
import com.whyaji.warehousestockapp.ui.screen.home.HomeScreen
import com.whyaji.warehousestockapp.viewmodel.MainViewModel
import kotlinx.serialization.Serializable

@Serializable
data object LoginScreen

@Serializable
data object HomeScreen

@Composable
fun Navigation(viewModel: MainViewModel, isTokenEmpty: Boolean = true) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val navController = rememberNavController()

        // On Login
        val loginState = viewModel.loginState.collectAsState()
        LaunchedEffect(loginState.value) {
            if (loginState.value is MainViewModel.LoginState.Success) {
                navController.navigate(HomeScreen) {
                    navController.popBackStack()
                }
            }
        }

        // On Logout
        val logoutState = viewModel.logoutState.collectAsState()
        LaunchedEffect(logoutState.value) {
            if (logoutState.value is MainViewModel.LogoutState.Success) {
                navController.navigate(LoginScreen) {
                    navController.popBackStack()
                }
            }
        }

        NavHost(
            navController = navController,
            startDestination = if (isTokenEmpty) LoginScreen else HomeScreen,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<LoginScreen> {
                LoginScreen(viewModel = viewModel)
            }
            composable<HomeScreen> {
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}
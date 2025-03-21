package com.whyaji.warehousestockapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.whyaji.warehousestockapp.ui.screen.auth.LoginScreen
import com.whyaji.warehousestockapp.ui.screen.home.ListScreen
import com.whyaji.warehousestockapp.viewmodel.MainViewModel

@Composable
fun Navigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    // On Login
    val loginState by viewModel.loginState.collectAsState()
    LaunchedEffect(loginState) {
        if (loginState is MainViewModel.LoginState.Success) {
            navController.navigate("list") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // On Logout
    val logoutState by viewModel.logoutState.collectAsState()
    LaunchedEffect(logoutState) {
        if (logoutState is MainViewModel.LogoutState.Success) {
            navController.navigate("login") {
                popUpTo("list") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {LoginScreen(viewModel = viewModel)}
        composable("list") {ListScreen(viewModel = viewModel)}
    }
}
package com.whyaji.warehousestockapp.ui.screen.main.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.whyaji.warehousestockapp.viewmodel.MainViewModel

@Composable
fun ProfileScreen(mainViewModel: MainViewModel){
    val logoutState = mainViewModel.logoutState.collectAsState()
    val userState = mainViewModel.userState.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.getUser()
    }

    Column (modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Profile Screen",
            style = MaterialTheme.typography.headlineLarge
        )

        when (val state = userState.value) {
            is MainViewModel.UserState.Success -> {
                Text(
                    text = "Email: ${state.user.email}"
                )
                Text(
                    text = "Name: ${state.user.name}"
                )
                Text(
                    text = "Department: ${state.user.department}"
                )
                Text(
                    text = "Position: ${state.user.position}"
                )
            }
            is MainViewModel.UserState.Error -> {
                Text(text = state.message)
            }
            MainViewModel.UserState.Loading -> {
                CircularProgressIndicator()
            }
            else -> {}
        }

        Button(onClick = {
            mainViewModel.logout()
        }) {
            Text("Logout")
        }

        when (logoutState.value) {
            is MainViewModel.LogoutState.Loading -> {
                CircularProgressIndicator()
            }
            is MainViewModel.LogoutState.Success -> {
                Text("Logout success")
            }
            is MainViewModel.LogoutState.Error -> {
                Text("Logout error")
            }
            else -> {}
        }
    }
}
package com.whyaji.warehousestockapp.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.whyaji.warehousestockapp.R
import com.whyaji.warehousestockapp.model.LoginRequest
import com.whyaji.warehousestockapp.ui.component.TextInput
import com.whyaji.warehousestockapp.viewmodel.MainViewModel

@Composable
fun LoginScreen(viewModel: MainViewModel) {
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState = viewModel.loginState.collectAsState()
    var errorMessage by remember { mutableStateOf("") } // State to store the error message

    var emailErrorMessage by remember { mutableStateOf("") }
    var passwordErrorMessage by remember { mutableStateOf("") }

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    val handleLogin : () -> Unit = {
        focusManager.clearFocus() // clear focus
        // check email and password
        emailErrorMessage = if (email.isEmpty()) {
            "Email is required"
        } else {
            ""
        }

        passwordErrorMessage = if (password.isEmpty()) {
            "Password is required"
        } else {
            ""
        }

        if (emailErrorMessage.isEmpty() && passwordErrorMessage.isEmpty()) {
            viewModel.login(LoginRequest(email, password))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Icon
        Image(
            painter = painterResource(id = R.drawable.ic_app),
            contentDescription = "App Icon",
            modifier = Modifier.size(96.dp).padding(top = 24.dp),
        )

        Text(
            text = "Warehouse Stock App",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
        )

        // Title
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
        )

        // Email TextField
        TextInput(
            value = email,
            onValueChange = {
                email = it
                emailErrorMessage = ""
                            },
            label = @Composable { Text("Email") },
            modifier = Modifier.fillMaxWidth().focusRequester(emailFocusRequester),
            isEmailTf = true,
            leadingIcon = @Composable {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Email Icon",
                    tint = Color.Gray
                )
            },
            errorMessage = emailErrorMessage,
            nextFocusRequester = passwordFocusRequester
        )

        Spacer(modifier = Modifier.height(16.dp))

        val passwordVisibility = remember { mutableStateOf(false) }

        // Password TextField
        TextInput(
            value = password,
            onValueChange = {
                password = it
                passwordErrorMessage = ""
            },
            label = @Composable { Text("Password") },
            modifier = Modifier.fillMaxWidth().focusRequester(passwordFocusRequester),
            isPasswordTf = true,
            visibility = passwordVisibility,
            leadingIcon = @Composable {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Password Icon",
                    tint = Color.Gray
                )
            },
            errorMessage = passwordErrorMessage,
            isLast = true,
            performAction = handleLogin
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            enabled = loginState.value != MainViewModel.LoginState.Loading,
            onClick = handleLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(text = "Login", style = MaterialTheme.typography.titleMedium)
            if (loginState.value == MainViewModel.LoginState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(start = 16.dp).size(20.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login State Handling
        when (val state = loginState.value) {
            is MainViewModel.LoginState.Error -> {
                // Update the error message and show the dialog
                errorMessage = state.message
                AlertDialog(
                    onDismissRequest = {
                        viewModel.setLoginStateValue(MainViewModel.LoginState.Idle)
                    },
                    title = { Text("Error") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.setLoginStateValue(MainViewModel.LoginState.Idle)
                        }) {
                            Text("OK")
                        }
                    }
                )
            }
            else -> {}
        }
    }
}
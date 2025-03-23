package com.whyaji.warehousestockapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whyaji.warehousestockapp.data.domain.repository.AuthRepository
import com.whyaji.warehousestockapp.data.domain.repository.ItemRepository
import com.whyaji.warehousestockapp.data.local.preference.TokenManager
import com.whyaji.warehousestockapp.model.Item
import com.whyaji.warehousestockapp.model.LoginRequest
import com.whyaji.warehousestockapp.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val itemRepository: ItemRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // State for login success/failure
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // State for logout
    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState

    private val _userState = MutableStateFlow<UserState>(UserState.Idle)
    val userState: StateFlow<UserState> = _userState

    // Login function
    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = authRepository.login(request)
                if (response.isSuccessful) {
                    val user = response.body()?.data

                    if (user != null) {
                        authRepository.setUser(
                            UserData(
                                email = user.email,
                                name = user.name,
                                department = user.department,
                                position = user.position,
                                api_token = "-" // user.api_token, // This is not the correct way to store the token
                            )
                        )
                    } else {
                        _loginState.value = LoginState.Error("Invalid user received")
                    }

                    val token = response.body()?.data?.api_token
                    if (token != null) {
                        tokenManager.saveToken(token)
                        _loginState.value = LoginState.Success
                    } else {
                        _loginState.value = LoginState.Error("Invalid token received")
                    }
                } else {
                    _loginState.value = LoginState.Error("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun setLoginStateValue (value: LoginState) {
        _loginState.value = value
    }

    // Logout function
    fun logout() {
        viewModelScope.launch {
            try {
                _logoutState.value = LogoutState.Loading
                tokenManager.clearToken()
                authRepository.deleteUser()
                _logoutState.value = LogoutState.Success
            } catch (e: Exception) {
                _logoutState.value = LogoutState.Error("Failed to logout: ${e.message}")
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            try {
                _userState.value = UserState.Loading
                val user = authRepository.getUser()
                if (user != null) {
                    _userState.value = UserState.Success(user)
                } else {
                    _userState.value = UserState.Error("No user found")
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error("Failed to get user: ${e.message}")
            }
        }
    }

    // Login state sealed class
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }

    // Logout state sealed class
    sealed class LogoutState {
        object Idle : LogoutState()
        object Loading : LogoutState()
        object Success : LogoutState()
        data class Error(val message: String) : LogoutState()
    }

    sealed class UserState {
        object Idle : UserState()
        object Loading : UserState()
        data class Success(val user: UserData) : UserState()
        data class Error(val message: String) : UserState()
    }

    // State for items list
    private val _itemsState = MutableStateFlow<ItemsState>(ItemsState.Idle)
    val itemsState: StateFlow<ItemsState> = _itemsState

    // Fetch items function
    fun getItems() {
        viewModelScope.launch {
            _itemsState.value = ItemsState.Loading
            val response = itemRepository.getItems()
            if (response.isSuccessful) {
                val items = response.body()?.data
                if (items != null) {
                    _itemsState.value = ItemsState.Success(items)
                } else {
                    _itemsState.value = ItemsState.Error("No items found")
                }
            } else {
                _itemsState.value = ItemsState.Error("Failed to fetch items: ${response.message()}")
            }
        }
    }

    fun getAllItems() {
        viewModelScope.launch {
            itemRepository.getAllItems()
        }
    }

    fun insertItem(item: Item) {
        viewModelScope.launch {
            itemRepository.insertItem(item)
        }
    }


    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            itemRepository.deleteItem(itemId)
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            itemRepository.updateItem(item)
        }
    }

    // Items state sealed class
    sealed class ItemsState {
        object Idle : ItemsState()
        object Loading : ItemsState()
        data class Success(val items: List<Item>) : ItemsState()
        data class Error(val message: String) : ItemsState()
    }
}
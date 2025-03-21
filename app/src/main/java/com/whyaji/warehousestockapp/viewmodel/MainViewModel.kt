package com.whyaji.warehousestockapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whyaji.warehousestockapp.data.domain.repository.Repository
import com.whyaji.warehousestockapp.data.local.preference.TokenManager
import com.whyaji.warehousestockapp.model.Item
import com.whyaji.warehousestockapp.model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: Repository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // State for login success/failure
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // State for items list
    private val _itemsState = MutableStateFlow<ItemsState>(ItemsState.Idle)
    val itemsState: StateFlow<ItemsState> = _itemsState

    // State for logout
    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState

    // Login function
    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = repository.login(request)
                if (response.isSuccessful) {
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

    // Fetch items function
    fun getItems() {
        viewModelScope.launch {
            _itemsState.value = ItemsState.Loading
            val response = repository.getItems()
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

    // Logout function
    fun logout() {
        viewModelScope.launch {
            _logoutState.value = LogoutState.Loading
            tokenManager.clearToken()
            _logoutState.value = LogoutState.Success
        }
    }

    fun getAllItems() {
        viewModelScope.launch {
            repository.getAllItems()
        }
    }

    fun insertItem(item: Item) {
        viewModelScope.launch {
            repository.insertItem(item)
        }
    }


    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    // Login state sealed class
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }

    // Items state sealed class
    sealed class ItemsState {
        object Idle : ItemsState()
        object Loading : ItemsState()
        data class Success(val items: List<Item>) : ItemsState()
        data class Error(val message: String) : ItemsState()
    }

    // Logout state sealed class
    sealed class LogoutState {
        object Idle : LogoutState()
        object Loading : LogoutState()
        object Success : LogoutState()
    }
}
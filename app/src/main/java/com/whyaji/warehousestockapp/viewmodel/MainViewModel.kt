package com.whyaji.warehousestockapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whyaji.warehousestockapp.data.domain.repository.AuthRepository
import com.whyaji.warehousestockapp.data.domain.repository.CartRepository
import com.whyaji.warehousestockapp.data.domain.repository.HistoryRepository
import com.whyaji.warehousestockapp.data.domain.repository.ItemRepository
import com.whyaji.warehousestockapp.data.local.preference.TokenManager
import com.whyaji.warehousestockapp.model.CartItem
import com.whyaji.warehousestockapp.model.CartItemWithItem
import com.whyaji.warehousestockapp.model.History
import com.whyaji.warehousestockapp.model.Item
import com.whyaji.warehousestockapp.model.LoginRequest
import com.whyaji.warehousestockapp.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val itemRepository: ItemRepository,
    private val cartRepository: CartRepository,
    private val historyRepository: HistoryRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    // back stack boolean
    private val _backStack = MutableStateFlow(false)
    val backStack: StateFlow<Boolean> = _backStack

    fun setBackStack(value: Boolean) {
        _backStack.value = value
    }

    // navigate to
    private val _routeNavigate = MutableStateFlow<Any?>(null)
    val routeNavigate: StateFlow<Any?> = _routeNavigate

    fun setNavigateTo(value: Any?) {
        _routeNavigate.value = value
    }

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

    // home search query
    private val _homeSearchQuery = MutableStateFlow("")
    val homeSearchQuery: StateFlow<String> = _homeSearchQuery

    fun setHomeSearchQuery(query: String) {
        _homeSearchQuery.value = query
    }

    private val _refreshHome = MutableStateFlow(false)
    val refreshHome: StateFlow<Boolean> = _refreshHome

    fun setRefreshHome(value: Boolean) {
        _refreshHome.value = value
    }

    // State for items list
    private val _itemsState = MutableStateFlow<ItemsState>(ItemsState.Idle)
    val itemsState: StateFlow<ItemsState> = _itemsState

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    private val _itemState = MutableStateFlow<ItemState>(ItemState.Idle)
    val itemState: StateFlow<ItemState> = _itemState

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState

    // Fetch items function
    fun getItems(searchQuery: String = "") {
        viewModelScope.launch {
            _itemsState.value = ItemsState.Loading
            itemRepository.clearItems()
            val response = itemRepository.getItems()
            if (response.isSuccessful) {
                val items = response.body()?.data
                if (items != null) {
                    _itemsState.value = ItemsState.Success(items)
                    _items.value = items
                    items.map { itemRepository.insertItem(it) }
                } else {
                    _itemsState.value = ItemsState.Error("No items found")
                }
            } else {
                _itemsState.value = ItemsState.Error("Failed to fetch items: ${response.message()}")
            }
        }
    }

    fun getAllItems(searchQuery: String = "") {
        viewModelScope.launch {
            _itemsState.value = ItemsState.Loading
            try {
                val items = itemRepository.getAllItems(searchQuery)
                _items.value = items
                _itemsState.value = ItemsState.Success(items)
            } catch (e: Exception) {
                _itemsState.value = ItemsState.Error("Failed to get items: ${e.message}")
            }
        }
    }

    fun addItem(item_name: String, stock: String, unit: String) {
        viewModelScope.launch {
            itemRepository.addItem(item_name, stock, unit)
        }
    }

    fun getItem(itemId: Int) {
        viewModelScope.launch {
            _itemState.value = ItemState.Loading
            try {
                val item = itemRepository.getItem(itemId)
                if (item != null) {
                    _itemState.value = ItemState.Success(item)
                } else {
                    _itemState.value = ItemState.Error("No item found")
                }
            } catch (e: Exception) {
                _itemState.value = ItemState.Error("Failed to get item: ${e.message}")
            }
        }
    }

    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            _deleteState.value = DeleteState.Loading
            try {
                itemRepository.deleteItem(itemId)
                cartRepository.deleteByItemId(itemId)
                historyRepository.clearHistory(itemId)
                _cartItemRefresh.value = true
                _deleteState.value = DeleteState.Success
            } catch (e: Exception) {
                _deleteState.value = DeleteState.Error("Failed to delete item: ${e.message}")
            }
        }
    }

    fun setDeleteStateValue (value: DeleteState) {
        _deleteState.value = value
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            itemRepository.updateItem(item)
        }
    }

    fun clearItems() {
        viewModelScope.launch {
            itemRepository.clearItems()
        }
    }

    // Items state sealed class
    sealed class ItemsState {
        object Idle : ItemsState()
        object Loading : ItemsState()
        data class Success(val items: List<Item>) : ItemsState()
        data class Error(val message: String) : ItemsState()
    }

    sealed class ItemState {
        object Idle : ItemState()
        object Loading : ItemState()
        data class Success(val item: Item) : ItemState()
        data class Error(val message: String) : ItemState()
    }

    sealed class DeleteState {
        object Idle : DeleteState()
        object Loading : DeleteState()
        object Success : DeleteState()
        data class Error(val message: String) : DeleteState()
    }

    private val _cartItems = MutableStateFlow<List<CartItemWithItem>>(emptyList())
    val cartItems: StateFlow<List<CartItemWithItem>> = _cartItems

    private val _cartItemRefresh = MutableStateFlow(true)
    val cartItemRefresh: StateFlow<Boolean> = _cartItemRefresh

    fun setCartItemRefresh(value: Boolean) {
        _cartItemRefresh.value = value
    }

    fun insertCartItem(itemId: Int) {
        viewModelScope.launch {
            val cartItem = cartRepository.getFromItemId(itemId)
            val item = itemRepository.getItem(itemId)
            if (cartItem != null && item != null) {
                if (cartItem.quantity + 1 > item.stock.toInt()) {
                    return@launch
                }
                cartRepository.update(cartItem.copy(quantity = cartItem.quantity + 1))
            } else if (item != null && item.stock.toInt() > 0) {
                cartRepository.insert(itemId, 1)
            }
            _cartItemRefresh.value = true
        }
    }

    fun updateCartItem(cartItem: CartItem) {
        viewModelScope.launch {
            cartRepository.update(cartItem)
            _cartItemRefresh.value = true
        }
    }

    fun deleteCartItem(cartItemId: Int) {
        viewModelScope.launch {
            cartRepository.delete(cartItemId)
            _cartItemRefresh.value = true
        }
    }

    fun getAllCartItems() {
        viewModelScope.launch {
            try {
                val cartItems = cartRepository.getAllWithItems()
                _cartItems.value = cartItems
            } catch (e: Exception) {
                _cartItems.value = emptyList()
            }
        }
    }

    fun checkOutCartItems() {
        viewModelScope.launch {
            try {
                val cartItems = cartRepository.getAllWithItems()
                for (
                    cartItem in cartItems
                ) {
                    val item = cartItem.item
                    itemRepository.updateItem(item.copy(stock = (item.stock.toInt() - cartItem.cartItem.quantity).toString()))
                    historyRepository.addHistory(item.id, cartItem.cartItem.quantity, System.currentTimeMillis().toString())
                    cartRepository.delete(cartItem.cartItem.id)
                }
                _cartItems.value = emptyList()
                _cartItemRefresh.value = true
            } catch (e: Exception) {
            }
        }
    }

    private val _historyItems = MutableStateFlow<List<History>>(emptyList())
    val historyItems: StateFlow<List<History>> = _historyItems

    fun getAllHistoryItems(itemId: Int) {
        viewModelScope.launch {
            try {
                val historyItems = historyRepository.getAllHistoryFromItemId(itemId)
                _historyItems.value = historyItems
            } catch (e: Exception) {
                _historyItems.value = emptyList()
            }
        }
    }
}
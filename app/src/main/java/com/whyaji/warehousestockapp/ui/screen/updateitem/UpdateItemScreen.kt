package com.whyaji.warehousestockapp.ui.screen.updateitem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.whyaji.warehousestockapp.constant.units
import com.whyaji.warehousestockapp.model.Item
import com.whyaji.warehousestockapp.ui.component.DropDown
import com.whyaji.warehousestockapp.ui.component.TextInput
import com.whyaji.warehousestockapp.ui.component.TopAppBar
import com.whyaji.warehousestockapp.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateItemScreen(viewModel: MainViewModel, itemId: Int, backPress: () -> Unit = {}) {
    val focusManager = LocalFocusManager.current
    val itemState = viewModel.itemState.collectAsState().value
    var itemName = remember { mutableStateOf("") }
    var stock = remember { mutableStateOf("") }
    var unit = remember { mutableStateOf("") }

    var itemNameErrorMessage = remember { mutableStateOf("") }
    var stockErrorMessage = remember { mutableStateOf("") }
    var unitErrorMessage = remember { mutableStateOf("") }

    var itemNameFocusRequester = remember { FocusRequester() }
    var stockFocusRequester = remember { FocusRequester() }

    var expanded = remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    // Initialize form with current values when item loads
    LaunchedEffect(itemState) {
        if (itemState is MainViewModel.ItemState.Success) {
            itemName.value = itemState.item.item_name
            stock.value = itemState.item.stock.toString()
            unit.value = itemState.item.unit
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getItem(itemId)
    }

    fun performUpdate() {
        viewModel.updateItem(
            Item(
                id = itemId,
                item_name = itemName.value,
                stock = if (stock.value == "") "0" else stock.value,
                unit = unit.value
            )
        )
        backPress()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Update Item",
                backPress = backPress
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (itemName.value.isEmpty() || stock.value.isEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            snackBarHostState.showSnackbar("Please fill all fields correctly")
                        }
                    } else {
                        performUpdate()
                    }
                },
                icon = @Composable { Icon(Icons.Default.Save, contentDescription = "Save") },
                text = { Text("Save Changes") },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (itemState) {
                is MainViewModel.ItemState.Success -> {
                    TextInput(
                        value = itemName.value,
                        onValueChange = { itemName.value = it },
                        label = @Composable { Text("Item Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(
                                itemNameFocusRequester
                            ),
                        errorMessage = itemNameErrorMessage.value,
                        nextFocusRequester = stockFocusRequester
                    )

                    TextInput(
                        value = stock.value,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                                stock.value = newValue
                            }
                        },
                        label = @Composable { Text("Stock Quantity") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(
                                stockFocusRequester
                            ),
                        keyboardType = KeyboardType.Number,
                        errorMessage = stockErrorMessage.value,
                        isLast = true,
                        performAction = {
                            focusManager.clearFocus()
                        }
                    )

                    DropDown(
                        expanded = expanded.value,
                        onExpandedChange = { expanded.value = it },
                        items = units,
                        selectedItem = unit.value,
                        onItemSelected = { unit.value = it },
                        label = @Composable { Text("Unit") },
                        errorMessage = unitErrorMessage.value
                    )
                }

                is MainViewModel.ItemState.Error -> {
                    Text(
                        text = itemState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                MainViewModel.ItemState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                else -> {}
            }
        }
    }
}
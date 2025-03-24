package com.whyaji.warehousestockapp.ui.screen.additem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.whyaji.warehousestockapp.constant.units
import com.whyaji.warehousestockapp.ui.component.DropDown
import com.whyaji.warehousestockapp.ui.component.TextInput
import com.whyaji.warehousestockapp.ui.component.TopAppBar
import com.whyaji.warehousestockapp.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AddItemScreen(viewModel: MainViewModel, backPress: () -> Unit = {}) {
    val focusManager = LocalFocusManager.current
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

    fun performAdd() {
        viewModel.addItem(
            item_name = itemName.value,
            stock = if (stock.value == "") "0" else stock.value,
            unit = unit.value
        )
        itemName.value = ""
        stock.value = ""
        unit.value = ""
        CoroutineScope(Dispatchers.Main).launch {
            snackBarHostState.showSnackbar("Item added successfully")
        }
        backPress()
    }

    Surface (
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = "Add Item",
                    backPress
                )
            },
            snackbarHost = { SnackbarHost(snackBarHostState) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (itemName.value.isEmpty()) {
                            itemNameErrorMessage.value = "Please fill this field"
                        } else {
                            itemNameErrorMessage.value = ""
                        }

                        if (stock.value.isEmpty()) {
                            stockErrorMessage.value = "Please fill this field"
                        } else {
                            stockErrorMessage.value = ""
                        }

                        if (unit.value.isEmpty()) {
                            unitErrorMessage.value = "Please fill this field"
                        } else {
                            unitErrorMessage.value = ""
                        }

                        if (itemName.value.isEmpty() || stock.value.isEmpty() || unit.value.isEmpty()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                snackBarHostState.showSnackbar("Please fill all fields correctly")
                            }
                        } else {
                            performAdd()
                        }
                    },
                    icon = @Composable { Icon(Icons.Default.Add, contentDescription = "Save") },
                    text = { Text("Add Item") },
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
        }
    }
}
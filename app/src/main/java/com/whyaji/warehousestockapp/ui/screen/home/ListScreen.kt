package com.whyaji.warehousestockapp.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.whyaji.warehousestockapp.model.Item
import com.whyaji.warehousestockapp.viewmodel.MainViewModel

@Composable
fun ListScreen(viewModel: MainViewModel) {
    val itemsState = viewModel.itemsState.collectAsState()
    val logoutState = viewModel.logoutState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getItems()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        when (val state = itemsState.value) {
            is MainViewModel.ItemsState.Success -> {
                LazyColumn {
                    items(state.items) { item ->
                        ListItem(item = item, onDelete = { viewModel.deleteItem(item.id) })
                    }
                }
            }
            is MainViewModel.ItemsState.Error -> {
                Text(text = state.message, color = Color.Red)
            }
            MainViewModel.ItemsState.Loading -> {
                CircularProgressIndicator()
            }
            else -> {}
        }

        Button(onClick = {
            viewModel.logout()
        }) {
            Text("Logout")
        }

        when (logoutState.value) {
            MainViewModel.LogoutState.Loading -> {
                CircularProgressIndicator()
            }
            else -> {}
        }
    }
}

@Composable
fun ListItem(item: Item, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.item_name)
        Button(onClick = onDelete) {
            Text("Delete")
        }
    }
}
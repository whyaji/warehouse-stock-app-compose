package com.whyaji.warehousestockapp.ui.screen.main.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.whyaji.warehousestockapp.model.Item
import com.whyaji.warehousestockapp.ui.component.TextInput
import com.whyaji.warehousestockapp.ui.navigation.DetailScreen
import com.whyaji.warehousestockapp.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(mainViewModel: MainViewModel, navigateTo: (Any) -> Unit) {
    val itemsState = mainViewModel.itemsState.collectAsState()
    val items = mainViewModel.items.collectAsState()
    val searchQuery = mainViewModel.homeSearchQuery.collectAsState().value
    val refreshing = mainViewModel.refreshHome.collectAsState().value
    val setRefreshing = mainViewModel::setRefreshHome

    fun onRefresh() {
        mainViewModel.getAllItems(searchQuery)
    }

    LaunchedEffect(refreshing) {
        if (refreshing) {
            onRefresh()
            setRefreshing(false)
        }
    }

    LaunchedEffect(searchQuery) {
        delay(500)
        onRefresh()
    }

    LaunchedEffect(Unit) {
        if (items.value.isEmpty()) {
            mainViewModel.getItems(searchQuery)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TextInput(
            value = searchQuery,
            onValueChange = { mainViewModel.setHomeSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            isLast = true,
            placeholder = @Composable { Text("Search items...") },
            leadingIcon = @Composable { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = @Composable {
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                mainViewModel.setHomeSearchQuery("")
                            }
                    )
                }
            }
        )

        LazyColumn (
            contentPadding = PaddingValues(bottom = 64.dp)
        ){
            items(items.value) { item ->
                ItemCard(item = item, onClick = {
                    navigateTo(DetailScreen(itemId = item.id))
                }, onAdd = {  })
            }
        }

        when (val state = itemsState.value) {
            is MainViewModel.ItemsState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
            MainViewModel.ItemsState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            else -> {}
        }
    }
}

@Composable
fun ItemCard(item: Item, onClick: () -> Unit, onAdd : () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = item.item_name,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.stock} ${item.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            // Button add to cart
            IconButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}
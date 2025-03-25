package com.whyaji.warehousestockapp.ui.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.whyaji.warehousestockapp.model.History
import com.whyaji.warehousestockapp.ui.component.TopAppBar
import com.whyaji.warehousestockapp.ui.navigation.UpdateItemScreen
import com.whyaji.warehousestockapp.utils.timeMillisToFormattedString
import com.whyaji.warehousestockapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(viewModel: MainViewModel, itemId: Int) {
    val itemState = viewModel.itemState.collectAsState()
    val deleteState = viewModel.deleteState.collectAsState()

    val historyItems = viewModel.historyItems.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getItem(itemId)
        viewModel.getAllHistoryItems(itemId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Detail Item",
                backPress = {
                    viewModel.setBackStack(true)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = itemState.value) {
                is MainViewModel.ItemState.Success -> {
                    // Item Details Card
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Item Name",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = state.item.item_name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Stock",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${state.item.stock} ${state.item.unit}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            var visibleAlertDelete = remember { mutableStateOf(false) }

                            // Delete Button
                            Button(
                                onClick = { visibleAlertDelete.value = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text(text = "Delete", color = MaterialTheme.colorScheme.onError)
                            }

                            if (visibleAlertDelete.value) {
                                AlertDialog(
                                    modifier = Modifier.fillMaxWidth(),
                                    onDismissRequest = {
                                        visibleAlertDelete.value = false
                                    },
                                    title = @Composable { Text("Delete Item") },
                                    confirmButton = @Composable {
                                        Button(
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                            onClick = {
                                                viewModel.deleteItem(itemId)
                                                visibleAlertDelete.value = false
                                                viewModel.setBackStack(true)
                                            }
                                        ) {
                                            Text("Delete")
                                        }
                                    },
                                    dismissButton = @Composable {
                                        OutlinedButton(
                                            onClick = {
                                                visibleAlertDelete.value = false
                                            }
                                        ) {
                                            Text("Cancel")
                                        }
                                    },
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Edit Button
                            Button(
                                onClick = {
                                    viewModel.setNavigateTo(UpdateItemScreen(itemId = itemId))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                            ) {
                                Text(text = "Edit")
                            }

                            when (val state = deleteState.value) {
                                is MainViewModel.DeleteState.Success -> {
                                    viewModel.setDeleteStateValue(
                                        MainViewModel.DeleteState.Idle
                                    )
                                }
                                is MainViewModel.DeleteState.Error -> {
                                    Text(
                                        text = state.message,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                                MainViewModel.DeleteState.Loading -> {
                                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                                }
                                else -> {}
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "History",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // add horizontal line
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            )

                            LazyColumn {
                                items(historyItems.value) { history ->
                                    HistoryCard(history = history, unit = state.item.unit)
                                }
                            }

                            if (historyItems.value.isEmpty()) {
                                Text(
                                    text = "No history",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            }
                        }
                    }
                }
                is MainViewModel.ItemState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                MainViewModel.ItemState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
                else -> {}
            }


        }
    }
}

@Composable
fun HistoryCard(history: History, unit: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = timeMillisToFormattedString(history.date),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Checkout ${history.quantity} $unit",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}
package com.whyaji.warehousestockapp.ui.screen.main.screen.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.whyaji.warehousestockapp.model.CartItem
import com.whyaji.warehousestockapp.model.CartItemWithItem
import com.whyaji.warehousestockapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun CheckoutScreen(viewModel: MainViewModel){
    val cartItems = viewModel.cartItems.collectAsState()
    val scope = rememberCoroutineScope()

    fun updateQuantity(cartItem: CartItem, quantity: Int) {
        if (quantity <= 0) {
            scope.launch {
                viewModel.deleteCartItem(cartItem.id)
            }
        } else {
            scope.launch {
                viewModel.updateCartItem(cartItem.copy(quantity = quantity))
            }
        }
    }

    fun onIncreaseQuantity(cartItem: CartItem) {
        updateQuantity(cartItem, cartItem.quantity + 1)
    }

    fun onDecreaseQuantity(cartItem: CartItem) {
        updateQuantity(cartItem, cartItem.quantity - 1)
    }

    fun onCheckout() {
        scope.launch {
            viewModel.checkOutCartItems()
        }
    }

    var visibleAlertCheckout = remember { mutableStateOf(false) }

    if (visibleAlertCheckout.value) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = {
                visibleAlertCheckout.value = false
            },
            title = @Composable { Text("Checkout") },
            text = @Composable { Text("Are you sure want checkout items?") },
            confirmButton = @Composable {
                Button(
                    onClick = {
                        onCheckout()
                        visibleAlertCheckout.value = false
                    }
                ) {
                    Text("Checkout")
                }
            },
            dismissButton = @Composable {
                OutlinedButton(
                    onClick = {
                        visibleAlertCheckout.value = false
                    }
                ) {
                    Text("Cancel")
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Checkout",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )
        if (cartItems.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Your cart is empty")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(cartItems.value) { cartItemWithItem ->
                    CartItemCard(
                        cartItemWithItem = cartItemWithItem,
                        onIncrease = { onIncreaseQuantity(it) },
                        onDecrease = { onDecreaseQuantity(it) }
                    )
                }
            }

            Button(
                onClick = { visibleAlertCheckout.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp),
                enabled = cartItems.value.isNotEmpty()
            ) {
                Text("Checkout")
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItemWithItem: CartItemWithItem,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit
) {
    val cartItem = cartItemWithItem.cartItem
    val item = cartItemWithItem.item

    ElevatedCard (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.item_name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.stock} ${item.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { onDecrease(cartItem) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }

                Text(
                    text = "${cartItem.quantity}",
                    style = MaterialTheme.typography.bodyLarge
                )

                IconButton(
                    onClick = { onIncrease(cartItem) },
                    modifier = Modifier.size(24.dp),
                    enabled = cartItem.quantity < item.stock.toInt() // Prevent increasing beyond stock
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }
        }
    }
}
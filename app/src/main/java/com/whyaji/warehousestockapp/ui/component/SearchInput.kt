package com.whyaji.warehousestockapp.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchInput(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
) {
    TextInput(
        value = searchQuery,
        onValueChange = { onSearchQueryChange(it) },
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
                            onSearchQueryChange("")
                        }
                )
            }
        }
    )
}
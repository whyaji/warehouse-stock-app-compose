package com.whyaji.warehousestockapp.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String,
    backPress: (() -> Unit)? = null,
) {
    TopAppBar(
        title = @Composable {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = @Composable {
            if (backPress != null) {
                IconButton(onClick = {
                    backPress()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
            } else {
                null
            }
        },
    )
}
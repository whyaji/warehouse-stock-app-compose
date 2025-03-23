package com.whyaji.warehousestockapp.ui.screen.main.screen.additem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AddItemScreen(){
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Add Item Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
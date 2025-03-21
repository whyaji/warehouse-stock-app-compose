package com.whyaji.warehousestockapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    @PrimaryKey
    val id: Int,
    val item_name: String,
    val stock: String,
    val unit: String
)

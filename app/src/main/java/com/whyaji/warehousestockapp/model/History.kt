package com.whyaji.warehousestockapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class History(
    @PrimaryKey
    val id: Int,
    val itemId: Int,
    val quantity: Int,
    val date: String
)
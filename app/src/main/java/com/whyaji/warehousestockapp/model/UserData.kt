package com.whyaji.warehousestockapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserData(
    @PrimaryKey
    val email: String,
    val name: String,
    val department: String,
    val position: String,
    val api_token: String
)


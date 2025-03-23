package com.whyaji.warehousestockapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.whyaji.warehousestockapp.model.UserData

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserData)

    @Query("SELECT * FROM user")
    suspend fun getUser(): UserData

    @Query("DELETE FROM user")
    suspend fun deleteUser()
}
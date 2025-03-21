package com.whyaji.warehousestockapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.whyaji.warehousestockapp.model.Item

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Query("SELECT * FROM item")
    suspend fun getAllItems(): List<Item>

    @Query("DELETE FROM item WHERE id = :itemId")
    suspend fun deleteItem(itemId: Int)

    // update item
    @Query("UPDATE item SET item_name = :item_name, stock = :stock, unit = :unit WHERE id = :itemId")
    suspend fun updateItem(itemId: Int, item_name: String, stock: String, unit: String)
}
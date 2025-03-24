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

    @Query("INSERT INTO item (item_name, stock, unit) VALUES (:item_name, :stock, :unit)")
    suspend fun addItem(item_name: String, stock: String, unit: String)

    @Query("SELECT * FROM item WHERE item_name LIKE '%' || :search || '%'")
    suspend fun getAllItems(search: String): List<Item>

    @Query("SELECT * FROM item WHERE id = :itemId")
    suspend fun getItem(itemId: Int): Item?

    @Query("DELETE FROM item WHERE id = :itemId")
    suspend fun deleteItem(itemId: Int)

    @Query("UPDATE item SET item_name = :item_name, stock = :stock, unit = :unit WHERE id = :itemId")
    suspend fun updateItem(itemId: Int, item_name: String, stock: String, unit: String)

    @Query("DELETE FROM item")
    suspend fun clearItems()
}
package com.whyaji.warehousestockapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.whyaji.warehousestockapp.model.History

@Dao
interface HistoryDao {
    // add history
    @Query("INSERT INTO history (itemId, quantity, date) VALUES (:itemId, :quantity, :date)")
    suspend fun addHistory(itemId: Int, quantity: Int, date: String)

    // get all history based on item id, sort descending by date
    @Query("SELECT * FROM history WHERE itemId = :itemId ORDER BY date DESC")
    suspend fun getHistoryFromItemId(itemId: Int): List<History>
    
    // delete all history based on item id
    @Query("DELETE FROM history WHERE itemId = :itemId")
    suspend fun deleteHistoryFromItemId(itemId: Int)
}
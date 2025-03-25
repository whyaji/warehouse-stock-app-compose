package com.whyaji.warehousestockapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.whyaji.warehousestockapp.model.CartItem
import com.whyaji.warehousestockapp.model.CartItemWithItem

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartItem: CartItem)

    @Query("INSERT INTO cart_item (itemId, quantity) VALUES (:itemId, :quantity)")
    suspend fun addCartItem(itemId: Int, quantity: Int)

    @Update
    suspend fun update(cartItem: CartItem)

    @Transaction
    @Query("SELECT * FROM cart_item")
    suspend fun getAllWithItems(): List<CartItemWithItem>

    @Transaction
    @Query("SELECT * FROM cart_item WHERE id = :cartItemId")
    suspend fun getWithItem(cartItemId: Int): CartItemWithItem?

    @Query("SELECT * FROM cart_item WHERE itemId = :itemId")
    suspend fun getFromItemId(itemId: Int): CartItem?

    @Transaction
    @Query("SELECT * FROM cart_item WHERE id = :itemId")
    suspend fun getWithItemFromItemId(itemId: Int): CartItemWithItem?

    @Query("DELETE FROM cart_item WHERE id = :cartItemId")
    suspend fun deleteByCartItemId(cartItemId: Int)

    @Query("DELETE FROM cart_item WHERE itemId = :itemId")
    suspend fun deleteByItemId(itemId: Int)
}
package com.whyaji.warehousestockapp.data.domain.repository

import com.whyaji.warehousestockapp.data.local.dao.CartDao
import com.whyaji.warehousestockapp.model.CartItem
import com.whyaji.warehousestockapp.model.CartItemWithItem


class CartRepository(
    private val cartDao: CartDao
) {
    suspend fun insert(itemId: Int, quantity: Int) {
        cartDao.addCartItem(itemId, quantity)
    }

    suspend fun update(cartItem: CartItem) {
        cartDao.update(cartItem)
    }

    suspend fun delete(cartItemId: Int) {
        cartDao.deleteByCartItemId(cartItemId)
    }

    suspend fun getAllWithItems(): List<CartItemWithItem> {
        return cartDao.getAllWithItems()
    }

    suspend fun getWithItem(cartItemId: Int): CartItemWithItem? {
        return cartDao.getWithItem(cartItemId)
    }

    suspend fun getFromItemId(itemId: Int): CartItem? {
        return cartDao.getFromItemId(itemId)
    }

    suspend fun getWithItemFromItemId(itemId: Int): CartItemWithItem? {
        return cartDao.getWithItemFromItemId(itemId)
    }
}
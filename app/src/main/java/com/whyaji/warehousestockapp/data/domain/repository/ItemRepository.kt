package com.whyaji.warehousestockapp.data.domain.repository

import com.whyaji.warehousestockapp.data.api.ApiService
import com.whyaji.warehousestockapp.data.local.dao.ItemDao
import com.whyaji.warehousestockapp.model.Item
import com.whyaji.warehousestockapp.model.ItemsResponse
import retrofit2.Response

class ItemRepository(
    private val apiService: ApiService,
    private val itemDao: ItemDao
) {
    suspend fun getItems(): Response<ItemsResponse> {
        return apiService.getItems()
    }

    suspend fun insertItem(item: Item) {
        itemDao.insert(item)
    }

    suspend fun getAllItems(search: String): List<Item> {
        return itemDao.getAllItems(search)
    }

    suspend fun deleteItem(itemId: Int) {
        itemDao.deleteItem(itemId)
    }

    suspend fun updateItem(item: Item) {
        itemDao.updateItem(
            item.id,
            item.item_name,
            item.stock,
            item.unit
        )
    }

    suspend fun clearItems() {
        itemDao.clearItems()
    }
}
package com.whyaji.warehousestockapp.data.domain.repository

import com.whyaji.warehousestockapp.data.api.ApiService
import com.whyaji.warehousestockapp.data.local.dao.ItemDao
import com.whyaji.warehousestockapp.model.Item
import com.whyaji.warehousestockapp.model.ItemsResponse
import com.whyaji.warehousestockapp.model.LoginRequest
import com.whyaji.warehousestockapp.model.LoginResponse
import retrofit2.Response

class Repository(
    private val apiService: ApiService,
    private val itemDao: ItemDao
) {

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }

    suspend fun getItems(): Response<ItemsResponse> {
        return apiService.getItems()
    }

    suspend fun insertItem(item: Item) {
        itemDao.insert(item)
    }

    suspend fun getAllItems(): List<Item> {
        return itemDao.getAllItems()
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
}
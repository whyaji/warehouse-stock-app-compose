package com.whyaji.warehousestockapp.data.domain.repository

import com.whyaji.warehousestockapp.data.local.dao.HistoryDao
import com.whyaji.warehousestockapp.model.History

class HistoryRepository (
    private val historyDao: HistoryDao
) {
    suspend fun addHistory(itemId: Int, quantity: Int, date: String) {
        historyDao.addHistory(itemId, quantity, date)
    }

    suspend fun clearHistory(itemId: Int) {
        historyDao.deleteHistoryFromItemId(itemId)
    }

    suspend fun getAllHistoryFromItemId(itemId: Int): List<History> {
        return historyDao.getHistoryFromItemId(itemId)
    }
}
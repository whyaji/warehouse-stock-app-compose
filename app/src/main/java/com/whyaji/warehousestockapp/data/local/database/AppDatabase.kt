package com.whyaji.warehousestockapp.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.whyaji.warehousestockapp.data.local.dao.CartDao
import com.whyaji.warehousestockapp.data.local.dao.HistoryDao
import com.whyaji.warehousestockapp.data.local.dao.ItemDao
import com.whyaji.warehousestockapp.data.local.dao.UserDao
import com.whyaji.warehousestockapp.model.CartItem
import com.whyaji.warehousestockapp.model.History
import com.whyaji.warehousestockapp.model.Item
import com.whyaji.warehousestockapp.model.UserData

@Database(entities = [Item::class, UserData::class, CartItem::class, History::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
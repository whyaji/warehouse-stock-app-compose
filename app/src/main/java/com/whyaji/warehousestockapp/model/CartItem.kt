package com.whyaji.warehousestockapp.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

data class CartItemWithItem(
    @Embedded
    val cartItem: CartItem,

    @Relation(
        parentColumn = "itemId",
        entityColumn = "id"
    )
    val item: Item
)

@Entity(
    tableName = "cart_item",
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("itemId")]
)
data class CartItem(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "itemId")
    val itemId: Int,
    val quantity: Int
)
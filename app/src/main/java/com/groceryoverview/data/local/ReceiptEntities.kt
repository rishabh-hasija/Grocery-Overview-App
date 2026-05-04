package com.groceryoverview.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipts")
data class ReceiptEntity(
    @PrimaryKey val id: String,
    val storeName: String?,
    val purchaseDateEpochDay: Long,
    val rawText: String,
    val totalAmount: Double?
)

@Entity(tableName = "receipt_items")
data class ReceiptItemEntity(
    @PrimaryKey val id: String,
    val receiptId: String,
    val name: String,
    val quantity: Double,
    val unitPrice: Double?,
    val totalPrice: Double?,
    val category: String
)

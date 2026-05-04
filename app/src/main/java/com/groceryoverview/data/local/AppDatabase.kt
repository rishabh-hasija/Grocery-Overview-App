package com.groceryoverview.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ReceiptEntity::class, ReceiptItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
}

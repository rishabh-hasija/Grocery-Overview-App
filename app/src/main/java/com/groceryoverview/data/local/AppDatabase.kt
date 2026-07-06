package com.groceryoverview.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ReceiptEntity::class, ReceiptItemEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao

    companion object {
        /** v2 adds the unit column ("Stk"/"kg") to receipt items. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE receipt_items ADD COLUMN unit TEXT NOT NULL DEFAULT 'Stk'"
                )
            }
        }
    }
}

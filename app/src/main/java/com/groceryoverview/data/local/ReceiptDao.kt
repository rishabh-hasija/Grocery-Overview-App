package com.groceryoverview.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class ReceiptWithItemsEntity(
    val receipt: ReceiptEntity,
    val items: List<ReceiptItemEntity>
)

@Dao
interface ReceiptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: ReceiptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ReceiptItemEntity>)

    @Transaction
    suspend fun insertReceiptWithItems(receipt: ReceiptEntity, items: List<ReceiptItemEntity>) {
        insertReceipt(receipt)
        insertItems(items)
    }

    @Query("SELECT * FROM receipts ORDER BY purchaseDateEpochDay DESC")
    fun observeReceipts(): Flow<List<ReceiptEntity>>

    @Query("SELECT * FROM receipts WHERE purchaseDateEpochDay BETWEEN :fromEpochDay AND :toEpochDay ORDER BY purchaseDateEpochDay DESC")
    fun observeReceiptsInRange(fromEpochDay: Long, toEpochDay: Long): Flow<List<ReceiptEntity>>

    @Query("SELECT * FROM receipt_items WHERE receiptId = :receiptId")
    suspend fun getItemsForReceipt(receiptId: String): List<ReceiptItemEntity>

    @Query("SELECT * FROM receipts WHERE id = :receiptId LIMIT 1")
    suspend fun getReceiptById(receiptId: String): ReceiptEntity?

    @Query("DELETE FROM receipt_items")
    suspend fun deleteAllItems()

    @Query("DELETE FROM receipts")
    suspend fun deleteAllReceipts()

    @Transaction
    suspend fun deleteAll() {
        deleteAllItems()
        deleteAllReceipts()
    }
}

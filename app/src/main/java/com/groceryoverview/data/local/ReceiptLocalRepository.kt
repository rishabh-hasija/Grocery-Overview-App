package com.groceryoverview.data.local

import com.groceryoverview.domain.Receipt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class ReceiptLocalRepository(
    private val receiptDao: ReceiptDao
) {
    fun observeReceipts(): Flow<List<Receipt>> {
        return receiptDao.observeReceipts().map { entities ->
            entities.map { entity ->
                val items = receiptDao.getItemsForReceipt(entity.id).map { it.toDomain() }
                entity.toDomain(items)
            }
        }
    }

    fun observeReceiptsInRange(fromDate: LocalDate, toDate: LocalDate): Flow<List<Receipt>> {
        return receiptDao.observeReceiptsInRange(fromDate.toEpochDay(), toDate.toEpochDay()).map { entities ->
            entities.map { entity ->
                val items = receiptDao.getItemsForReceipt(entity.id).map { it.toDomain() }
                entity.toDomain(items)
            }
        }
    }

    suspend fun save(receipt: Receipt) {
        receiptDao.insertReceiptWithItems(
            receipt = receipt.toEntity(),
            items = receipt.items.map { it.toEntity() }
        )
    }

    suspend fun clearAll() {
        receiptDao.deleteAll()
    }

    suspend fun getReceiptById(receiptId: String): Receipt? {
        val receipt = receiptDao.getReceiptById(receiptId) ?: return null
        val items = receiptDao.getItemsForReceipt(receiptId).map { it.toDomain() }
        return receipt.toDomain(items)
    }
}

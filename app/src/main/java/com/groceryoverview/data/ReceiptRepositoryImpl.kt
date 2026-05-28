package com.groceryoverview.data

import com.groceryoverview.domain.Receipt
import com.groceryoverview.domain.ReceiptRepository
import java.time.LocalDate

class ReceiptRepositoryImpl : ReceiptRepository {

    private val receipts = mutableListOf<Receipt>()

    override suspend fun saveReceipt(receipt: Receipt) {
        receipts.removeAll { it.id == receipt.id }
        receipts.add(receipt)
    }

    override fun getReceipts(): List<Receipt> {
        return receipts.toList()
    }

    fun getByDateRange(fromDate: LocalDate, toDate: LocalDate): List<Receipt> {
        return receipts.filter { receipt ->
            !receipt.purchaseDate.isBefore(fromDate) &&
            !receipt.purchaseDate.isAfter(toDate)
        }
    }
}

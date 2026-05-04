package com.groceryoverview.data

import com.groceryoverview.domain.Receipt
import java.time.LocalDate

class ReceiptRepository {
    private val receipts = mutableListOf<Receipt>()

    fun save(receipt: Receipt) {
        receipts.removeAll { it.id == receipt.id }
        receipts.add(receipt)
    }

    fun getAll(): List<Receipt> = receipts.toList()

    fun getByDateRange(fromDate: LocalDate, toDate: LocalDate): List<Receipt> {
        return receipts.filter { receipt ->
            !receipt.purchaseDate.isBefore(fromDate) && !receipt.purchaseDate.isAfter(toDate)
        }
    }
}

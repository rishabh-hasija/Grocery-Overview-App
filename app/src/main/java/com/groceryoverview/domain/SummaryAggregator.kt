package com.groceryoverview.domain

import java.time.LocalDate

class SummaryAggregator {

    fun summarize(
        receipts: List<Receipt>,
        fromDate: LocalDate,
        toDate: LocalDate
    ): PurchaseSummary {
        val filteredReceipts = receipts.filter { receipt ->
            !receipt.purchaseDate.isBefore(fromDate) && !receipt.purchaseDate.isAfter(toDate)
        }

        val allItems = filteredReceipts.flatMap { it.items }
        val totalSpent = allItems.sumOf { it.spend() }

        val itemTotals = allItems
            .groupBy { it.name.trim().lowercase() to it.unit }
            .map { (_, items) ->
                val first = items.first()
                ItemTotal(
                    name = first.name.trim(),
                    quantity = items.sumOf { it.quantity },
                    unit = first.unit,
                    totalSpent = items.sumOf { it.spend() },
                    category = first.category
                )
            }
            .sortedByDescending { it.totalSpent }

        val categoryTotals = allItems
            .groupBy { it.category }
            .map { (category, items) ->
                CategoryTotal(
                    category = category,
                    itemCount = items.size,
                    totalQuantity = items.sumOf { it.quantity },
                    totalSpent = items.sumOf { it.spend() }
                )
            }
            .sortedByDescending { it.totalSpent }

        return PurchaseSummary(
            fromDate = fromDate,
            toDate = toDate,
            totalSpent = totalSpent,
            itemTotals = itemTotals,
            categoryTotals = categoryTotals
        )
    }

    private fun ReceiptItem.spend(): Double =
        totalPrice ?: ((unitPrice ?: 0.0) * quantity)
}

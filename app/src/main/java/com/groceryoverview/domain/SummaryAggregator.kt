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
        val totalSpent = allItems.sumOf { it.totalPrice ?: ((it.unitPrice ?: 0.0) * it.quantity) }

        val itemTotals = allItems
            .groupBy { it.name.trim().lowercase() }
            .map { (key, items) ->
                val first = items.first()
                ItemTotal(
                    name = first.name.trim(),
                    quantity = items.sumOf { it.quantity },
                    totalSpent = items.sumOf { it.totalPrice ?: ((it.unitPrice ?: 0.0) * it.quantity) },
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
                    totalSpent = items.sumOf { it.totalPrice ?: ((it.unitPrice ?: 0.0) * it.quantity) }
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
}

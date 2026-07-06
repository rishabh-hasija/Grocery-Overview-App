package com.groceryoverview.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class SummaryAggregatorTest {

    private val aggregator = SummaryAggregator()

    private fun receipt(date: LocalDate, vararg items: Triple<String, Double, ItemCategory>): Receipt {
        val id = "r-$date-${items.size}"
        return Receipt(
            id = id,
            purchaseDate = date,
            rawText = "",
            items = items.map { (name, price, category) ->
                ReceiptItem(
                    receiptId = id,
                    name = name,
                    quantity = 1.0,
                    unitPrice = price,
                    totalPrice = price,
                    category = category
                )
            }
        )
    }

    @Test
    fun `filters receipts by date range`() {
        val receipts = listOf(
            receipt(LocalDate.of(2026, 6, 1), Triple("Milch", 1.0, ItemCategory.Dairy)),
            receipt(LocalDate.of(2026, 6, 15), Triple("Brot", 2.0, ItemCategory.Bakery)),
            receipt(LocalDate.of(2026, 7, 1), Triple("Käse", 3.0, ItemCategory.Dairy))
        )

        val summary = aggregator.summarize(
            receipts,
            fromDate = LocalDate.of(2026, 6, 10),
            toDate = LocalDate.of(2026, 6, 30)
        )

        assertEquals(2.0, summary.totalSpent, 0.001)
        assertEquals(1, summary.itemTotals.size)
        assertEquals("Brot", summary.itemTotals[0].name)
    }

    @Test
    fun `groups repeated items and sums quantity and spend`() {
        val receipts = listOf(
            receipt(
                LocalDate.of(2026, 6, 1),
                Triple("Milch", 0.95, ItemCategory.Dairy),
                Triple("Brot", 1.49, ItemCategory.Bakery)
            ),
            receipt(
                LocalDate.of(2026, 6, 8),
                Triple("MILCH", 0.95, ItemCategory.Dairy)
            )
        )

        val summary = aggregator.summarize(
            receipts,
            fromDate = LocalDate.of(2026, 6, 1),
            toDate = LocalDate.of(2026, 6, 30)
        )

        val milk = summary.itemTotals.first { it.name.equals("milch", ignoreCase = true) }
        assertEquals(2.0, milk.quantity, 0.001)
        assertEquals(1.90, milk.totalSpent, 0.001)
        assertEquals(3.39, summary.totalSpent, 0.001)
    }

    @Test
    fun `weight and piece items are grouped separately`() {
        val id = "r1"
        val receipts = listOf(
            Receipt(
                id = id,
                purchaseDate = LocalDate.of(2026, 6, 1),
                rawText = "",
                items = listOf(
                    ReceiptItem(
                        receiptId = id, name = "Bananen", quantity = 0.75, unit = "kg",
                        unitPrice = 1.63, totalPrice = 1.22, category = ItemCategory.Produce
                    ),
                    ReceiptItem(
                        receiptId = id, name = "Bananen", quantity = 1.0, unit = "Stk",
                        unitPrice = 0.29, totalPrice = 0.29, category = ItemCategory.Produce
                    )
                )
            )
        )

        val summary = aggregator.summarize(
            receipts,
            fromDate = LocalDate.of(2026, 6, 1),
            toDate = LocalDate.of(2026, 6, 30)
        )

        assertEquals(2, summary.itemTotals.size)
        val produce = summary.categoryTotals.first { it.category == ItemCategory.Produce }
        assertEquals(2, produce.itemCount)
        assertEquals(1.51, produce.totalSpent, 0.001)
    }

    @Test
    fun `category totals are sorted by spend descending`() {
        val receipts = listOf(
            receipt(
                LocalDate.of(2026, 6, 5),
                Triple("Milch", 1.0, ItemCategory.Dairy),
                Triple("Steak", 9.0, ItemCategory.Meat),
                Triple("Brot", 2.0, ItemCategory.Bakery)
            )
        )

        val summary = aggregator.summarize(
            receipts,
            fromDate = LocalDate.of(2026, 6, 1),
            toDate = LocalDate.of(2026, 6, 30)
        )

        assertEquals(
            listOf(ItemCategory.Meat, ItemCategory.Bakery, ItemCategory.Dairy),
            summary.categoryTotals.map { it.category }
        )
    }
}

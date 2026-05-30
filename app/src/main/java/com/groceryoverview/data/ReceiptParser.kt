package com.groceryoverview.data

import com.groceryoverview.domain.CategoryClassifier
import com.groceryoverview.domain.Receipt
import com.groceryoverview.domain.ReceiptItem
import java.time.LocalDate
import java.util.UUID

class ReceiptParser(
    private val categoryClassifier: CategoryClassifier = CategoryClassifier()
) {
    fun parse(
        rawText: String,
        purchaseDate: LocalDate,
        storeName: String? = null
    ): Receipt {
        val receiptId = UUID.randomUUID().toString()
        val items = rawText
            .lineSequence()
            .mapNotNull { line -> parseLine(line, receiptId) }
            .toList()

        return Receipt(
            id = receiptId,
            storeName = storeName,
            purchaseDate = purchaseDate,
            rawText = rawText,
            items = items
        )
    }

    private fun parseLine(line: String, receiptId: String): ReceiptItem? {
        val trimmed = line.trim()
        if (trimmed.isBlank()) return null

        val priceMatch = PRICE_REGEX.find(trimmed) ?: return null
        val price = priceMatch.groupValues[1].replace(",", ".").toDoubleOrNull() ?: return null
        val name = trimmed.substring(0, priceMatch.range.first).trim()
        if (name.length < 2) return null

        return ReceiptItem(
            receiptId = receiptId,
            name = name,
            quantity = 1.0,
            unitPrice = price,
            totalPrice = price,
            category = categoryClassifier.classify(name)
        )
    }

    companion object {
        private val PRICE_REGEX = Regex("""(\d+[,.]\d{1,2})\s*[A-Za-z]?\s*$""")
    }
}

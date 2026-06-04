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

        // Find the last valid price on the line — take rightmost to avoid
        // matching quantities or percentages embedded in the product name.
        val priceMatch = PRICE_REGEX.findAll(trimmed)
            .filter { match ->
                // Reject if immediately followed by another digit or separator —
                // that means it's part of a larger number (e.g. date "05.06.2026").
                val next = trimmed.getOrNull(match.range.last + 1)
                next == null || (!next.isDigit() && next != '.' && next != ',')
            }
            .lastOrNull() ?: return null

        val price = priceMatch.groupValues[1].replace(",", ".").toDoubleOrNull() ?: return null
        // Sanity-check: grocery prices are between 0.01 and 9999.99
        if (price <= 0.0 || price > 9999.0) return null

        val name = trimmed.substring(0, priceMatch.range.first).trim()
        if (name.length < 2) return null
        if (isNoiseName(name)) return null

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
        // No end anchor — matches a price-like number (up to 5 integer digits,
        // comma or period separator, 1–2 decimal digits) anywhere on the line.
        private val PRICE_REGEX = Regex("""(\d{1,5}[,.]\d{1,2})""")

        // Names that are structural receipt lines, not products.
        private val NOISE_NAMES = setOf(
            // English
            "total", "subtotal", "tax", "change", "cash", "paid", "payment",
            "discount", "card", "visa", "mastercard", "debit", "credit",
            "balance", "receipt", "sale", "savings", "refund",
            // German
            "summe", "gesamtsumme", "gesamtbetrag", "gesamt", "zwischensumme",
            "mwst", "mehrwertsteuer", "ust", "steuer",
            "rückgeld", "wechselgeld", "gegeben", "betrag", "rabatt",
            "bon", "kasse", "barzahlung", "kartenzahlung", "ec-karte",
            "danke", "tschüss", "auf wiedersehen"
        )

        private fun isNoiseName(name: String): Boolean =
            NOISE_NAMES.contains(name.lowercase().trim())
    }
}

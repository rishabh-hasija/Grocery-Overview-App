package com.groceryoverview.data

import com.groceryoverview.domain.CategoryClassifier
import com.groceryoverview.domain.ItemCategory
import com.groceryoverview.domain.Receipt
import com.groceryoverview.domain.ReceiptItem
import com.groceryoverview.domain.StoreDetector
import java.time.LocalDate
import java.util.UUID

/**
 * Parses OCR text from German (and generic international) grocery receipts.
 *
 * Handles the receipt structures used by REWE, EDEKA, Lidl, ALDI, Kaufland,
 * Netto, PENNY, dm, Rossmann and independent stores:
 *  - item lines with trailing VAT class markers (A/B for supermarkets, 1/2 for dm)
 *  - quantity follow-up lines ("2 x 1,19", "2 Stk x 0,89")
 *  - weight follow-up lines ("0,754 kg x 1,63 EUR/kg")
 *  - inline quantities ("MILCH 2 x 1,19 2,38 A")
 *  - Pfand (bottle deposit) and Leergut (deposit return, negative)
 *  - discounts (Rabatt/Nachlass/Coupon, negative or trailing-minus amounts)
 *  - both German ("1,99") and international ("1.99") decimal formats
 *  - store detection, purchase-date extraction and receipt-total extraction
 */
class ReceiptParser(
    private val categoryClassifier: CategoryClassifier = CategoryClassifier()
) {

    fun parse(
        rawText: String,
        purchaseDate: LocalDate,
        storeName: String? = null
    ): Receipt {
        val receiptId = UUID.randomUUID().toString()
        val items = mutableListOf<ReceiptItem>()
        var extractedTotal: Double? = null

        for (rawLine in rawText.lineSequence()) {
            val line = rawLine.trim().replace(Regex("""\s{2,}"""), " ")
            if (line.isBlank()) continue

            // Stop collecting items once the total line is reached: everything
            // after it is payment info, VAT tables and footer noise.
            if (extractedTotal == null) {
                val total = matchTotal(line)
                if (total != null) {
                    extractedTotal = total
                    continue
                }
            } else {
                continue
            }

            // Quantity / weight follow-up lines modify the previous item.
            val qty = QTY_FOLLOWUP.matchEntire(line)
            if (qty != null && items.isNotEmpty()) {
                val quantity = qty.groupValues[1].toGermanDouble()
                val unitPrice = qty.groupValues[2].toGermanDouble()
                if (quantity != null && unitPrice != null && quantity > 0) {
                    val last = items.removeAt(items.lastIndex)
                    items.add(last.copy(quantity = quantity, unitPrice = unitPrice))
                }
                continue
            }
            val weight = WEIGHT_FOLLOWUP.matchEntire(line)
            if (weight != null && items.isNotEmpty()) {
                val kilos = weight.groupValues[1].toGermanDouble()
                val pricePerKg = weight.groupValues[2].toGermanDouble()
                if (kilos != null && pricePerKg != null && kilos > 0) {
                    val last = items.removeAt(items.lastIndex)
                    items.add(last.copy(quantity = kilos, unit = "kg", unitPrice = pricePerKg))
                }
                continue
            }

            if (isNoiseLine(line)) continue

            parseItemLine(line, receiptId)?.let { items.add(it) }
        }

        val detectedStore = storeName ?: StoreDetector.detect(rawText)
        val detectedDate = extractDate(rawText) ?: purchaseDate

        return Receipt(
            id = receiptId,
            storeName = detectedStore,
            purchaseDate = detectedDate,
            rawText = rawText,
            totalAmount = extractedTotal
                ?: items.sumOf { it.totalPrice ?: 0.0 }.takeIf { items.isNotEmpty() },
            items = items
        )
    }

    private fun parseItemLine(line: String, receiptId: String): ReceiptItem? {
        val match = ITEM_LINE.matchEntire(line) ?: return null
        var name = match.groupValues[1].trim().trimEnd('*', '.', ',', '-', ':')
        val priceText = match.groupValues[2]
        val trailingMinus = match.groupValues[3].isNotBlank()

        var price = priceText.toGermanDouble() ?: return null
        if (trailingMinus && price > 0) price = -price

        if (name.length < 2 || name.none { it.isLetter() }) return null
        if (kotlin.math.abs(price) > 2000.0 || price == 0.0) return null

        val lowerName = name.lowercase()

        val isDeposit = DEPOSIT_NAME.containsMatchIn(lowerName)
        val isDiscount = DISCOUNT_NAME.containsMatchIn(lowerName)

        // A negative amount is only plausible for deposit returns and discounts.
        if (price < 0 && !isDeposit && !isDiscount) return null
        // Structural lines (SUMME, MwSt, ...) are never items.
        if (!isDeposit && !isDiscount && isNoiseName(lowerName)) return null

        // Inline quantity: "MILCH 2 x 1,19 2,38" -> name "MILCH", qty 2, unit 1,19.
        var quantity = 1.0
        var unit = "Stk"
        var unitPrice = price
        val inlineQty = INLINE_QTY.find(name)
        if (inlineQty != null) {
            val q = inlineQty.groupValues[1].toGermanDouble()
            val u = inlineQty.groupValues[2].toGermanDouble()
            if (q != null && u != null && q > 0) {
                quantity = q
                unitPrice = u
                name = name.removeRange(inlineQty.range).trim().trimEnd('*', '.', ',', '-', ':')
                if (name.length < 2) return null
            }
        } else {
            val inlineWeight = INLINE_WEIGHT.find(name)
            if (inlineWeight != null) {
                val kg = inlineWeight.groupValues[1].toGermanDouble()
                val perKg = inlineWeight.groupValues[2].toGermanDouble()
                if (kg != null && perKg != null && kg > 0) {
                    quantity = kg
                    unit = "kg"
                    unitPrice = perKg
                    name = name.removeRange(inlineWeight.range).trim().trimEnd('*', '.', ',', '-', ':')
                    if (name.length < 2) return null
                }
            }
        }

        val category = when {
            isDeposit -> ItemCategory.Deposit
            isDiscount -> ItemCategory.Discount
            else -> categoryClassifier.classify(name)
        }

        return ReceiptItem(
            receiptId = receiptId,
            name = name,
            quantity = quantity,
            unit = unit,
            unitPrice = unitPrice,
            totalPrice = price,
            category = category
        )
    }

    private fun matchTotal(line: String): Double? {
        val match = TOTAL_LINE.find(line) ?: return null
        return match.groupValues[1].toGermanDouble()
    }

    private fun isNoiseLine(line: String): Boolean {
        // Lines with 3+ money-like numbers are VAT summary tables.
        if (PRICE_TOKEN.findAll(line).count() >= 3) return true
        return NOISE_LINE.containsMatchIn(line)
    }

    private fun isNoiseName(lowerName: String): Boolean =
        NOISE_NAMES.any { lowerName == it || lowerName.startsWith("$it ") || lowerName.startsWith("$it:") }

    internal fun extractDate(rawText: String): LocalDate? {
        for (match in DATE_PATTERN.findAll(rawText)) {
            val day = match.groupValues[1].toIntOrNull() ?: continue
            val month = match.groupValues[2].toIntOrNull() ?: continue
            var year = match.groupValues[3].toIntOrNull() ?: continue
            if (year < 100) year += 2000
            if (day !in 1..31 || month !in 1..12 || year !in 2015..2100) continue
            val date = runCatching { LocalDate.of(year, month, day) }.getOrNull() ?: continue
            return date
        }
        // ISO format fallback (some independent stores / printed web receipts).
        ISO_DATE_PATTERN.find(rawText)?.let { m ->
            return runCatching {
                LocalDate.of(m.groupValues[1].toInt(), m.groupValues[2].toInt(), m.groupValues[3].toInt())
            }.getOrNull()
        }
        return null
    }

    companion object {
        private fun String.toGermanDouble(): Double? =
            replace(",", ".").toDoubleOrNull()

        private val PRICE_TOKEN = Regex("""\d{1,4}[,.]\d{2}""")

        /** "NAME  -1,99 A", "NAME 1.99", "NAME 0,95 2", "NAME 0,50- B" */
        private val ITEM_LINE = Regex(
            """^(.{2,}?)\s+(-?\d{1,4}[,.]\d{2})\s*(-?)\s*(?:[AB12]\s*\*?|\*)?$"""
        )

        /** Follow-up: "2 x 1,19", "2 Stk x 0,89", "3 X 0,99 EUR" */
        private val QTY_FOLLOWUP = Regex(
            """(?i)^(\d{1,3}(?:[,.]\d{1,3})?)\s*(?:stk\.?)?\s*[x*]\s*(\d{1,4}[,.]\d{2})\s*(?:eur|€)?$"""
        )

        /** Follow-up: "0,754 kg x 1,63 EUR/kg", "1,102 kg x 4,98 /kg" */
        private val WEIGHT_FOLLOWUP = Regex(
            """(?i)^(\d{1,3}[,.]\d{1,3})\s*kg\s*[x*]?\s*(\d{1,4}[,.]\d{2})\s*(?:eur\s*/?\s*kg|€\s*/?\s*kg|/\s*kg)?$"""
        )

        /** Inline: "MILCH 2 x 1,19" (total price already stripped by ITEM_LINE). */
        private val INLINE_QTY = Regex(
            """(?i)\b(\d{1,3})\s*(?:stk\.?)?\s*[x*]\s*(\d{1,4}[,.]\d{2})\s*$"""
        )
        private val INLINE_WEIGHT = Regex(
            """(?i)\b(\d{1,3}[,.]\d{1,3})\s*kg\s*[x*]\s*(\d{1,4}[,.]\d{2})(?:\s*(?:eur|€)?\s*/?\s*kg)?\s*$"""
        )

        private val TOTAL_LINE = Regex(
            """(?i)^\s*(?:summe|gesamtsumme|gesamtbetrag|zwischensumme|zu\s*zahlen|zahlbetrag|endbetrag|total|gesamt)\b[^0-9-]*(\d{1,5}[,.]\d{2})"""
        )

        private val DATE_PATTERN = Regex("""\b(\d{1,2})\.(\d{1,2})\.(\d{2}|\d{4})\b""")
        private val ISO_DATE_PATTERN = Regex("""\b(\d{4})-(\d{2})-(\d{2})\b""")

        private val DEPOSIT_NAME = Regex("""(?i)\b(pfand|leergut|einweg|mehrweg)\b""")
        private val DISCOUNT_NAME = Regex(
            """(?i)\b(rabatt|nachlass|aktionsnachlass|sofortrabatt|treuerabatt|preisvorteil|coupon|gutschein|discount|angebot)\b"""
        )

        private val NOISE_LINE = Regex(
            """(?i)(""" +
                // company / address / contact header
                """gmbh|co\.\s*kg|\bohg\b|e\.\s*k\.|stra(ß|ss)e\b|\bstr\.|\btel\b|telefon|\bfax\b|www\.|\.de\b|\.com\b|""" +
                """ust[-\s]?id|uid\s*nr|steuer[-\s]?nr|filiale|markt\s*:|kasse\s*:|bed(iener)?\s*[.:]|""" +
                // payment section
                """girocard|kartenzahlung|ec[-\s]karte|kontaktlos|visa|mastercard|maestro|apple\s*pay|google\s*pay|""" +
                """terminal|ta[-\s]?nr|beleg[-\s]?nr|bon[-\s]?nr|trace[-\s]?nr|genehmigung|autorisi|kundenbeleg|""" +
                """\bbar\b|gegeben|r(ü|ue?)ckgeld|wechselgeld|\bcash\b|\bchange\b|\bpaid\b|\bcard\b|""" +
                // VAT / tax rows
                """mwst|mehrwertsteuer|\bust\b|\bnetto\s+\d|\bbrutto\b|steuer\s*%|inkl\.|enth(ä|ae?)lt|""" +
                // fiscal signature (TSE) and footer
                """\btse\b|signatur|seriennummer|transaktion|prozessdaten|zeitformat|kassen[-\s]?id|""" +
                """vielen\s+dank|danke|besuch|wiedersehen|(ö|oe?)ffnungszeit|thank\s+you|receipt\s+no|invoice|""" +
                // misc structural
                """datum\s*[:.]|uhrzeit|\be[-\s]?bon\b|handeingabe|posten\s*:|artikelanzahl|st(ü|ue?)ck\s*:\s*\d+$""" +
                """)"""
        )

        private val NOISE_NAMES = setOf(
            "total", "subtotal", "sub-total", "sub total", "tax", "vat", "gst",
            "change", "cash", "paid", "payment", "balance", "amount due", "receipt",
            "summe", "gesamtsumme", "gesamtbetrag", "gesamt", "zwischensumme",
            "mwst", "mehrwertsteuer", "ust", "steuer", "betrag",
            "rückgeld", "wechselgeld", "gegeben", "bar", "kasse", "bon",
            "barzahlung", "kartenzahlung", "ec-karte"
        )
    }
}

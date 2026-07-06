package com.groceryoverview.data

import com.groceryoverview.domain.ItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Parser tests using realistic OCR text in the receipt formats printed by the
 * major German chains and independent stores.
 */
class ReceiptParserTest {

    private val parser = ReceiptParser()
    private val fallbackDate: LocalDate = LocalDate.of(2026, 1, 1)

    private fun itemSum(receipt: com.groceryoverview.domain.Receipt): Double =
        receipt.items.sumOf { it.totalPrice ?: 0.0 }

    // ── REWE ─────────────────────────────────────────────────────────────

    @Test
    fun `parses REWE receipt with quantity line, Pfand and VAT table`() {
        val text = """
            REWE
            REWE Markt GmbH
            Hauptstr. 12
            10115 Berlin
            Tel.: 030-1234567
            UID Nr.: DE812706034
            AVOCADO VORGEREIFT 1,79 B
            BIO BANANE 1,78 B
            2 Stk x 0,89
            NATURJOGHURT 3,5% 0,89 B
            G&G H-MILCH 1,5% 0,95 B
            RINDERHACK 400G 3,99 B
            PFAND 0,25 A
            SUMME EUR 9,65
            Geg. Mastercard EUR 9,65
            Steuer % Netto Steuer Brutto
            B= 7,0% 8,79 0,62 9,41
            A= 19,0% 0,20 0,04 0,24
            Gesamtbetrag 8,99 0,66 9,65
            15.06.2026 18:32 Bon-Nr.:4711
            Markt:0815 Kasse:3 Bed.:222222
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)

        assertEquals("REWE", receipt.storeName)
        assertEquals(LocalDate.of(2026, 6, 15), receipt.purchaseDate)
        assertEquals(9.65, receipt.totalAmount!!, 0.001)
        assertEquals(6, receipt.items.size)

        val banana = receipt.items.first { it.name.contains("BANANE") }
        assertEquals(2.0, banana.quantity, 0.001)
        assertEquals(0.89, banana.unitPrice!!, 0.001)
        assertEquals(1.78, banana.totalPrice!!, 0.001)
        assertEquals(ItemCategory.Produce, banana.category)

        val pfand = receipt.items.first { it.name.contains("PFAND") }
        assertEquals(ItemCategory.Deposit, pfand.category)

        assertEquals(9.65, itemSum(receipt), 0.001)
    }

    // ── Lidl ─────────────────────────────────────────────────────────────

    @Test
    fun `parses Lidl receipt with quantity line and zu zahlen total`() {
        val text = """
            Lidl sagt Danke
            Lidl Vertriebs GmbH & Co. KG
            Bahnhofstr. 5 * 80331 München
            Vollmilch 3,5% 2,38 A
            2 x 1,19
            Bio-Eier 10er 2,89 A
            Körniger Frischkäse 0,99 A
            Roggenbrot 500g 1,49 B
            Äpfel rot 1kg 2,19 A
            zu zahlen 9,94
            Kartenzahlung 9,94
            girocard
            Datum: 22.06.2026 Uhrzeit: 17:05:33 Uhr
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)

        assertEquals("Lidl", receipt.storeName)
        assertEquals(LocalDate.of(2026, 6, 22), receipt.purchaseDate)
        assertEquals(9.94, receipt.totalAmount!!, 0.001)
        assertEquals(5, receipt.items.size)

        val milk = receipt.items.first { it.name.startsWith("Vollmilch") }
        assertEquals(2.0, milk.quantity, 0.001)
        assertEquals(1.19, milk.unitPrice!!, 0.001)
        assertEquals(ItemCategory.Dairy, milk.category)

        assertEquals(9.94, itemSum(receipt), 0.001)
    }

    // ── ALDI SÜD ─────────────────────────────────────────────────────────

    @Test
    fun `parses Aldi receipt with weight-priced produce`() {
        val text = """
            ALDI SÜD
            Filiale 123
            BUTTER DEUTSCHE MARKENBUTTER 1,99 B
            BANANEN 1,23 B
            0,754 kg x 1,63 EUR/kg
            MILCH 1,5% 1L 0,95 B
            GURKEN 0,79 B
            SUMME 4,96 €
            BAR 5,00
            RÜCKGELD 0,04
            04.07.2026 11:22
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)

        assertEquals("ALDI SÜD", receipt.storeName)
        assertEquals(LocalDate.of(2026, 7, 4), receipt.purchaseDate)
        assertEquals(4.96, receipt.totalAmount!!, 0.001)
        assertEquals(4, receipt.items.size)

        val bananas = receipt.items.first { it.name == "BANANEN" }
        assertEquals("kg", bananas.unit)
        assertEquals(0.754, bananas.quantity, 0.001)
        assertEquals(1.63, bananas.unitPrice!!, 0.001)
        assertEquals(1.23, bananas.totalPrice!!, 0.001)

        assertEquals(4.96, itemSum(receipt), 0.001)
    }

    // ── EDEKA ────────────────────────────────────────────────────────────

    @Test
    fun `parses EDEKA receipt with discount line`() {
        val text = """
            EDEKA Neukauf
            GUT&GÜNSTIG BASMATI REIS 2,49 B
            PANEER 200G 3,29 B
            RABATT -0,50
            JOGHURT NATUR 0,79 B
            SUMME 6,07
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)

        assertEquals("EDEKA", receipt.storeName)
        assertEquals(6.07, receipt.totalAmount!!, 0.001)
        assertEquals(4, receipt.items.size)

        val discount = receipt.items.first { it.category == ItemCategory.Discount }
        assertEquals(-0.50, discount.totalPrice!!, 0.001)

        val paneer = receipt.items.first { it.name.contains("PANEER") }
        assertEquals(ItemCategory.Dairy, paneer.category)
        val rice = receipt.items.first { it.name.contains("REIS") }
        assertEquals(ItemCategory.Pantry, rice.category)

        assertEquals(6.07, itemSum(receipt), 0.001)
    }

    // ── dm (digit VAT markers) ───────────────────────────────────────────

    @Test
    fun `parses dm receipt with numeric VAT class markers`() {
        val text = """
            dm-drogerie markt GmbH + Co. KG
            Balea Duschgel 0,95 2
            Alverde Zahncreme 1,45 2
            Babylove Windeln Gr.4 4,95 2
            SUMME EUR 7,35
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)

        assertEquals("dm", receipt.storeName)
        assertEquals(7.35, receipt.totalAmount!!, 0.001)
        assertEquals(3, receipt.items.size)
        assertEquals(
            ItemCategory.PersonalCare,
            receipt.items.first { it.name.contains("Duschgel") }.category
        )
        assertEquals(
            ItemCategory.Baby,
            receipt.items.first { it.name.contains("Windeln") }.category
        )
        assertEquals(7.35, itemSum(receipt), 0.001)
    }

    // ── Netto ────────────────────────────────────────────────────────────

    @Test
    fun `parses Netto receipt with Pfand and percentage discount`() {
        val text = """
            Netto Marken-Discount
            STICKS LAKRITZ 1,19 A
            BIO APFELMUS 1,49 B
            ENERGY DRINK 0,55 A
            PFAND 0,25 A
            Rabatt 5% -0,17
            SUMME 3,31
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)

        assertEquals("Netto", receipt.storeName)
        assertEquals(3.31, receipt.totalAmount!!, 0.001)
        assertEquals(5, receipt.items.size)
        assertEquals(3.31, itemSum(receipt), 0.001)

        val drink = receipt.items.first { it.name == "ENERGY DRINK" }
        assertEquals(ItemCategory.Beverages, drink.category)
    }

    // ── Kaufland ─────────────────────────────────────────────────────────

    @Test
    fun `parses Kaufland receipt with weight line and Zwischensumme`() {
        val text = """
            KAUFLAND
            Bergstr. 1
            K-CLASSIC TOASTBROT 0,89 A
            HÄHNCHENBRUSTFILET 5,49 A
            1,102 kg x 4,98 /kg
            KATZENFUTTER 3,29 A
            Zwischensumme 9,67
            SUMME 9,67
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)

        assertEquals("Kaufland", receipt.storeName)
        assertEquals(9.67, receipt.totalAmount!!, 0.001)
        assertEquals(3, receipt.items.size)

        val chicken = receipt.items.first { it.name.contains("HÄHNCHEN") }
        assertEquals("kg", chicken.unit)
        assertEquals(1.102, chicken.quantity, 0.001)
        assertEquals(ItemCategory.Meat, chicken.category)
        assertEquals(
            ItemCategory.Pet,
            receipt.items.first { it.name == "KATZENFUTTER" }.category
        )
    }

    // ── PENNY ────────────────────────────────────────────────────────────

    @Test
    fun `parses Penny receipt`() {
        val text = """
            PENNY Markt GmbH
            BROT WEIZEN 1,29 B
            SALAMI 1,99 B
            CHIO CHIPS 1,79 A
            SUMME 5,07
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)

        assertEquals("PENNY", receipt.storeName)
        assertEquals(3, receipt.items.size)
        assertEquals(5.07, itemSum(receipt), 0.001)
        assertEquals(ItemCategory.Bakery, receipt.items[0].category)
        assertEquals(ItemCategory.Meat, receipt.items[1].category)
        assertEquals(ItemCategory.Snacks, receipt.items[2].category)
    }

    // ── Rossmann ─────────────────────────────────────────────────────────

    @Test
    fun `parses Rossmann receipt`() {
        val text = """
            ROSSMANN
            Mein Drogeriemarkt
            ISANA SHAMPOO 0,85 1
            DOMOL WASCHMITTEL 3,95 1
            SUMME 4,80
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)

        assertEquals("ROSSMANN", receipt.storeName)
        assertEquals(2, receipt.items.size)
        assertEquals(ItemCategory.PersonalCare, receipt.items[0].category)
        assertEquals(ItemCategory.Household, receipt.items[1].category)
        assertEquals(4.80, receipt.totalAmount!!, 0.001)
    }

    // ── Independent Indian grocery store (English format) ────────────────

    @Test
    fun `parses independent Indian store receipt with period decimals`() {
        val text = """
            INDIA BAZAAR
            Frankfurter Str. 10
            60311 Frankfurt
            AASHIRVAAD ATTA 5KG 8.99
            TOOR DAL 1KG 3.49
            GARAM MASALA 100G 1.99
            PANEER FRESH 400G 4.50
            BASMATI RICE 5KG 12.99
            TOTAL 31.96
            CASH 35.00
            CHANGE 3.04
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)

        assertEquals("INDIA BAZAAR", receipt.storeName)
        // No date on receipt -> falls back to provided date.
        assertEquals(fallbackDate, receipt.purchaseDate)
        assertEquals(31.96, receipt.totalAmount!!, 0.001)
        assertEquals(5, receipt.items.size)
        assertEquals(31.96, itemSum(receipt), 0.001)

        assertEquals(
            ItemCategory.Pantry,
            receipt.items.first { it.name.contains("ATTA") }.category
        )
        assertEquals(
            ItemCategory.Pantry,
            receipt.items.first { it.name.contains("DAL") }.category
        )
        assertEquals(
            ItemCategory.Dairy,
            receipt.items.first { it.name.contains("PANEER") }.category
        )
    }

    // ── Structural behaviours ────────────────────────────────────────────

    @Test
    fun `parses inline quantity on a single line`() {
        val text = """
            MILCH 2 x 1,19 2,38 A
            SUMME 2,38
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)

        assertEquals(1, receipt.items.size)
        val milk = receipt.items[0]
        assertEquals("MILCH", milk.name)
        assertEquals(2.0, milk.quantity, 0.001)
        assertEquals(1.19, milk.unitPrice!!, 0.001)
        assertEquals(2.38, milk.totalPrice!!, 0.001)
    }

    @Test
    fun `handles trailing minus discount notation`() {
        val text = """
            EDEKA
            KAFFEE 4,99 B
            AKTIONSNACHLASS 1,00- B
            SUMME 3,99
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)

        assertEquals(2, receipt.items.size)
        val discount = receipt.items.first { it.category == ItemCategory.Discount }
        assertEquals(-1.00, discount.totalPrice!!, 0.001)
        assertEquals(3.99, itemSum(receipt), 0.001)
    }

    @Test
    fun `ignores negative amounts on non-discount lines`() {
        val text = """
            SOMETHING -3,99
            MILCH 0,95 B
            SUMME 0,95
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)
        assertEquals(1, receipt.items.size)
        assertEquals("MILCH", receipt.items[0].name)
    }

    @Test
    fun `stops parsing items after the total line`() {
        val text = """
            MILCH 0,95 B
            SUMME 0,95
            GEBÜHR 2,50
            IRGENDWAS 9,99
        """.trimIndent()

        val receipt = parser.parse(text, fallbackDate)
        assertEquals(1, receipt.items.size)
    }

    @Test
    fun `returns empty items for unreadable text`() {
        val receipt = parser.parse("....\n???\n123", fallbackDate)
        assertTrue(receipt.items.isEmpty())
    }

    @Test
    fun `extracts two-digit year dates`() {
        val date = parser.extractDate("Datum 05.03.26 Uhrzeit 10:11")
        assertNotNull(date)
        assertEquals(LocalDate.of(2026, 3, 5), date)
    }

    @Test
    fun `explicit store name overrides detection`() {
        val receipt = parser.parse("MILCH 0,95 B", fallbackDate, storeName = "My Shop")
        assertEquals("My Shop", receipt.storeName)
    }
}

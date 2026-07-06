package com.groceryoverview.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StoreDetectorTest {

    @Test
    fun `detects major German chains`() {
        assertEquals("REWE", StoreDetector.detect("REWE\nREWE Markt GmbH\nDanke"))
        assertEquals("EDEKA", StoreDetector.detect("EDEKA Neukauf Musterstadt"))
        assertEquals("Lidl", StoreDetector.detect("Lidl sagt Danke"))
        assertEquals("ALDI SÜD", StoreDetector.detect("ALDI SÜD Filiale 12"))
        assertEquals("ALDI SÜD", StoreDetector.detect("ALDI SUED Filiale 12"))
        assertEquals("ALDI Nord", StoreDetector.detect("ALDI NORD"))
        assertEquals("Kaufland", StoreDetector.detect("KAUFLAND Bergstr. 1"))
        assertEquals("Netto", StoreDetector.detect("Netto Marken-Discount AG"))
        assertEquals("PENNY", StoreDetector.detect("PENNY Markt GmbH"))
        assertEquals("dm", StoreDetector.detect("dm-drogerie markt GmbH + Co. KG"))
        assertEquals("ROSSMANN", StoreDetector.detect("ROSSMANN\nMein Drogeriemarkt"))
    }

    @Test
    fun `falls back to header line for independent stores`() {
        assertEquals(
            "INDIA BAZAAR",
            StoreDetector.detect("INDIA BAZAAR\nFrankfurter Str. 10\n60311 Frankfurt")
        )
    }

    @Test
    fun `returns null when nothing usable found`() {
        assertNull(StoreDetector.detect("12345\n???\n!!"))
    }
}

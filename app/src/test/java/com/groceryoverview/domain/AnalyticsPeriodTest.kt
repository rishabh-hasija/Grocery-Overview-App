package com.groceryoverview.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class AnalyticsPeriodTest {

    private val today: LocalDate = LocalDate.of(2026, 7, 4)

    @Test
    fun `week covers the last 7 days inclusive`() {
        val (from, to) = AnalyticsPeriod.WEEK.range(today)!!
        assertEquals(LocalDate.of(2026, 6, 28), from)
        assertEquals(today, to)
    }

    @Test
    fun `month covers the last month`() {
        val (from, to) = AnalyticsPeriod.MONTH.range(today)!!
        assertEquals(LocalDate.of(2026, 6, 5), from)
        assertEquals(today, to)
    }

    @Test
    fun `year covers the last year`() {
        val (from, to) = AnalyticsPeriod.YEAR.range(today)!!
        assertEquals(LocalDate.of(2025, 7, 5), from)
        assertEquals(today, to)
    }

    @Test
    fun `custom has no predefined range`() {
        assertNull(AnalyticsPeriod.CUSTOM.range(today))
    }

    @Test
    fun `clamp swaps inverted ranges`() {
        val (from, to) = AnalyticsPeriod.clampCustomRange(
            LocalDate.of(2026, 7, 1),
            LocalDate.of(2026, 6, 1)
        )
        assertEquals(LocalDate.of(2026, 6, 1), from)
        assertEquals(LocalDate.of(2026, 7, 1), to)
    }

    @Test
    fun `clamp caps ranges at one year`() {
        val (from, to) = AnalyticsPeriod.clampCustomRange(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2026, 7, 4)
        )
        assertEquals(LocalDate.of(2026, 7, 4), to)
        assertEquals(to.minusDays(AnalyticsPeriod.MAX_RANGE_DAYS), from)
    }
}

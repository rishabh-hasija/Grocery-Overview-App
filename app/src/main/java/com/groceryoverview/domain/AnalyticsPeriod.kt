package com.groceryoverview.domain

import java.time.LocalDate

/**
 * Quick date-range filters for the analytics screen.
 * Custom ranges are capped at one year ([MAX_RANGE_DAYS]).
 */
enum class AnalyticsPeriod(val label: String) {
    WEEK("Week"),
    MONTH("Month"),
    THREE_MONTHS("3 Months"),
    SIX_MONTHS("6 Months"),
    YEAR("Year"),
    CUSTOM("Custom");

    /** Inclusive date range ending today. CUSTOM returns null (caller-defined). */
    fun range(today: LocalDate = LocalDate.now()): Pair<LocalDate, LocalDate>? = when (this) {
        WEEK -> today.minusDays(6) to today
        MONTH -> today.minusMonths(1).plusDays(1) to today
        THREE_MONTHS -> today.minusMonths(3).plusDays(1) to today
        SIX_MONTHS -> today.minusMonths(6).plusDays(1) to today
        YEAR -> today.minusYears(1).plusDays(1) to today
        CUSTOM -> null
    }

    companion object {
        const val MAX_RANGE_DAYS = 366L

        /** Clamps a custom range: from <= to, and span at most one year. */
        fun clampCustomRange(from: LocalDate, to: LocalDate): Pair<LocalDate, LocalDate> {
            val (lo, hi) = if (from.isAfter(to)) to to from else from to to
            val clampedLo = if (hi.toEpochDay() - lo.toEpochDay() > MAX_RANGE_DAYS) {
                hi.minusDays(MAX_RANGE_DAYS)
            } else lo
            return clampedLo to hi
        }
    }
}

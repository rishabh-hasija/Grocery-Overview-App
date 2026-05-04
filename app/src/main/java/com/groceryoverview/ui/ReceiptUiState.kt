package com.groceryoverview.ui

import com.groceryoverview.domain.PurchaseSummary
import com.groceryoverview.domain.Receipt
import java.time.LocalDate

data class ReceiptUiState(
    val isScanning: Boolean = false,
    val scanText: String = "",
    val receipts: List<Receipt> = emptyList(),
    val selectedFromDate: LocalDate = LocalDate.now().minusMonths(1),
    val selectedToDate: LocalDate = LocalDate.now(),
    val summary: PurchaseSummary? = null,
    val errorMessage: String? = null
)

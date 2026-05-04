package com.groceryoverview.domain

data class ReceiptSummary(
    val receiptCount: Int,
    val itemCount: Int,
    val totalSpent: Double
)

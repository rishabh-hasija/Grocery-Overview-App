package com.groceryoverview.domain

import java.time.LocalDate
import java.util.UUID

data class Receipt(
    val id: String = UUID.randomUUID().toString(),
    val storeName: String? = null,
    val purchaseDate: LocalDate,
    val rawText: String,
    val totalAmount: Double? = null,
    val items: List<ReceiptItem> = emptyList()
)

data class ReceiptItem(
    val id: String = UUID.randomUUID().toString(),
    val receiptId: String,
    val name: String,
    val quantity: Double = 1.0,
    val unitPrice: Double? = null,
    val totalPrice: Double? = null,
    val category: ItemCategory = ItemCategory.Unknown
)

enum class ItemCategory {
    Produce,
    Dairy,
    Bakery,
    Meat,
    Frozen,
    Pantry,
    Beverages,
    Snacks,
    Household,
    PersonalCare,
    Baby,
    Pet,
    Unknown
}

data class PurchaseSummary(
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val totalSpent: Double,
    val itemTotals: List<ItemTotal>,
    val categoryTotals: List<CategoryTotal>
)

data class ItemTotal(
    val name: String,
    val quantity: Double,
    val totalSpent: Double,
    val category: ItemCategory
)

data class CategoryTotal(
    val category: ItemCategory,
    val itemCount: Int,
    val totalSpent: Double
)

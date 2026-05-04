package com.groceryoverview.data.local

import com.groceryoverview.domain.ItemCategory
import com.groceryoverview.domain.Receipt
import com.groceryoverview.domain.ReceiptItem
import java.time.LocalDate

fun Receipt.toEntity(): ReceiptEntity {
    return ReceiptEntity(
        id = id,
        storeName = storeName,
        purchaseDateEpochDay = purchaseDate.toEpochDay(),
        rawText = rawText,
        totalAmount = totalAmount
    )
}

fun ReceiptItem.toEntity(): ReceiptItemEntity {
    return ReceiptItemEntity(
        id = id,
        receiptId = receiptId,
        name = name,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice,
        category = category.name
    )
}

fun ReceiptEntity.toDomain(items: List<ReceiptItem>): Receipt {
    return Receipt(
        id = id,
        storeName = storeName,
        purchaseDate = LocalDate.ofEpochDay(purchaseDateEpochDay),
        rawText = rawText,
        totalAmount = totalAmount,
        items = items
    )
}

fun ReceiptItemEntity.toDomain(): ReceiptItem {
    return ReceiptItem(
        id = id,
        receiptId = receiptId,
        name = name,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice,
        category = runCatching { ItemCategory.valueOf(category) }.getOrDefault(ItemCategory.Unknown)
    )
}

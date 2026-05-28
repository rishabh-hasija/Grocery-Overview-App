package com.groceryoverview.domain

interface ReceiptRepository {
    suspend fun saveReceipt(receipt: Receipt)

    fun getReceipts(): List<Receipt>
}

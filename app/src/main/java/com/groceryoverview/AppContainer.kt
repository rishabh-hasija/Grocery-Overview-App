package com.groceryoverview

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.groceryoverview.data.ReceiptCaptureProcessor
import com.groceryoverview.data.ReceiptParser
import com.groceryoverview.data.local.AppDatabase
import com.groceryoverview.data.local.ReceiptLocalRepository
import com.groceryoverview.data.ocr.MlKitReceiptTextExtractor
import com.groceryoverview.ui.ReceiptViewModel

class AppContainer(context: Context) {
    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "grocery-overview.db"
    )
        .addMigrations(AppDatabase.MIGRATION_1_2)
        .build()

    private val receiptRepository = ReceiptLocalRepository(database.receiptDao())

    val receiptViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReceiptViewModel::class.java)) {
                return ReceiptViewModel(
                    repository = receiptRepository,
                    processor = ReceiptCaptureProcessor(
                        receiptParser = ReceiptParser(),
                        textExtractor = MlKitReceiptTextExtractor()
                    )
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

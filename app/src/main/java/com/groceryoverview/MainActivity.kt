package com.groceryoverview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.groceryoverview.ui.GroceryOverviewApp
import com.groceryoverview.ui.ReceiptViewModel

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer
    private lateinit var receiptViewModel: ReceiptViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(applicationContext)
        receiptViewModel = ViewModelProvider(this, appContainer.receiptViewModelFactory)[ReceiptViewModel::class.java]
        setContent {
            GroceryOverviewApp(viewModel = receiptViewModel)
        }
    }
}

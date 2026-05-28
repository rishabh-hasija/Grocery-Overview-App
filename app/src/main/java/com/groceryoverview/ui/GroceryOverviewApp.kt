package com.groceryoverview.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.groceryoverview.ui.screens.HomeScreen
import com.groceryoverview.ui.screens.ScanReceiptScreen
import com.groceryoverview.ui.screens.SummaryScreen

@Composable
fun GroceryOverviewApp(
    viewModel: ReceiptViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var screen by remember { mutableStateOf("home") }

    MaterialTheme {
        when (screen) {
            "scan" -> ScanReceiptScreen(
                onImageCaptured = { imageProxy ->
                    viewModel.processScan(imageProxy, java.time.LocalDate.now())
                    screen = "home"
                },
                onStopScan = { screen = "home" }
            )
            "summary" -> SummaryScreen(summary = uiState.summary)
            else -> HomeScreen(
                receiptCount = uiState.receipts.size,
                onScanClick = { screen = "scan" },
                onSummaryClick = { screen = "summary" }
            )
        }
    }
}

package com.groceryoverview.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.groceryoverview.BuildConfig
import com.groceryoverview.ui.screens.HomeScreen
import com.groceryoverview.ui.screens.ScanReceiptScreen
import com.groceryoverview.ui.screens.SummaryScreen
import com.groceryoverview.update.AppUpdateManager
import com.groceryoverview.update.UpdateCheckResult
import kotlinx.coroutines.launch

@Composable
fun GroceryOverviewApp(
    viewModel: ReceiptViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var screen by remember { mutableStateOf("home") }
    val context = LocalContext.current
    val updateManager = remember(context) { AppUpdateManager(context) }
    val scope = rememberCoroutineScope()
    var updateMessage by remember { mutableStateOf<String?>(null) }

    MaterialTheme {
        when (screen) {
            "scan" -> ScanReceiptScreen(
                onImageCaptured = { imageProxy ->
                    viewModel.processScan(imageProxy, java.time.LocalDate.now())
                    screen = "home"
                },
                onCaptureError = { errorMsg ->
                    viewModel.setErrorMessage(errorMsg)
                    screen = "home"
                },
                onStopScan = { screen = "home" }
            )
            "summary" -> SummaryScreen(
                summary = uiState.summary,
                onBack = { screen = "home" },
                onClearAll = { viewModel.clearAll() }
            )
            else -> HomeScreen(
                receiptCount = uiState.receipts.size,
                appVersion = BuildConfig.VERSION_NAME,
                updateStatus = updateMessage,
                isScanning = uiState.isScanning,
                scanText = uiState.scanText,
                errorMessage = uiState.errorMessage,
                onScanClick = {
                    viewModel.clearScanMessage()
                    screen = "scan"
                },
                onSummaryClick = { screen = "summary" },
                onUpdateClick = {
                    scope.launch {
                        updateMessage = "Checking for updates..."
                        when (val result = updateManager.checkForUpdate()) {
                            UpdateCheckResult.UpToDate -> {
                                updateMessage = "You are already on the latest version."
                            }
                            is UpdateCheckResult.UpdateAvailable -> {
                                updateMessage = "Downloading ${result.info.versionName}..."
                                val installResult = updateManager.downloadAndInstall(result.info)
                                updateMessage = installResult.fold(
                                    onSuccess = { "Update installer opened. Finish the install prompt to complete the update." },
                                    onFailure = { it.message ?: "Unable to start the update installer." }
                                )
                            }
                            is UpdateCheckResult.Unavailable -> {
                                updateMessage = result.message
                            }
                        }
                    }
                }
            )
        }
    }
}

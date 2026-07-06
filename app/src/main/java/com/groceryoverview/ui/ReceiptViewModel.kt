package com.groceryoverview.ui

import androidx.camera.core.ImageProxy
import androidx.camera.core.ExperimentalGetImage
import kotlin.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryoverview.data.ReceiptCaptureProcessor
import com.groceryoverview.data.ReceiptParser
import com.groceryoverview.data.local.ReceiptLocalRepository
import com.groceryoverview.data.ocr.MlKitReceiptTextExtractor
import com.groceryoverview.domain.AnalyticsPeriod
import com.groceryoverview.domain.SummaryAggregator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ReceiptViewModel(
    private val repository: ReceiptLocalRepository,
    private val summaryAggregator: SummaryAggregator = SummaryAggregator(),
    private val processor: ReceiptCaptureProcessor = ReceiptCaptureProcessor(
        receiptParser = ReceiptParser(),
        textExtractor = MlKitReceiptTextExtractor()
    )
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReceiptUiState())
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeReceipts().collectLatest { receipts ->
                _uiState.update { current ->
                    current.copy(
                        receipts = receipts,
                        summary = summaryAggregator.summarize(
                            receipts = receipts,
                            fromDate = current.selectedFromDate,
                            toDate = current.selectedToDate
                        )
                    )
                }
            }
        }
    }

    /** Selects a quick period filter (Week / Month / 3M / 6M / Year). */
    fun selectPeriod(period: AnalyticsPeriod) {
        val range = period.range() ?: return
        _uiState.update { current ->
            current.copy(
                selectedPeriod = period,
                selectedFromDate = range.first,
                selectedToDate = range.second,
                summary = summaryAggregator.summarize(
                    receipts = current.receipts,
                    fromDate = range.first,
                    toDate = range.second
                )
            )
        }
    }

    /** Sets a custom date range, clamped to at most one year. */
    fun setCustomRange(fromDate: LocalDate, toDate: LocalDate) {
        val (from, to) = AnalyticsPeriod.clampCustomRange(fromDate, toDate)
        _uiState.update { current ->
            current.copy(
                selectedPeriod = AnalyticsPeriod.CUSTOM,
                selectedFromDate = from,
                selectedToDate = to,
                summary = summaryAggregator.summarize(
                    receipts = current.receipts,
                    fromDate = from,
                    toDate = to
                )
            )
        }
    }

    @Deprecated("Use selectPeriod/setCustomRange", ReplaceWith("setCustomRange(fromDate, toDate)"))
    fun updateDateRange(fromDate: LocalDate, toDate: LocalDate) = setCustomRange(fromDate, toDate)

    fun clearAll() {
        viewModelScope.launch { repository.clearAll() }
    }

    fun clearScanMessage() {
        _uiState.update { it.copy(scanText = "", errorMessage = null) }
    }

    fun setErrorMessage(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    @OptIn(ExperimentalGetImage::class)
    fun processScan(imageProxy: ImageProxy, purchaseDate: LocalDate, storeName: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, errorMessage = null) }
            runCatching {
                val receipt = processor.process(imageProxy, purchaseDate, storeName)
                if (receipt.items.isEmpty()) {
                    error("No items found. Ensure the receipt is well-lit and prices are clearly visible, then try again.")
                }
                repository.save(receipt)
            }.onSuccess {
                _uiState.update { it.copy(isScanning = false, scanText = "Receipt saved locally.") }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isScanning = false,
                        errorMessage = throwable.message ?: "Failed to process receipt."
                    )
                }
            }.also {
                imageProxy.close()
            }
        }
    }
}

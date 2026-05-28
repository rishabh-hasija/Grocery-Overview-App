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

    fun updateDateRange(fromDate: LocalDate, toDate: LocalDate) {
        _uiState.update { current ->
            current.copy(
                selectedFromDate = fromDate,
                selectedToDate = toDate,
                summary = summaryAggregator.summarize(
                    receipts = current.receipts,
                    fromDate = fromDate,
                    toDate = toDate
                )
            )
        }
    }

    @OptIn(ExperimentalGetImage::class)
    fun processScan(imageProxy: ImageProxy, purchaseDate: LocalDate, storeName: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, errorMessage = null) }
            runCatching {
                val receipt = processor.process(imageProxy, purchaseDate, storeName)
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

package com.groceryoverview.data

import androidx.camera.core.ImageProxy
import com.groceryoverview.data.ocr.ReceiptTextExtractor
import com.google.mlkit.vision.common.InputImage
import com.groceryoverview.domain.Receipt
import java.time.LocalDate

class ReceiptCaptureProcessor(
    private val receiptParser: ReceiptParser,
    private val textExtractor: ReceiptTextExtractor
) {
    suspend fun process(imageProxy: ImageProxy, purchaseDate: LocalDate, storeName: String? = null): Receipt {
        val mediaImage = imageProxy.image ?: error("Captured frame did not contain image data")
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val text = textExtractor.extractText(image)
        return receiptParser.parse(
            rawText = text,
            purchaseDate = purchaseDate,
            storeName = storeName
        )
    }
}

package com.groceryoverview.data.ocr

import com.google.mlkit.vision.common.InputImage

interface ReceiptTextExtractor {
    suspend fun extractText(image: InputImage): String
}

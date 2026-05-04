package com.groceryoverview.data.ocr

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

class MlKitReceiptTextExtractor : ReceiptTextExtractor {
    override suspend fun extractText(image: InputImage): String {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val result = recognizer.process(image).await()
        return result.text
    }
}

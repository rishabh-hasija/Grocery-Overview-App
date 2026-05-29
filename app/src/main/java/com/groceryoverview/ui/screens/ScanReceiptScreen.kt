package com.groceryoverview.ui.screens

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

@Composable
fun ScanReceiptScreen(
    onImageCaptured: (ImageProxy) -> Unit,
    onStopScan: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember { LifecycleCameraController(context) }

    LaunchedEffect(controller) {
        controller.setEnabledUseCases(androidx.camera.view.CameraController.IMAGE_CAPTURE)
        controller.bindToLifecycle(lifecycleOwner)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight(0.7f),
            factory = {
                PreviewView(it).apply {
                    this.controller = controller
                }
            }
        )

        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Hold the receipt steady. The app will process the image in memory, extract text, and store only the data.",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = {
                    controller.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                onImageCaptured(image)
                            }
                        }
                    )
                },
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Capture and Scan")
            }
            Button(
                onClick = onStopScan,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Back")
            }
        }
    }
}

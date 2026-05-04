package com.groceryoverview.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    receiptCount: Int,
    onScanClick: () -> Unit,
    onSummaryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Grocery Overview", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Saved receipts: $receiptCount")
                Text("Scan a bill, store only extracted data, and review shopping patterns over time.")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onScanClick) { Text("Scan Receipt") }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onSummaryClick) { Text("View Summary") }
    }
}

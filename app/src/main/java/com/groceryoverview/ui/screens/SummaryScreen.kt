package com.groceryoverview.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.groceryoverview.domain.PurchaseSummary

@Composable
fun SummaryScreen(summary: PurchaseSummary?, onBack: () -> Unit = {}) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        item {
            Text("Summary", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.Button(onClick = onBack) {
                Text("Back")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (summary == null) {
            item { Text("No receipts available yet.") }
            return@LazyColumn
        }

        item {
            Text(
                text = "Total Spent: ${"%.2f".format(summary.totalSpent)}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("Products", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (summary.itemTotals.isEmpty()) {
            item { Text("No items found.", style = MaterialTheme.typography.bodyMedium) }
        } else {
            items(summary.itemTotals) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = "Qty: ${"%.0f".format(item.quantity)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = "${"%.2f".format(item.totalSpent)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            Text("By Category", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (summary.categoryTotals.isEmpty()) {
            item { Text("No categories found.", style = MaterialTheme.typography.bodyMedium) }
        } else {
            items(summary.categoryTotals) { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = category.category.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "${category.itemCount} item${if (category.itemCount != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = "${"%.2f".format(category.totalSpent)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

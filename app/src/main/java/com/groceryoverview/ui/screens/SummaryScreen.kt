package com.groceryoverview.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.groceryoverview.domain.PurchaseSummary

@Composable
fun SummaryScreen(summary: PurchaseSummary?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Summary", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))
        if (summary == null) {
            Text("No receipts available yet.")
            return
        }
        Text("Total spent: %.2f".format(summary.totalSpent))
        Spacer(modifier = Modifier.height(16.dp))
        Text("By category", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(summary.categoryTotals) { category ->
                Card(modifier = Modifier.padding(vertical = 6.dp)) {
                    Text(
                        text = "${category.category}: ${category.itemCount} items, %.2f spent".format(category.totalSpent),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

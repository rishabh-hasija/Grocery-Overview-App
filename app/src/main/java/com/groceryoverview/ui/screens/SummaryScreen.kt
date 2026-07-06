package com.groceryoverview.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.groceryoverview.domain.AnalyticsPeriod
import com.groceryoverview.domain.ItemTotal
import com.groceryoverview.domain.PurchaseSummary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

private fun formatEur(value: Double): String =
    String.format(Locale.GERMANY, "%.2f €", value)

private fun formatQuantity(quantity: Double, unit: String): String? = when {
    unit == "kg" -> String.format(Locale.GERMANY, "%.3f kg", quantity)
    quantity == 1.0 -> null
    quantity == quantity.toLong().toDouble() -> "${quantity.toLong()}×"
    else -> String.format(Locale.GERMANY, "%.2f×", quantity)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    summary: PurchaseSummary?,
    receiptCount: Int,
    selectedPeriod: AnalyticsPeriod,
    fromDate: LocalDate,
    toDate: LocalDate,
    onPeriodSelect: (AnalyticsPeriod) -> Unit,
    onCustomRange: (LocalDate, LocalDate) -> Unit,
    onBack: () -> Unit,
    onClearAll: () -> Unit
) {
    BackHandler { onBack() }

    var showClearConfirm by remember { mutableStateOf(false) }
    var showRangePicker by remember { mutableStateOf(false) }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear all receipts?") },
            text = { Text("This will permanently delete all saved receipts and items.") },
            confirmButton = {
                TextButton(
                    onClick = { onClearAll(); showClearConfirm = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Clear All") }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) { Text("Cancel") }
            }
        )
    }

    if (showRangePicker) {
        val pickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = fromDate
                .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            initialSelectedEndDateMillis = toDate
                .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val startMillis = pickerState.selectedStartDateMillis
                        val endMillis = pickerState.selectedEndDateMillis
                        if (startMillis != null && endMillis != null) {
                            onCustomRange(
                                Instant.ofEpochMilli(startMillis).atZone(ZoneOffset.UTC).toLocalDate(),
                                Instant.ofEpochMilli(endMillis).atZone(ZoneOffset.UTC).toLocalDate()
                            )
                        }
                        showRangePicker = false
                    }
                ) { Text("Apply") }
            },
            dismissButton = {
                TextButton(onClick = { showRangePicker = false }) { Text("Cancel") }
            }
        ) {
            DateRangePicker(
                state = pickerState,
                modifier = Modifier.weight(1f),
                title = {
                    Text(
                        "Select range (max. 1 year)",
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp)
                    )
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to home"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Period filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnalyticsPeriod.entries.forEach { period ->
                    FilterChip(
                        selected = period == selectedPeriod,
                        onClick = {
                            if (period == AnalyticsPeriod.CUSTOM) {
                                showRangePicker = true
                            } else {
                                onPeriodSelect(period)
                            }
                        },
                        label = { Text(period.label) }
                    )
                }
            }
            Text(
                text = "${fromDate.format(DATE_FORMAT)} – ${toDate.format(DATE_FORMAT)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (summary == null || summary.itemTotals.isEmpty()) {
                val message = if (receiptCount == 0)
                    "No receipts scanned yet."
                else
                    "No purchases in the selected period.\nTry a different date range, or scan another receipt."
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = message, style = MaterialTheme.typography.bodyLarge)
                }
                return@Column
            }

            val groupedByCategory = summary.itemTotals.groupBy { it.category }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Total spend card + Clear All button
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Spend",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = formatEur(summary.totalSpent),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            TextButton(
                                onClick = { showClearConfirm = true },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Clear All")
                            }
                        }
                    }
                }

                // One section per category (ordered by total spend desc)
                for (catTotal in summary.categoryTotals) {
                    val items = groupedByCategory[catTotal.category] ?: continue

                    item(key = "header_${catTotal.category}") {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = catTotal.category.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${catTotal.itemCount} item${if (catTotal.itemCount == 1) "" else "s"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = formatEur(catTotal.totalSpent),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    items(items, key = { "item_${catTotal.category}_${it.name}_${it.unit}" }) { item ->
                        ItemRow(item)
                    }
                    item(key = "spacer_${catTotal.category}") {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemRow(item: ItemTotal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name.toTitleCase(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        formatQuantity(item.quantity, item.unit)?.let { qty ->
            Text(
                text = qty,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(
            text = formatEur(item.totalSpent),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun String.toTitleCase() =
    split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

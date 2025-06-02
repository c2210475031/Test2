package com.example.financetracker.presentation.transaction

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.financetracker.MainActivity
import com.example.financetracker.database.model.Transaction
import com.example.financetracker.database.model.Category
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionChartScreen(modifier: Modifier = Modifier, navController: NavController) {
    val viewModel = MainActivity.globalViewModel
    val transactions by viewModel.userTransactions.observeAsState(initial = emptyList())
    val categories by viewModel.userCategories.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Charts") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            BarChartView(
                context = LocalContext.current,
                transactions = transactions,
                categories = categories
            )
        }
    }
}

@Composable
fun BarChartView(
    context: Context,
    transactions: List<Transaction>,
    categories: List<Category>
) {
    // Group transactions by category ID and sum them
    val categorySums = transactions.groupBy { it.categoryId }
        .mapValues { (_, txs) -> txs.sumOf { it.amount.toDouble() } }

    val categoryNames = mutableListOf<String>()
    val barEntries = mutableListOf<BarEntry>()

    categorySums.entries.forEachIndexed { index, (categoryId, sum) ->
        val categoryName = categories.find { it.id == categoryId }?.name ?: "Unknown"
        categoryNames.add(categoryName)
        barEntries.add(BarEntry(index.toFloat(), sum.toFloat()))
    }

    AndroidView(
        factory = {
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    600
                )
                val dataSet = BarDataSet(barEntries, "Categories").apply {
                    color = Color.BLUE
                    valueTextColor = Color.BLACK
                    valueTextSize = 12f
                }

                data = BarData(dataSet)
                description.isEnabled = false

                xAxis.valueFormatter = IndexAxisValueFormatter(categoryNames)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)

                axisRight.isEnabled = false
                animateY(1000)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}
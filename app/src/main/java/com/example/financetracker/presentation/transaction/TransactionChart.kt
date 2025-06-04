package com.example.financetracker.presentation.transaction

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.financetracker.MainActivity
import com.example.financetracker.database.model.Category
import com.example.financetracker.database.model.Transaction
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
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                        )
                    }
                })
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
    // Get category names mapped to indices
    val categoryIdToName = categories.associateBy({ it.id }, { it.name })

    val categoryIds = categoryIdToName.keys.sorted()
    val categoryNames = categoryIds.map { categoryIdToName[it] ?: "Unknown" }

    val budgetEntries = mutableListOf<BarEntry>()
    val costEntries = mutableListOf<BarEntry>()

    categoryIds.forEachIndexed { index, categoryId ->
        val transactionsInCategory = transactions.filter { it.categoryId == categoryId }

        val budgetSum = transactionsInCategory
            .filter { it.isPositive }
            .sumOf { it.amount }
            .toFloat()

        val costSum = transactionsInCategory
            .filter { !it.isPositive }
            .sumOf { it.amount }
            .toFloat()

        budgetEntries.add(BarEntry(index.toFloat(), budgetSum))
        costEntries.add(BarEntry(index.toFloat(), costSum))
    }

    AndroidView(
        factory = {
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    600
                )

                val budgetDataSet = BarDataSet(budgetEntries, "Budget").apply {
                    color = Color.GREEN
                    valueTextColor = Color.BLACK
                    valueTextSize = 12f
                }

                val costDataSet = BarDataSet(costEntries, "Cost").apply {
                    color = Color.RED
                    valueTextColor = Color.BLACK
                    valueTextSize = 12f
                }

                val barData = BarData(budgetDataSet, costDataSet)
                val groupSpace = 0.2f
                val barSpace = 0.05f
                val barWidth = 0.35f

                barData.barWidth = barWidth
                data = barData

                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(categoryNames)
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                    axisMinimum = 0f
                    axisMaximum = categoryNames.size.toFloat()
                }

                axisRight.isEnabled = false
                description.isEnabled = false
                animateY(1000)

                groupBars(0f, groupSpace, barSpace)
                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
    )
}



package com.example.recipeapp.feature.recipes.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.recipeapp.feature.recipes.presentation.R
import com.example.recipeapp.feature.recipes.presentation.ui.components.CircularDonutChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.chart_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimensionResource(R.dimen.padding_large)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.chart_spacer_top)))

            Text(
                text = stringResource(R.string.chart_demo_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.chart_title_margin_bottom))
            )

            CircularDonutChart(
                sectors = listOf(52f, 60f, 70f, 80f),
                colors = listOf(
                    colorResource(R.color.chart_color_1),
                    colorResource(R.color.chart_color_2),
                    colorResource(R.color.chart_color_3),
                    colorResource(R.color.chart_color_4)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

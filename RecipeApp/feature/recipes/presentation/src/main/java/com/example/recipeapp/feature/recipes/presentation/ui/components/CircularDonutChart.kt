package com.example.recipeapp.feature.recipes.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.example.recipeapp.feature.recipes.presentation.R
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

data class ChartSector(
    val value: Float,
    val color: Color,
    val label: String
)

@Composable
fun CircularDonutChart(
    sectors: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    require(sectors.size in 2..7) { "Sectors count must be between 2 and 7" }
    require(sectors.size == colors.size) { "Sectors and colors lists must have the same size" }
    require(colors.distinct().size == colors.size) { "Colors must be unique" }

    val context = LocalContext.current
    val resources = context.resources
    
    val ringThicknessRatio = remember { ResourcesCompat.getFloat(resources, R.dimen.chart_ring_thickness_ratio) }
    val spacingRatio = remember { ResourcesCompat.getFloat(resources, R.dimen.chart_ring_spacing_ratio) }
    val arcAlphaUnselected = remember { ResourcesCompat.getFloat(resources, R.dimen.chart_arc_alpha_unselected) }
    val labelAlphaUnselected = remember { ResourcesCompat.getFloat(resources, R.dimen.chart_label_alpha_unselected) }
    val selectedScale = remember { ResourcesCompat.getFloat(resources, R.dimen.chart_selected_scale) }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val chartSectors = sectors.mapIndexed { index, value ->
        val coercedValue = value.coerceIn(1f, 100f)
        ChartSector(
            coercedValue,
            colors[index],
            stringResource(R.string.chart_percent_format, coercedValue.toInt())
        )
    }

    var labelColumnWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Row(
        modifier = modifier.padding(dimensionResource(R.dimen.chart_padding)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.width(labelColumnWidth + dimensionResource(R.dimen.chart_label_padding_start)))

        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.chart_size))
                .aspectRatio(1f)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(chartSectors) {
                        detectTapGestures { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val dx = offset.x - center.x
                            val dy = offset.y - center.y
                            val distance = sqrt(dx.pow(2) + dy.pow(2))
                            
                            val canvasSize = minOf(size.width, size.height).toFloat()
                            val outerRadius = canvasSize / 2f
                            val ringThickness = (outerRadius / (chartSectors.size + 1)) * ringThicknessRatio
                            val spacing = (outerRadius / (chartSectors.size + 1)) * spacingRatio
                            
                            var angle = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
                            if (angle < 0) angle += 360f
                            val normalizedAngle = (angle - 90f + 360f) % 360f
                            
                            var found = false
                            for (i in chartSectors.indices) {
                                val currentOuterRadius = outerRadius - i * (ringThickness + spacing)
                                val currentInnerRadius = currentOuterRadius - ringThickness
                                
                                if (distance in currentInnerRadius..currentOuterRadius) {
                                    val sweepAngle = (chartSectors[i].value / 100f) * 360f
                                    if (normalizedAngle <= sweepAngle) {
                                        selectedIndex = if (selectedIndex == i) null else i
                                        found = true
                                        break
                                    }
                                }
                            }
                            if (!found) selectedIndex = null
                        }
                    }
            ) {
                val canvasSize = minOf(size.width, size.height)
                val center = Offset(size.width / 2f, size.height / 2f)
                val outerRadius = canvasSize / 2f
                
                val ringThickness = (outerRadius / (chartSectors.size + 1)) * ringThicknessRatio
                val spacing = (outerRadius / (chartSectors.size + 1)) * spacingRatio

                chartSectors.forEachIndexed { index, sector ->
                    val isSelected = selectedIndex == index
                    
                    val radius = outerRadius - index * (ringThickness + spacing) - (ringThickness / 2f)
                    val arcSize = Size(radius * 2, radius * 2)
                    val arcTopLeft = Offset(center.x - radius, center.y - radius)

                    drawArc(
                        color = if (isSelected) sector.color else sector.color.copy(alpha = arcAlphaUnselected),
                        startAngle = 90f,
                        sweepAngle = (sector.value / 100f) * 360f,
                        useCenter = false,
                        topLeft = arcTopLeft,
                        size = arcSize,
                        style = Stroke(width = ringThickness, cap = StrokeCap.Round)
                    )
                }
            }
        }

        // Labels
        Column(
            modifier = Modifier
                .padding(start = dimensionResource(R.dimen.chart_label_padding_start))
                .onGloballyPositioned { coordinates ->
                    labelColumnWidth = with(density) { coordinates.size.width.toDp() }
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            val textSize = with(LocalDensity.current) { dimensionResource(R.dimen.chart_text_size).toSp() }
            chartSectors.forEachIndexed { index, sector ->
                val isSelected = selectedIndex == index
                val animatedColor by animateColorAsState(
                    if (isSelected) sector.color else sector.color.copy(alpha = labelAlphaUnselected),
                    label = "color"
                )
                val animatedScale by animateFloatAsState(
                    if (isSelected) selectedScale else 1.0f,
                    label = "scale"
                )

                Text(
                    text = sector.label,
                    color = animatedColor,
                    fontSize = textSize * animatedScale,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    softWrap = false,
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.chart_label_vertical_padding))
                )
            }
        }
    }
}

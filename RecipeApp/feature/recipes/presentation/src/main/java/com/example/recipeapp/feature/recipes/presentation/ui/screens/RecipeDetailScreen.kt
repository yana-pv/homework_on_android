package com.example.recipeapp.feature.recipes.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.recipeapp.feature.recipes.presentation.R
import com.example.recipeapp.feature.recipes.domain.model.RecipeDetail

@Composable
fun RecipeDetailScreen(
    detail: RecipeDetail,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.image_height_large))
                ) {
                    AsyncImage(
                        model = detail.image,
                        contentDescription = detail.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                                )
                            )
                    )

                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.padding_large)),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_medium)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))) {
                            Text(
                                text = detail.title,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
                            ) {
                                detail.category?.let {
                                    AssistChip(
                                        onClick = { },
                                        label = { Text(it) }
                                    )
                                }
                                detail.area?.let {
                                    AssistChip(
                                        onClick = { },
                                        label = { Text(it) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Ingredients header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(id = R.dimen.padding_large))
                        .padding(top = dimensionResource(id = R.dimen.padding_large))
                        .background(
                            color = colorResource(id = R.color.section_background),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_small))
                        )
                        .padding(dimensionResource(id = R.dimen.padding_normal))
                ) {
                    Text(
                        text = stringResource(id = R.string.ingredients),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            // Ingredients list
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.white))
                        .padding(horizontal = dimensionResource(id = R.dimen.padding_large), vertical = dimensionResource(id = R.dimen.padding_medium))
                ) {
                    detail.ingredients.forEach { ingredient ->
                        Text(
                            text = "• ${ingredient.measure} ${ingredient.name}".trim(),
                            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small)),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Instructions header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(id = R.dimen.padding_large))
                        .padding(top = dimensionResource(id = R.dimen.padding_large))
                        .background(
                            color = colorResource(id = R.color.section_background),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_small))
                        )
                        .padding(dimensionResource(id = R.dimen.padding_normal))
                ) {
                    Text(
                        text = stringResource(id = R.string.instructions),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            // Instructions text
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.white))
                        .padding(dimensionResource(id = R.dimen.padding_large))
                ) {
                    Text(
                        text = detail.instructions,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))
            }
        }

        // Back button
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = dimensionResource(id = R.dimen.padding_large), top = dimensionResource(id = R.dimen.padding_medium))
                .size(dimensionResource(id = R.dimen.button_back_size))
                .background(
                    color = colorResource(id = R.color.transparent_black_50),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_large))
                )
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.back),
                tint = colorResource(id = R.color.white),
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_back_size))
            )
        }
    }
}

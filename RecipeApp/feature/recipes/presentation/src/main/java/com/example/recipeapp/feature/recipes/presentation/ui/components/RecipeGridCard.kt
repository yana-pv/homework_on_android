package com.example.recipeapp.feature.recipes.presentation.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.recipeapp.feature.recipes.presentation.R
import com.example.recipeapp.feature.recipes.domain.model.Recipe

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun RecipeGridCard(
    recipe: Recipe,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val context = androidx.compose.ui.platform.LocalContext.current
    val cardAspectRatio = remember(context) { context.resources.getFloat(R.dimen.card_aspect_ratio)}

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(cardAspectRatio)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    isPressed = true
                    onClick()
                    isPressed = false
                }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPressed) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isPressed) colorResource(id = R.color.card_pressed)
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_medium))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = recipe.image,
                contentDescription = recipe.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(
                        topStart = dimensionResource(id = R.dimen.corner_medium),
                        topEnd = dimensionResource(id = R.dimen.corner_medium)
                    )),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_medium))
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    minLines = 2,
                    color = if (isPressed) colorResource(id = R.color.black)
                    else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

                recipe.category?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isPressed) colorResource(id = R.color.black).copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.primary
                    )
                }

                recipe.area?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isPressed) colorResource(id = R.color.black).copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
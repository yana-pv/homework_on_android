package com.example.recipeapp.feature.recipes.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.recipeapp.feature.recipes.domain.model.Recipe
import com.example.recipeapp.feature.recipes.presentation.R
import com.example.recipeapp.feature.recipes.presentation.MainViewModel
import com.example.recipeapp.feature.recipes.presentation.state.SearchState
import com.example.recipeapp.feature.recipes.presentation.ui.components.RecipeGridCard
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onRecipeClick: (Recipe) -> Unit,
    onShowChart: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(
                start = dimensionResource(id = R.dimen.padding_large),
                end = dimensionResource(id = R.dimen.padding_large),
                bottom = dimensionResource(id = R.dimen.padding_large)
            )
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            placeholder = { Text(stringResource(id = R.string.search_hint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (searchQuery.isNotBlank()) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onSearch(searchQuery)
                    }
                }
            ),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_small)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(id = R.color.search_border_focused),
                unfocusedBorderColor = colorResource(id = R.color.search_border_unfocused)
            )
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(id = R.dimen.padding_medium)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.all_recipes),
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = onShowChart) {
                Text("Show Chart")
            }
        }

        when (val state = searchState) {
            is SearchState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is SearchState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))
                        Button(
                            onClick = {
                                onSearch(searchQuery)
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ) {
                            Text(stringResource(id = R.string.retry))
                        }
                    }
                }
            }
            is SearchState.Success -> {
                if (state.recipes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(id = R.string.no_recipes_found))
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_spacing)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_spacing)),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.recipes, key = { it.id }) { recipe ->
                            RecipeGridCard(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe) }
                            )
                        }
                    }
                }
            }
            is SearchState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(id = R.string.empty_search_hint))
                }
            }
        }
    }
}
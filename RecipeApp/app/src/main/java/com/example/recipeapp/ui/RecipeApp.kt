package com.example.recipeapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipeapp.R
import com.example.recipeapp.core.common.Result
import com.example.recipeapp.feature.recipes.presentation.MainViewModel
import com.example.recipeapp.feature.recipes.presentation.RecipeDetailViewModel
import com.example.recipeapp.feature.recipes.presentation.di.RecipeDetailComponent
import com.example.recipeapp.feature.recipes.presentation.di.ViewModelFactory
import com.example.recipeapp.feature.recipes.presentation.ui.screens.RecipeDetailScreen
import com.example.recipeapp.feature.recipes.presentation.ui.screens.SearchScreen
import com.google.firebase.crashlytics.FirebaseCrashlytics

@Composable
fun RecipeApp(
    viewModelFactory: ViewModelFactory,
    detailComponentFactory: RecipeDetailComponent.Factory
) {
    val mainViewModel: MainViewModel = viewModel(factory = viewModelFactory)

    var selectedRecipeId by remember { mutableStateOf<String?>(null) }
    val snackbarMessage by mainViewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            mainViewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LaunchedEffect(selectedRecipeId) {
            val screenName = if (selectedRecipeId == null) "SearchScreen" else "RecipeDetailScreen($selectedRecipeId)"
            FirebaseCrashlytics.getInstance().log("Navigation to $screenName")
            FirebaseCrashlytics.getInstance().setCustomKey("current_screen", screenName)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (selectedRecipeId != null) {
                val detailViewModel: RecipeDetailViewModel = remember(selectedRecipeId) {
                    detailComponentFactory.create(selectedRecipeId!!).viewModel()
                }
                val detailResult by detailViewModel.state.collectAsState()

                when (val result = detailResult) {
                    is Result.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is Result.Success -> {
                        RecipeDetailScreen(
                            detail = result.data,
                            onBack = { selectedRecipeId = null }
                        )
                    }
                    is Result.NetworkError, is Result.ServerError, is Result.UnknownError,
                    is Result.ParsingError, is Result.NoDataError -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = stringResource(R.string.error_loading_details))
                        }
                    }
                    else -> {}
                }
            }
            else {
                SearchScreen(
                    viewModel = mainViewModel,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onSearch = { mainViewModel.searchRecipes(it) },
                    onRecipeClick = { recipe ->
                        selectedRecipeId = recipe.id
                    }
                )
            }
        }
    }
}

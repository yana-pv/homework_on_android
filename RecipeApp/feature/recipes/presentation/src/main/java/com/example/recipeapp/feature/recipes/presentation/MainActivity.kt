package com.example.recipeapp.feature.recipes.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipeapp.core.common.Result
import com.example.recipeapp.core.network.NetworkModule
import com.example.recipeapp.feature.recipes.data.api.MealApi
import com.example.recipeapp.feature.recipes.data.cache.RecipeCache
import com.example.recipeapp.feature.recipes.data.repository.RecipeRepositoryImpl
import com.example.recipeapp.feature.recipes.domain.usecase.GetRecipeDetailUseCase
import com.example.recipeapp.feature.recipes.presentation.ui.screens.RecipeDetailScreen
import com.example.recipeapp.feature.recipes.presentation.ui.screens.SearchScreen
import com.example.recipeapp.feature.recipes.presentation.ui.theme.RecipeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeAppTheme {
                RecipeApp()
            }
        }
    }
}

@Composable
fun RecipeApp() {
    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory.getInstance(context)
    )

    var selectedRecipeId by remember { mutableStateOf<String?>(null) }
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (selectedRecipeId != null) {
                val detailViewModel: RecipeDetailViewModel = viewModel(
                    key = selectedRecipeId,
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val networkModule = NetworkModule()
                            val client = networkModule.provideOkHttpClient()
                            val retrofit = networkModule.provideRetrofit(client)
                            val api = retrofit.create(MealApi::class.java)
                            val cache = RecipeCache(context.applicationContext)
                            val repository = RecipeRepositoryImpl(api, cache)
                            val useCase = GetRecipeDetailUseCase(repository)
                            return RecipeDetailViewModel(selectedRecipeId!!, useCase) as T
                        }
                    }
                )

                val detailResult by detailViewModel.state.collectAsStateWithLifecycle()

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
                    else -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "Error loading details")
                        }
                    }
                }
            }
            else {
                SearchScreen(
                    viewModel = viewModel,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onSearch = { viewModel.searchRecipes(it) },
                    onRecipeClick = { recipe -> selectedRecipeId = recipe.id }
                )
            }
        }
    }
}

package com.example.recipeapp.feature.recipes.presentation

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.recipeapp.core.common.NetworkUtils
import com.example.recipeapp.core.common.Result
import com.example.recipeapp.feature.recipes.domain.model.RecipeDetail
import com.example.recipeapp.feature.recipes.domain.repository.RecipeRepository
import com.example.recipeapp.feature.recipes.domain.usecase.GetRecipeDetailUseCase
import com.example.recipeapp.feature.recipes.domain.usecase.SearchRecipesUseCase
import com.example.recipeapp.feature.recipes.presentation.state.SearchState

class MainViewModel(
    private val context: Context,
    private val searchUseCase: SearchRecipesUseCase,
    private val detailUseCase: GetRecipeDetailUseCase,
    private val repository: RecipeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Empty)
    val searchState: StateFlow<SearchState> = _searchState

    private val _detailState = MutableStateFlow<RecipeDetail?>(null)
    val detailState: StateFlow<RecipeDetail?> = _detailState

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    init {
        savedStateHandle.get<String>("last_query")?.let { query ->
            if (query.isNotEmpty()) {
                searchRecipes(query)
            }
        }
    }

    fun searchRecipes(query: String) {
        if (query.isEmpty()) return

        // Проверяем интернет
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _searchState.value = SearchState.Error(context.getString(R.string.error_no_internet))
            return
        }

        _searchState.value = SearchState.Loading
        savedStateHandle["last_query"] = query

        viewModelScope.launch {
            when (val result = searchUseCase(query)) {
                is Result.Success -> {
                    _searchState.value = if (result.data.isEmpty()) {
                        SearchState.Error(context.getString(R.string.error_no_recipes, query))
                    } else {
                        SearchState.Success(result.data, repository.getLastDataSource())
                    }
                    _snackbarMessage.value = if (repository.getLastDataSource() == "CACHE") {
                        context.getString(R.string.data_from_cache)
                    } else {
                        context.getString(R.string.data_from_server)
                    }
                }
                is Result.NetworkError -> {
                    _searchState.value = SearchState.Error(context.getString(R.string.error_no_internet))
                }
                is Result.ServerError -> {
                    _searchState.value = SearchState.Error(
                        context.getString(R.string.error_server)
                    )
                }
                is Result.ParsingError -> {
                    _searchState.value = SearchState.Error(context.getString(R.string.error_parsing))
                }
                is Result.NoDataError -> {
                    _searchState.value = SearchState.Error(context.getString(R.string.error_no_recipes, query))
                }
                is Result.UnknownError -> {
                    _searchState.value = SearchState.Error(
                        context.getString(R.string.error_unknown, result.cause.message)
                    )
                }
            }
        }
    }

    fun getRecipeDetail(id: String) {
        viewModelScope.launch {
            when (val result = detailUseCase(id)) {
                is Result.Success -> {
                    _detailState.value = result.data
                }
                is Result.NetworkError -> {
                    _snackbarMessage.value = context.getString(R.string.error_no_internet)
                }
                is Result.ServerError -> {
                    _snackbarMessage.value = context.getString(R.string.error_server)
                }
                is Result.NoDataError -> {
                    _snackbarMessage.value = context.getString(R.string.error_not_found)
                }
                else -> {
                    _snackbarMessage.value = context.getString(R.string.error_unknown, result::class.java.simpleName)
                }
            }
        }
    }

    fun clearDetail() {
        _detailState.value = null
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
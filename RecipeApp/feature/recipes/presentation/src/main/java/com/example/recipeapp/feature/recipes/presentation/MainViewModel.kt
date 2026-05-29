package com.example.recipeapp.feature.recipes.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.common.NetworkUtils
import com.example.recipeapp.core.common.Result
import com.example.recipeapp.feature.recipes.domain.repository.RecipeRepository
import com.example.recipeapp.feature.recipes.domain.usecase.SearchRecipesUseCase
import com.example.recipeapp.feature.recipes.presentation.state.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class MainViewModel @Inject constructor(
    private val context: Context,
    private val searchUseCase: SearchRecipesUseCase,
    private val repository: RecipeRepository,
    @Named("sessionId") private val sessionId: String
) : ViewModel() {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Empty)
    val searchState: StateFlow<SearchState> = _searchState

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    init {
        _snackbarMessage.value = "Session started: $sessionId"
    }

    fun searchRecipes(query: String) {
        if (query.isEmpty()) return

        if (!NetworkUtils.isNetworkAvailable(context)) {
            _searchState.value = SearchState.Error(context.getString(R.string.error_no_internet))
            return
        }

        _searchState.value = SearchState.Loading

        viewModelScope.launch {
            when (val result = searchUseCase(query)) {
                is Result.Success -> {
                    _searchState.value = if (result.data.isEmpty()) {
                        SearchState.Error(context.getString(R.string.error_no_recipes, query))
                    } else {
                        SearchState.Success(result.data, repository.getLastDataSource())
                    }
                }
                is Result.Loading -> _searchState.value = SearchState.Loading
                is Result.NetworkError -> _searchState.value = SearchState.Error(context.getString(R.string.error_no_internet))
                is Result.ServerError -> _searchState.value = SearchState.Error(context.getString(R.string.error_server))
                is Result.ParsingError -> _searchState.value = SearchState.Error(context.getString(R.string.error_parsing))
                is Result.NoDataError -> _searchState.value = SearchState.Error(context.getString(R.string.error_no_recipes, query))
                is Result.UnknownError -> _searchState.value = SearchState.Error(context.getString(R.string.error_unknown, result.cause.message))
            }
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}

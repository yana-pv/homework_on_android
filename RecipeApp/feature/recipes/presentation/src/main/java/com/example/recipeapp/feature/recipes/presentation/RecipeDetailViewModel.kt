package com.example.recipeapp.feature.recipes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.common.Result
import com.example.recipeapp.feature.recipes.domain.model.RecipeDetail
import com.example.recipeapp.feature.recipes.domain.usecase.GetRecipeDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class RecipeDetailViewModel @Inject constructor(
    @Named("recipeId") private val recipeId: String,
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<Result<RecipeDetail>?>(null)
    val state: StateFlow<Result<RecipeDetail>?> = _state

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            _state.value = getRecipeDetailUseCase(recipeId)
        }
    }
}
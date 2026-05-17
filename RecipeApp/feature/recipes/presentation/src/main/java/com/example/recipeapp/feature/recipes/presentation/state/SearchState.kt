package com.example.recipeapp.feature.recipes.presentation.state

import com.example.recipeapp.feature.recipes.domain.model.Recipe

sealed class SearchState {
    object Empty : SearchState()
    object Loading : SearchState()
    data class Success(val recipes: List<Recipe>, val source: String) : SearchState()
    data class Error(val message: String) : SearchState()
}
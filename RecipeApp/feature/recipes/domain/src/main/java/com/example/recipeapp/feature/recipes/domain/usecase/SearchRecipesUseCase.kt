package com.example.recipeapp.feature.recipes.domain.usecase

import com.example.recipeapp.core.common.Result
import com.example.recipeapp.feature.recipes.domain.model.Recipe
import com.example.recipeapp.feature.recipes.domain.repository.RecipeRepository

class SearchRecipesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(query: String): Result<List<Recipe>> {
        if (query.isBlank()) return Result.Success(emptyList())
        return repository.searchRecipes(query)
    }
}
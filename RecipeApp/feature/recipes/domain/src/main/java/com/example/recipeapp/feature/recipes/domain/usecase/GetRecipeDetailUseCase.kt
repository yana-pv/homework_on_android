package com.example.recipeapp.feature.recipes.domain.usecase

import com.example.recipeapp.core.common.Result
import com.example.recipeapp.feature.recipes.domain.model.RecipeDetail
import com.example.recipeapp.feature.recipes.domain.repository.RecipeRepository

class GetRecipeDetailUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: String): Result<RecipeDetail> {
        return repository.getRecipeDetail(id)
    }
}
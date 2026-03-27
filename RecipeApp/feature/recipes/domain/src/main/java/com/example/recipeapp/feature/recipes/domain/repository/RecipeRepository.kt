package com.example.recipeapp.feature.recipes.domain.repository

import com.example.recipeapp.core.common.Result
import com.example.recipeapp.feature.recipes.domain.model.Recipe
import com.example.recipeapp.feature.recipes.domain.model.RecipeDetail

interface RecipeRepository {
    suspend fun searchRecipes(query: String): Result<List<Recipe>>
    suspend fun getRecipeDetail(id: String): Result<RecipeDetail>
    fun getLastDataSource(): String
}
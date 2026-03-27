package com.example.recipeapp.feature.recipes.data.repository

import com.example.recipeapp.core.common.Result
import com.example.recipeapp.core.common.safeApiCall
import com.example.recipeapp.feature.recipes.data.api.MealApi
import com.example.recipeapp.feature.recipes.data.cache.RecipeCache
import com.example.recipeapp.feature.recipes.data.mapper.RecipeMapper
import com.example.recipeapp.feature.recipes.domain.model.Recipe
import com.example.recipeapp.feature.recipes.domain.model.RecipeDetail
import com.example.recipeapp.feature.recipes.domain.repository.RecipeRepository

class RecipeRepositoryImpl(
    private val api: MealApi,
    private val cache: RecipeCache
) : RecipeRepository {

    private var lastDataSource: String = "UNKNOWN"

    override fun getLastDataSource(): String = lastDataSource

    override suspend fun searchRecipes(query: String): Result<List<Recipe>> {
        return safeApiCall {
            val cached = cache.get(query)

            if (cached != null) {
                lastDataSource = "CACHE"
                return@safeApiCall cached.map { RecipeMapper.toDomain(it) }
            }

            val response = api.searchMeals(query)
            val meals = response.meals ?: emptyList()

            if (meals.isNotEmpty()) {
                cache.put(query, meals)
            } else {
                throw Exception("No recipes found")
            }

            lastDataSource = "SERVER"
            meals.map { RecipeMapper.toDomain(it) }
        }
    }

    override suspend fun getRecipeDetail(id: String): Result<RecipeDetail> {
        return safeApiCall {
            val response = api.getMealDetails(id)
            val mealMap = response.meals?.firstOrNull()
                ?: throw Exception("Recipe not found")

            RecipeMapper.toDomain(mealMap)
        }
    }
}
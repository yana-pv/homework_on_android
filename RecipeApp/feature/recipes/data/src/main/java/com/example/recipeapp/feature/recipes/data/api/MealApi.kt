package com.example.recipeapp.feature.recipes.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealSearchResponse

    @GET("lookup.php")
    suspend fun getMealDetails(@Query("i") id: String): MealDetailResponse
}

data class MealSearchResponse(
    val meals: List<MealDto>?
)

data class MealDetailResponse(
    val meals: List<Map<String, String?>>?
)

data class MealDto(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String?,
    val strCategory: String?,
    val strArea: String?
)
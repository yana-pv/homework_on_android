package com.example.recipeapp.feature.recipes.data.mapper

import com.example.recipeapp.feature.recipes.data.api.MealDto
import com.example.recipeapp.feature.recipes.domain.model.Ingredient
import com.example.recipeapp.feature.recipes.domain.model.Recipe
import com.example.recipeapp.feature.recipes.domain.model.RecipeDetail

object RecipeMapper {

    fun toDomain(dto: MealDto): Recipe = Recipe(
        id = dto.idMeal,
        title = dto.strMeal,
        image = dto.strMealThumb,
        category = dto.strCategory,
        area = dto.strArea
    )

    fun toDomain(map: Map<String, String?>): RecipeDetail {
        val ingredients = mutableListOf<Ingredient>()

        for (i in 1..20) {
            val ingredient = map["strIngredient$i"]
            val measure = map["strMeasure$i"] ?: ""

            if (!ingredient.isNullOrBlank()) {
                ingredients.add(Ingredient(
                    name = ingredient.trim(),
                    measure = measure.trim()
                ))
            }
        }

        return RecipeDetail(
            id = map["idMeal"] ?: "",
            title = map["strMeal"] ?: "",
            image = map["strMealThumb"],
            category = map["strCategory"],
            area = map["strArea"],
            ingredients = ingredients,
            instructions = map["strInstructions"] ?: ""
        )
    }
}
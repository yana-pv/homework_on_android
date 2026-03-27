package com.example.recipeapp.feature.recipes.domain.model

data class Recipe(
    val id: String,
    val title: String,
    val image: String?,
    val category: String?,
    val area: String?
)

data class RecipeDetail(
    val id: String,
    val title: String,
    val image: String?,
    val category: String?,
    val area: String?,
    val ingredients: List<Ingredient>,
    val instructions: String
)

data class Ingredient(
    val name: String,
    val measure: String
)
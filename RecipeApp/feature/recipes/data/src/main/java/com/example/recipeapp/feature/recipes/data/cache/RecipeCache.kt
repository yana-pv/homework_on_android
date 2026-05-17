package com.example.recipeapp.feature.recipes.data.cache

import android.content.Context
import com.example.recipeapp.feature.recipes.data.api.MealDto
import com.google.gson.Gson
import java.io.File
import java.util.concurrent.TimeUnit

private data class CachedData(
    val query: String,
    val recipes: List<MealDto>,
    val timestamp: Long
)

class RecipeCache(
    private val context: Context,
    private val cacheDurationSeconds: Long = 60
) {
    private val cacheDir = File(context.filesDir, "recipe_cache")
    private val gson = Gson()

    init {
        if (!cacheDir.exists()){
            cacheDir.mkdirs()
        }
    }

    fun get(query: String): List<MealDto>? {
        val file = File(cacheDir, "${query.hashCode()}.json")
        if (!file.exists()) return null

        val cached = gson.fromJson(file.readText(), CachedData::class.java)
        val isExpired = System.currentTimeMillis() - cached.timestamp >
                TimeUnit.SECONDS.toMillis(cacheDurationSeconds)

        return if (isExpired) {
            file.delete()
            null
        } else {
            cached.recipes
        }
    }

    fun put(query: String, recipes: List<MealDto>) {
        val file = File(cacheDir, "${query.hashCode()}.json")
        val cached = CachedData(query, recipes, System.currentTimeMillis())
        file.writeText(gson.toJson(cached))
    }
}
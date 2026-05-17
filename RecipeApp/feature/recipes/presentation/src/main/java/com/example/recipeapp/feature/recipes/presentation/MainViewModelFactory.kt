package com.example.recipeapp.feature.recipes.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recipeapp.core.network.NetworkModule
import com.example.recipeapp.feature.recipes.data.api.MealApi
import com.example.recipeapp.feature.recipes.data.cache.RecipeCache
import com.example.recipeapp.feature.recipes.data.repository.RecipeRepositoryImpl
import com.example.recipeapp.feature.recipes.domain.repository.RecipeRepository
import com.example.recipeapp.feature.recipes.domain.usecase.GetRecipeDetailUseCase
import com.example.recipeapp.feature.recipes.domain.usecase.SearchRecipesUseCase

class MainViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val client = NetworkModule.createOkHttpClient()
        val retrofit = NetworkModule.createRetrofit(
            baseUrl = "https://www.themealdb.com/api/json/v1/1/",
            client = client
        )
        val api = NetworkModule.createApi<MealApi>(retrofit)
        val cache = RecipeCache(context.applicationContext)
        val repository: RecipeRepository = RecipeRepositoryImpl(api, cache)

        val searchUseCase = SearchRecipesUseCase(repository)
        val detailUseCase = GetRecipeDetailUseCase(repository)

        return MainViewModel(
            context = context.applicationContext,
            searchUseCase = searchUseCase,
            detailUseCase = detailUseCase,
            repository = repository,
            savedStateHandle = extras.createSavedStateHandle()
        ) as T
    }

    companion object {
        @Volatile
        private var instance: MainViewModelFactory? = null

        fun getInstance(context: Context): MainViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: MainViewModelFactory(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
package com.example.recipeapp.feature.recipes.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recipeapp.core.network.NetworkModule
import com.example.recipeapp.feature.recipes.data.api.MealApi
import com.example.recipeapp.feature.recipes.data.cache.RecipeCache
import com.example.recipeapp.feature.recipes.data.repository.RecipeRepositoryImpl
import com.example.recipeapp.feature.recipes.domain.repository.RecipeRepository
import com.example.recipeapp.feature.recipes.domain.usecase.SearchRecipesUseCase
import java.util.UUID

class MainViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val networkModule = NetworkModule()
        val client = networkModule.provideOkHttpClient()
        val retrofit = networkModule.provideRetrofit(client)
        val api = retrofit.create(MealApi::class.java)
        
        val cache = RecipeCache(context.applicationContext)
        val repository: RecipeRepository = RecipeRepositoryImpl(api, cache)

        val searchUseCase = SearchRecipesUseCase(repository)

        return MainViewModel(
            context = context.applicationContext,
            searchUseCase = searchUseCase,
            repository = repository,
            sessionId = UUID.randomUUID().toString()
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
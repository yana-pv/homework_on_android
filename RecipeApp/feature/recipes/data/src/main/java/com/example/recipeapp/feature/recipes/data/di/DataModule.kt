package com.example.recipeapp.feature.recipes.data.di

import android.content.Context
import com.example.recipeapp.feature.recipes.data.api.MealApi
import com.example.recipeapp.feature.recipes.data.cache.RecipeCache
import com.example.recipeapp.feature.recipes.data.repository.RecipeRepositoryImpl
import com.example.recipeapp.feature.recipes.domain.repository.RecipeRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideMealApi(retrofit: Retrofit): MealApi {
        return retrofit.create(MealApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRecipeCache(context: Context): RecipeCache {
        return RecipeCache(context)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(api: MealApi, cache: RecipeCache): RecipeRepository {
        return RecipeRepositoryImpl(api, cache)
    }
}
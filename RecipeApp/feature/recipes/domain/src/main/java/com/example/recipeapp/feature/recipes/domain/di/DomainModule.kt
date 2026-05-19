package com.example.recipeapp.feature.recipes.domain.di

import com.example.recipeapp.feature.recipes.domain.repository.RecipeRepository
import com.example.recipeapp.feature.recipes.domain.usecase.GetRecipeDetailUseCase
import com.example.recipeapp.feature.recipes.domain.usecase.SearchRecipesUseCase
import dagger.Module
import dagger.Provides

@Module
class DomainModule {

    @Provides
    fun provideSearchRecipesUseCase(repository: RecipeRepository): SearchRecipesUseCase {
        return SearchRecipesUseCase(repository)
    }

    @Provides
    fun provideGetRecipeDetailUseCase(repository: RecipeRepository): GetRecipeDetailUseCase {
        return GetRecipeDetailUseCase(repository)
    }
}
package com.example.recipeapp.feature.recipes.presentation.di

import com.example.recipeapp.feature.recipes.presentation.RecipeDetailViewModel
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Named

@Subcomponent
interface RecipeDetailComponent {

    fun viewModel(): RecipeDetailViewModel

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance @Named("recipeId") recipeId: String): RecipeDetailComponent
    }
}
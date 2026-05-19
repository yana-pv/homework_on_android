package com.example.recipeapp.feature.recipes.presentation.di

import androidx.lifecycle.ViewModel
import com.example.recipeapp.feature.recipes.presentation.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(subcomponents = [RecipeDetailComponent::class])
abstract class PresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}

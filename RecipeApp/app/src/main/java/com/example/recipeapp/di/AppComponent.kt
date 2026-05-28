package com.example.recipeapp.di

import android.content.Context
import com.example.recipeapp.core.network.NetworkModule
import com.example.recipeapp.feature.recipes.data.di.DataModule
import com.example.recipeapp.feature.recipes.domain.di.DomainModule
import com.example.recipeapp.MainActivity
import com.example.recipeapp.feature.recipes.presentation.di.PresentationModule
import com.example.recipeapp.feature.recipes.presentation.di.RecipeDetailComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetworkModule::class,
    DataModule::class,
    DomainModule::class,
    PresentationModule::class
])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun recipeDetailComponent(): RecipeDetailComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance @Named("sessionId") sessionId: String
        ): AppComponent
    }
}
package com.example.recipeapp

import android.app.Application
import com.example.recipeapp.di.AppComponent
import com.example.recipeapp.di.DaggerAppComponent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.util.UUID

class RecipeApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        val sessionId = UUID.randomUUID().toString()
        appComponent = DaggerAppComponent.factory().create(this, sessionId)

        FirebaseCrashlytics.getInstance().setUserId(sessionId)
        FirebaseCrashlytics.getInstance().setCustomKey("user_uuid", sessionId)
    }
}
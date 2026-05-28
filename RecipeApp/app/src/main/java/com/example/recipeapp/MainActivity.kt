package com.example.recipeapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.recipeapp.feature.recipes.presentation.di.RecipeDetailComponent
import com.example.recipeapp.feature.recipes.presentation.di.ViewModelFactory
import com.example.recipeapp.feature.recipes.presentation.ui.theme.RecipeAppTheme
import com.example.recipeapp.ui.AppInfoBottomSheet
import com.example.recipeapp.ui.NotificationPermissionHandler
import com.example.recipeapp.ui.RecipeApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var recipeDetailComponentFactory: RecipeDetailComponent.Factory

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as RecipeApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_DEBUG", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            Log.d("FCM_DEBUG", "FCM Token: ${task.result}")
        }

        enableEdgeToEdge()
        setContent {
            RecipeAppTheme {
                val context = LocalContext.current
                var showInfoScreen by remember {
                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    mutableStateOf(!prefs.getBoolean("info_shown", false))
                }

                if (showInfoScreen) {
                    AppInfoBottomSheet(
                        onDismiss = {
                            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            prefs.edit().putBoolean("info_shown", true).apply()
                            showInfoScreen = false
                            
                            val bundle = Bundle().apply {
                                putString("button_name", "accept_info")
                            }
                            firebaseAnalytics.logEvent("info_screen_dismissed", bundle)
                        },
                        onShow = {
                            firebaseAnalytics.logEvent("info_screen_shown", null)
                        }
                    )
                }

                NotificationPermissionHandler()
                RecipeApp(viewModelFactory, recipeDetailComponentFactory)
            }
        }
    }
}

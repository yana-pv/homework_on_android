package com.example.homework2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.homework2.data.Note
import com.example.homework2.screens.AddNoteScreen
import com.example.homework2.screens.LoginScreen
import com.example.homework2.screens.NotesScreen

@Composable
fun NotesApp() {
    val navController: NavHostController = rememberNavController()
    val notes = remember { mutableStateListOf<Note>() }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("notes/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            NotesScreen(
                navController = navController,
                email = email,
                notes = notes,
                onAddNote = {
                    navController.navigate("addNote")
                }
            )
        }

        composable("addNote") {
            AddNoteScreen(
                navController = navController,
                onSaveNote = { title, content ->
                    notes.add(Note(title, content))
                }
            )
        }
    }
}
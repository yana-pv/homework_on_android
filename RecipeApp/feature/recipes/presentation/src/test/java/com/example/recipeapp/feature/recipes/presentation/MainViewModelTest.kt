package com.example.recipeapp.feature.recipes.presentation

import android.content.Context
import app.cash.turbine.test
import com.example.recipeapp.core.common.NetworkUtils
import com.example.recipeapp.core.common.Result
import com.example.recipeapp.feature.recipes.domain.model.Recipe
import com.example.recipeapp.feature.recipes.domain.repository.RecipeRepository
import com.example.recipeapp.feature.recipes.domain.usecase.SearchRecipesUseCase
import com.example.recipeapp.feature.recipes.presentation.state.SearchState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val context: Context = mockk()
    private val searchUseCase: SearchRecipesUseCase = mockk()
    private val repository: RecipeRepository = mockk()
    private val sessionId = "test_session"

    private lateinit var viewModel: MainViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(NetworkUtils)
        
        // Mock string resources
        every { context.getString(any()) } returns "Error message"
        every { context.getString(any(), any()) } returns "Error message with arg"
        
        viewModel = MainViewModel(
            context = context,
            searchUseCase = searchUseCase,
            repository = repository,
            sessionId = sessionId
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `searchRecipes should update state to Success when use case returns data`() = runTest {
        // Arrange
        val query = "Pasta"
        val recipes = listOf(Recipe("1", "Pasta", null, null, null))
        
        every { NetworkUtils.isNetworkAvailable(context) } returns true
        coEvery { searchUseCase(query) } returns Result.Success(recipes)
        every { repository.getLastDataSource() } returns "Remote"

        // Act & Assert
        viewModel.searchState.test {
            assertEquals(SearchState.Empty, awaitItem())
            
            viewModel.searchRecipes(query)
            
            assertEquals(SearchState.Loading, awaitItem())
            val successState = awaitItem() as SearchState.Success
            assertEquals(recipes, successState.recipes)
            assertEquals("Remote", successState.source)
        }
    }

    @Test
    fun `clearSnackbar should set snackbarMessage to null`() = runTest {
        assertEquals("Session started: $sessionId", viewModel.snackbarMessage.value)

        // Act
        viewModel.clearSnackbar()

        // Assert
        assertEquals(null, viewModel.snackbarMessage.value)
    }

    @Test
    fun `searchRecipes should update state to Error when use case returns empty list`() = runTest {
        // Arrange
        val query = "UnknownFood"
        
        every { NetworkUtils.isNetworkAvailable(context) } returns true
        coEvery { searchUseCase(query) } returns Result.Success(emptyList())

        // Act & Assert
        viewModel.searchState.test {
            assertEquals(SearchState.Empty, awaitItem())
            
            viewModel.searchRecipes(query)
            
            assertEquals(SearchState.Loading, awaitItem())
            val errorState = awaitItem() as SearchState.Error
            assert(errorState.message.contains("Error message"))
        }
    }
}

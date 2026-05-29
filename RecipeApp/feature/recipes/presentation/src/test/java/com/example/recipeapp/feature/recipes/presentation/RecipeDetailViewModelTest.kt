package com.example.recipeapp.feature.recipes.presentation

import app.cash.turbine.test
import com.example.recipeapp.core.common.Result
import com.example.recipeapp.feature.recipes.domain.model.RecipeDetail
import com.example.recipeapp.feature.recipes.domain.usecase.GetRecipeDetailUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeDetailViewModelTest {

    private val recipeId = "52772"
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load recipe detail successfully`() = runTest {
        // Arrange
        val detail = mockk<RecipeDetail>()
        val expectedResult = Result.Success(detail)
        coEvery { getRecipeDetailUseCase(recipeId) } returns expectedResult

        // Act
        val viewModel = RecipeDetailViewModel(recipeId, getRecipeDetailUseCase)

        // Assert
        viewModel.state.test {
            assertEquals(expectedResult, awaitItem())
        }
    }

    @Test
    fun `init should set error state when use case fails`() = runTest {
        // Arrange
        val expectedResult = Result.ServerError(500, "Internal Server Error")
        coEvery { getRecipeDetailUseCase(recipeId) } returns expectedResult

        // Act
        val viewModel = RecipeDetailViewModel(recipeId, getRecipeDetailUseCase)

        // Assert
        viewModel.state.test {
            assertEquals(expectedResult, awaitItem())
        }
    }
}

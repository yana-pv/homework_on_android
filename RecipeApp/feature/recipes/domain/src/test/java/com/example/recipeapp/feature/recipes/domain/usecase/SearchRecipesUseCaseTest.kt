package com.example.recipeapp.feature.recipes.domain.usecase

import com.example.recipeapp.core.common.Result
import com.example.recipeapp.feature.recipes.domain.model.Recipe
import com.example.recipeapp.feature.recipes.domain.repository.RecipeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchRecipesUseCaseTest {

    private val repository: RecipeRepository = mockk()
    private val useCase = SearchRecipesUseCase(repository)

    @Test
    fun `invoke should call repository and return success result`() = runTest {
        // Arrange
        val query = "Pasta"
        val expectedRecipes = listOf(
            Recipe("1", "Pasta Carbonara", null, "Pasta", "Italian"),
            Recipe("2", "Pasta Bolognese", null, "Pasta", "Italian")
        )
        val expectedResult = Result.Success(expectedRecipes)

        coEvery { repository.searchRecipes(query) } returns expectedResult

        // Act
        val result = useCase(query)

        // Assert
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { repository.searchRecipes(query) }
    }

    @Test
    fun `invoke should return empty list when query is blank`() = runTest {
        // Arrange
        val query = "   "

        // Act
        val result = useCase(query)

        // Assert
        assert(result is Result.Success && result.data.isEmpty())
        coVerify(exactly = 0) { repository.searchRecipes(any()) }
    }
}

package com.example.recipeapp.feature.recipes.domain.usecase

import com.example.recipeapp.core.common.Result
import com.example.recipeapp.feature.recipes.domain.model.RecipeDetail
import com.example.recipeapp.feature.recipes.domain.repository.RecipeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetRecipeDetailUseCaseTest {

    private val repository: RecipeRepository = mockk()
    private val useCase = GetRecipeDetailUseCase(repository)

    @Test
    fun `invoke should call repository and return recipe detail`() = runTest {
        // Arrange
        val recipeId = "1"
        val expectedDetail = RecipeDetail(
            id = "1",
            title = "Pasta",
            image = null,
            category = "Italian",
            area = "Italy",
            ingredients = emptyList(),
            instructions = "Cook it"
        )
        val expectedResult = Result.Success(expectedDetail)

        coEvery { repository.getRecipeDetail(recipeId) } returns expectedResult

        // Act
        val result = useCase(recipeId)

        // Assert
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { repository.getRecipeDetail(recipeId) }
    }
}

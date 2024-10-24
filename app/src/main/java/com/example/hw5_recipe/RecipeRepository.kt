package com.example.hw5_recipe

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RecipeRepository(private val apiKey: String) {
    private val api = ApiService.api

    fun searchRecipes(
        query: String?,
        cuisine: String?,
        diet: String?,
        maxCalories: Int?
    ): Flow<List<Recipe>> = flow {
        val response = api.searchRecipes(apiKey, query, cuisine, diet, maxCalories)
        emit(response.results)
    }

    suspend fun getRecipeDetail(id: Int): RecipeDetail {
        return api.getRecipeDetail(id, apiKey)
    }
}
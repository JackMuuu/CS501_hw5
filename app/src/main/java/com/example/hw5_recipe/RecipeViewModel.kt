package com.example.hw5_recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {
    var query by mutableStateOf("")
    var cuisine by mutableStateOf("")
    var diet by mutableStateOf("")
    var maxCalories by mutableStateOf("")

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> get() = _recipes

    private val _selectedRecipe = MutableStateFlow<RecipeDetail?>(null)
    val selectedRecipe: StateFlow<RecipeDetail?> get() = _selectedRecipe

    fun searchRecipes(
        query: String?,
        cuisine: String?,
        diet: String?,
        maxCalories: Int?
    ) {
        viewModelScope.launch {
            repository.searchRecipes(query, cuisine, diet, maxCalories).collect { recipeList ->
                _recipes.value = recipeList
            }
        }
    }

    fun getRecipeDetail(id: Int) {
        viewModelScope.launch {
            val recipeDetail = repository.getRecipeDetail(id)
            _selectedRecipe.value = recipeDetail
        }
    }

    fun clearSelectedRecipe() {
        _selectedRecipe.value = null
    }
}
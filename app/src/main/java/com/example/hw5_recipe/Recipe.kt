package com.example.hw5_recipe

import com.squareup.moshi.Json

data class RecipeResponse(
    val results: List<Recipe>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)

data class Recipe(
    val id: Int,
    val title: String,
    @Json(name = "image") val imageUrl: String
)

data class RecipeDetail(
    val id: Int,
    val title: String,
    val image: String,
    val instructions: String?,
    val extendedIngredients: List<Ingredient>
)

data class Ingredient(
    val id: Int,
    val original: String
)
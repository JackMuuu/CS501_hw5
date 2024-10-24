package com.example.hw5_recipe

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import java.util.regex.Pattern
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel


class MainActivity : ComponentActivity() {
    private val apiKey = BuildConfig.SpoonacularAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val repository = RecipeRepository(apiKey)
            val viewModel: RecipeViewModel = viewModel(
                factory = RecipeViewModelFactory(repository)
            )
            RecipeApp(viewModel)
        }
    }
}

//Search bars with Recipes names, Cuisine type, Diet, and Max Calories.
@Composable
fun SearchBar(viewModel: RecipeViewModel, onSearch: (String, String?, String?, Int?) -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.padding(10.dp)) {
        OutlinedTextField(
            value = viewModel.query,
            onValueChange = { viewModel.query = it },
            label = { Text("Search Recipes") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
        )
        OutlinedTextField(
            value = viewModel.cuisine,
            onValueChange = { viewModel.cuisine = it },
            label = { Text("Cuisine Type") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
        )
        OutlinedTextField(
            value = viewModel.diet,
            onValueChange = { viewModel.diet = it },
            label = { Text("Diet") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
        )
        OutlinedTextField(
            value = viewModel.maxCalories,
            onValueChange = { viewModel.maxCalories = it },
            label = { Text("Max Calories") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
        )
        Button(
            onClick = {
                val calories = viewModel.maxCalories.toIntOrNull()
                onSearch(
                    viewModel.query,
                    viewModel.cuisine.takeIf { it.isNotBlank() },
                    viewModel.diet.takeIf { it.isNotBlank() },
                    calories
                )
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Search")
        }
    }
}

//The overall structure of the lazy list
@Composable
fun RecipeList(recipes: List<Recipe>, onRecipeClick: (Int) -> Unit) {
    LazyColumn {
        items(recipes) { recipe ->
            RecipeItem(recipe = recipe, onClick = { onRecipeClick(recipe.id) })
        }
    }
}

//The lazy list that contains recipes searched from API
@Composable
fun RecipeItem(recipe: Recipe, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(recipe.imageUrl),
            contentDescription = recipe.title,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(recipe.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.CenterVertically))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecipeDetailScreen(recipeDetail: RecipeDetail, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            //An empty top bar with a back arrow
            TopAppBar(
                modifier = Modifier.height(50.dp),
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        // Details in a column
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .verticalScroll(scrollState)
        ) {

            Text(
                text = recipeDetail.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 22.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Image(
                painter = rememberAsyncImagePainter(recipeDetail.image),
                contentDescription = recipeDetail.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ingredients:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            for (ingredient in recipeDetail.extendedIngredients) {
                Text("- ${ingredient.original}")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Instructions:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = recipeDetail.instructions?.stripHtml() ?: "No instructions available.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
        }
    }
}


@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Composable
fun RecipeApp(viewModel: RecipeViewModel) {
    val recipes by viewModel.recipes.collectAsState()
    val selectedRecipe by viewModel.selectedRecipe.collectAsState()
    var showDetail by rememberSaveable { mutableStateOf(false) }
    val isLandscape = isLandscape()

    if (showDetail && selectedRecipe != null) {
        // Recipe Detail Screen
        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left pane: Recipe list in landscape mode
                Box(modifier = Modifier.weight(0.3f)) {
                    RecipeList(recipes = recipes) { recipeId ->
                        viewModel.getRecipeDetail(recipeId)
                        showDetail = true
                    }
                }
                // Right pane: Recipe details in landscape mode
                Box(modifier = Modifier.weight(0.7f)) {
                    RecipeDetailScreen(
                        recipeDetail = selectedRecipe!!,
                        onBack = { showDetail = false }
                    )
                }
            }
        } else {
            // Portrait mode: Only show RecipeDetail
            RecipeDetailScreen(
                recipeDetail = selectedRecipe!!,
                onBack = {
                    showDetail = false
                    viewModel.clearSelectedRecipe()
                }
            )
        }
    } else {
        if (isLandscape) {
            // Landscape mode: Show search bars on the left and recipe list on the right
            Row(modifier = Modifier.fillMaxSize()) {
                // Left pane: Search bars
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    SearchBar(viewModel) { query, cuisine, diet, maxCalories ->
                        viewModel.searchRecipes(query, cuisine, diet, maxCalories)
                    }
                }

                // Right pane: welcome text or recipe list
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .padding(16.dp)
                ) {
                    if (recipes.isEmpty()) {
                        // show welcome text
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Welcome to Yuanman's Recipe Search :)",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    } else {
                        // Show recipe list
                        RecipeList(recipes = recipes) { recipeId ->
                            viewModel.getRecipeDetail(recipeId)
                            showDetail = true
                        }
                    }
                }
            }
        } else {
            // Portrait mode: everything in a column
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Welcome to Yuanman's Recipe Search :)",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                )

                // Search bars and recipe list below
                SearchBar(viewModel) { query, cuisine, diet, maxCalories ->
                    viewModel.searchRecipes(query, cuisine, diet, maxCalories)
                }
                RecipeList(recipes = recipes) { recipeId ->
                    viewModel.getRecipeDetail(recipeId)
                    showDetail = true
                }
            }
        }
    }
}



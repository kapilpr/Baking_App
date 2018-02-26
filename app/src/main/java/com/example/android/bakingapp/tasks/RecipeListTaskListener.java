package com.example.android.bakingapp.tasks;

import com.example.android.bakingapp.models.Recipe;

import java.util.ArrayList;

public interface RecipeListTaskListener {

    public void onTaskPreExecute();
    public void onTaskPostExecute(ArrayList<Recipe> recipeArrayList);
}

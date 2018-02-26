package com.example.android.bakingapp.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.utilities.NetworkUtils;

import java.util.ArrayList;

public class RecipeListAsyncTask extends AsyncTask<Void, Void, ArrayList<Recipe>> {

    private RecipeListTaskListener mRecipeListTaskListener;
    private Context mContext;

    public RecipeListAsyncTask(Context context, RecipeListTaskListener recipeListTaskListener) {
        mContext = context;
        mRecipeListTaskListener = recipeListTaskListener;
    }

    @Override
    protected void onPreExecute() {
        mRecipeListTaskListener.onTaskPreExecute();
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Recipe> doInBackground(Void... voids) {
        return NetworkUtils.fetchRecipeList();
    }

    @Override
    protected void onPostExecute(ArrayList<Recipe> recipes) {
        mRecipeListTaskListener.onTaskPostExecute(recipes);
    }
}

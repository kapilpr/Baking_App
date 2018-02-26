package com.example.android.bakingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingapp.fragments.RecipeDetailFragment;
import com.example.android.bakingapp.fragments.StepsDetailFragment;
import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.models.Steps;
import com.example.android.bakingapp.widget.BakingWidgetProvider;
import com.google.gson.Gson;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepsItemClickListener {

    private ArrayList<Steps> mStepsArrayList;
    private String mRecipeName;
    public static boolean mTwoPane = false;

    public static final String RECIPE_NAME_KEY = "recipe_name";
    public static final String STEPS_LIST_KEY = "steps_list";
    public static final String STEPS_POSITION_KEY = "steps_position";
    public static final String INGREDIENTS_LIST_KEY = "ingredients_list";
    public static final String ACTION_UPDATE_WIDGET = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String WIDGET_RECIPE_KEY = "wiget_recipe";
    public static final String WIDGET_PREFERENCE_RECIPE_NAME_KEY = "widget_preference_recipe_name";
    public static final String WIDGET_PREFERENCE_INGREDIENTS_KEY = "widget_preference_ingredients";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Recipe recipe = getIntent().getParcelableExtra(MainActivity.RECIPE_KEY);
        if (recipe != null) {
            mStepsArrayList = recipe.getStepsList();
            mRecipeName = recipe.getName();
            RecipeDetailActivity.this.setTitle(mRecipeName);
            // Broadcast the recipe
            broadcastRecipe(recipe);
            // Store the ingredients data in SharedPreferences
            saveIngredientsForWidget(recipe);

            if (savedInstanceState == null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (findViewById(R.id.recipe_detail_tablet_view) != null) {
                    mTwoPane = true;
                    RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
                    recipeDetailFragment.setRecipe(recipe);
                    fragmentManager.beginTransaction()
                            .add(R.id.recipe_detail_fl, recipeDetailFragment)
                            .commit();

                    StepsDetailFragment stepsDetailFragment = new StepsDetailFragment();
                    stepsDetailFragment.setStepsArrayList(mStepsArrayList);
                    fragmentManager.beginTransaction()
                            .add(R.id.steps_detail_fl, stepsDetailFragment)
                            .commit();
                } else {
                    RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
                    recipeDetailFragment.setRecipe(recipe);
                    fragmentManager.beginTransaction()
                            .add(R.id.recipe_detail_fl, recipeDetailFragment)
                            .commit();
                }
            }
        }
    }

    private void broadcastRecipe(Recipe recipe) {
        Intent recipeBroadcastIntent = new Intent(RecipeDetailActivity.this, BakingWidgetProvider.class);
        recipeBroadcastIntent.setAction(ACTION_UPDATE_WIDGET);
        recipeBroadcastIntent.putExtra(WIDGET_RECIPE_KEY, recipe);
        sendBroadcast(recipeBroadcastIntent);
    }

    private void saveIngredientsForWidget(Recipe recipe) {
        Gson gson = new Gson();
        String recipeNameJson = gson.toJson(recipe.getName());
        String ingredientsJson = gson.toJson(recipe.getIngredientsList());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(WIDGET_PREFERENCE_RECIPE_NAME_KEY, recipeNameJson);
        editor.putString(WIDGET_PREFERENCE_INGREDIENTS_KEY, ingredientsJson);
        editor.apply();
    }

    @Override
    public void onStepSelected(int position) {
        if (!mTwoPane) {
            Intent startStepsDetailActivityIntent = new Intent(RecipeDetailActivity.this, StepsDetailActivity.class);
            startStepsDetailActivityIntent.putParcelableArrayListExtra(STEPS_LIST_KEY, mStepsArrayList);
            startStepsDetailActivityIntent.putExtra(STEPS_POSITION_KEY, position);
            startStepsDetailActivityIntent.putExtra(RECIPE_NAME_KEY, mRecipeName);
            startActivity(startStepsDetailActivityIntent);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            StepsDetailFragment stepsDetailFragment = new StepsDetailFragment();
            stepsDetailFragment.setClickedPosition(position);
            stepsDetailFragment.setStepsArrayList(mStepsArrayList);
            fragmentManager.beginTransaction()
                    .replace(R.id.steps_detail_fl, stepsDetailFragment)
                    .commit();
        }
    }
}


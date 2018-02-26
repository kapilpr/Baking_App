package com.example.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakingapp.adapters.RecipeAdapter;
import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.tasks.RecipeListAsyncTask;
import com.example.android.bakingapp.tasks.RecipeListTaskListener;
import com.example.android.bakingapp.widget.BakingWidgetProvider;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeAdapterOnClickHandler {

    @BindView(R.id.recyclerview_recipe_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.tv_no_internet_message)
    TextView mNoInternetTextView;

    private static final String SAVE_LAYOUT_MANAGER_KEY = "save_layout_manager";
    public static final String RECIPE_KEY = "recipe";

    private RecipeAdapter mRecipeAdapter;
    private Parcelable mLayoutManagerSavedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Timber
        Timber.plant(new Timber.DebugTree());

        ButterKnife.bind(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns());

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecipeAdapter = new RecipeAdapter(this, this);

        mRecyclerView.setAdapter(mRecipeAdapter);

        loadRecipe();
    }

    private void loadRecipe() {
        if (isNetworkAvailable(getApplicationContext())) {
            showRecipeDataView();

            new RecipeListAsyncTask(this, new RecipeListTaskListener() {
                @Override
                public void onTaskPreExecute() {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                }

                @Override
                public void onTaskPostExecute(ArrayList<Recipe> recipeArrayList) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);

                    if (!recipeArrayList.isEmpty()) {
                        mRecipeAdapter.setRecipeList(recipeArrayList);
                        // Restore the Layout Manager saved state
                        if (mLayoutManagerSavedState != null) {
                            mRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManagerSavedState);
                        }
                    }
                }
            }).execute();

        } else {
            showNoInternetView();
        }
    }

    private void showNoInternetView() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mNoInternetTextView.setVisibility(View.VISIBLE);
    }

    private void showRecipeDataView() {
        /* First, make sure the error is invisible */
        mNoInternetTextView.setVisibility(View.INVISIBLE);
        /* Then, make sure the baking data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Returns true if network is available or about to become available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVE_LAYOUT_MANAGER_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mLayoutManagerSavedState = savedInstanceState.getParcelable(SAVE_LAYOUT_MANAGER_KEY);
        super.onRestoreInstanceState(savedInstanceState);
    }

    // Method to calculate the number of columns dynamically
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // Change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 1;
        return nColumns;
    }

    @Override
    public void onClick(Recipe recipe) {
        Intent startRecipeDetailActivity = new Intent(MainActivity.this, RecipeDetailActivity.class);
        startRecipeDetailActivity.putExtra(RECIPE_KEY, recipe);
        startActivity(startRecipeDetailActivity);

    }
}

package com.example.android.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.bakingapp.MainActivity;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeDetailActivity;
import com.example.android.bakingapp.adapters.IngredientsAdapter;
import com.example.android.bakingapp.adapters.StepsAdapter;
import com.example.android.bakingapp.models.Ingredients;
import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.models.Steps;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailFragment extends Fragment implements StepsAdapter.StepsAdapterOnClickHandler {

    public interface OnStepsItemClickListener {
        void onStepSelected(int position);
    }

    OnStepsItemClickListener mCallback;

    @BindView(R.id.ingredients_list_rv)
    RecyclerView recipeIngredientsListRecyclerView;

    @BindView(R.id.steps_list_rv)
    RecyclerView recipeStepsListRecyclerView;

    @BindView(R.id.recipe_image_iv)
    ImageView recipeImageView;

    private Recipe mRecipe;
    ArrayList<Ingredients> ingredientsArrayList;
    ArrayList<Steps> stepsArrayList;

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnStepsItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepsItemClickListener");
        }
    }

    public RecipeDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            ingredientsArrayList = savedInstanceState.getParcelableArrayList(RecipeDetailActivity.INGREDIENTS_LIST_KEY);
            stepsArrayList = savedInstanceState.getParcelableArrayList(RecipeDetailActivity.STEPS_LIST_KEY);
            mRecipe = savedInstanceState.getParcelable(MainActivity.RECIPE_KEY);
        } else {
            // Obtain the ingredients and steps for the current mRecipe
            ingredientsArrayList = mRecipe.getIngredientsList();
            stepsArrayList = mRecipe.getStepsList();
        }
        // Setting up the Image of the Recipe
        String imageUrl = mRecipe.getImage();
        if (!imageUrl.isEmpty()) {
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .error(R.drawable.baking_basics)
                    .into(recipeImageView);
        } else {
            recipeImageView.setVisibility(View.GONE);
        }
        recipeIngredientsListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        recipeStepsListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));

        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(ingredientsArrayList);
        StepsAdapter stepsAdapter = new StepsAdapter(this, stepsArrayList);

        recipeIngredientsListRecyclerView.setAdapter(ingredientsAdapter);
        recipeStepsListRecyclerView.setAdapter(stepsAdapter);

        return rootView;
    }

    @Override
    public void onClick(int clickedPosition) {
        mCallback.onStepSelected(clickedPosition);
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(RecipeDetailActivity.INGREDIENTS_LIST_KEY, ingredientsArrayList);
        outState.putParcelableArrayList(RecipeDetailActivity.STEPS_LIST_KEY, stepsArrayList);
        outState.putParcelable(MainActivity.RECIPE_KEY, mRecipe);
        super.onSaveInstanceState(outState);
    }
}


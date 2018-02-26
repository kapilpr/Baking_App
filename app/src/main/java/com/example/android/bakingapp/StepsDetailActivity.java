package com.example.android.bakingapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingapp.fragments.StepsDetailFragment;
import com.example.android.bakingapp.models.Steps;

import java.util.ArrayList;

public class StepsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_detail);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() != null) {
            ArrayList<Steps> stepsArrayList = getIntent().getParcelableArrayListExtra(RecipeDetailActivity.STEPS_LIST_KEY);
            int position = getIntent().getExtras().getInt(RecipeDetailActivity.STEPS_POSITION_KEY);
            String recipeName = getIntent().getExtras().getString(RecipeDetailActivity.RECIPE_NAME_KEY);
            StepsDetailActivity.this.setTitle(recipeName);

            if (savedInstanceState == null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                StepsDetailFragment stepsDetailFragment = new StepsDetailFragment();
                stepsDetailFragment.setStepsArrayList(stepsArrayList);
                stepsDetailFragment.setClickedPosition(position);
                fragmentManager.beginTransaction()
                        .add(R.id.steps_detail_fl, stepsDetailFragment)
                        .commit();
            }
        }
    }
}

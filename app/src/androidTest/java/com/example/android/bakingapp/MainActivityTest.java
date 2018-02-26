package com.example.android.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * This test demos a user clicking on a Recycler View item in MainActivity which opens up the
 * corresponding RecipeDetailActivity.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    public static final String RECIPE_KEY = "recipe";

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule =
            new IntentsTestRule<>(MainActivity.class);

    @Test
    public void clickMainActivityRecyclerView() {
        // Check if the Recycler View is Displayed
        onView(withId(R.id.recyclerview_recipe_list)).check(matches(isDisplayed()));

        // Click on a Recycler View position
        onView(withId(R.id.recyclerview_recipe_list)).perform(RecyclerViewActions
                .actionOnItemAtPosition(1, click()));

        // Check for the intents sent
        intended(hasComponent(RecipeDetailActivity.class.getName()));
        intended(hasExtraWithKey(RECIPE_KEY));
    }
}

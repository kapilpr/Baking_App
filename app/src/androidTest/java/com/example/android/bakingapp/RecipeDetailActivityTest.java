package com.example.android.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RecipeDetailActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickRecipeDetailActivityRecyclerView() {
        onView(withId(R.id.recyclerview_recipe_list))
                .perform(actionOnItemAtPosition(1, click()));
        onView(withId(R.id.ingredients_list_rv)).check(matches(isDisplayed()));
        onView(withId(R.id.steps_list_rv)).check(matches(isDisplayed()));
        // Scroll and Click on a Recycler View position
        onView(withId(R.id.steps_list_rv))
                .perform(actionOnItemAtPosition(0, click()))
                .check(matches(hasDescendant(withText("Recipe Introduction"))));
    }
}

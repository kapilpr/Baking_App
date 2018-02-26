package com.example.android.bakingapp.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.example.android.bakingapp.MainActivity;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeDetailActivity;
import com.example.android.bakingapp.models.Ingredients;
import com.example.android.bakingapp.models.Recipe;
import com.google.gson.Gson;

import java.util.ArrayList;

import timber.log.Timber;


public class BakingWidgetProvider extends AppWidgetProvider {
    private Recipe mRecipe;
    private ArrayList<Ingredients> mIngredientsList;

    public static final String INGREDIENTS_LIST_KEY = "ingredients_list";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Set the BakingWidgetService intent to act as the adapter for the StackView
        Intent remoteViewServiceIntent = new Intent(context, BakingWidgetService.class);
        remoteViewServiceIntent.putExtra(INGREDIENTS_LIST_KEY, mIngredientsList);
        // When intents are compared, the extras are ignored, so we need to embed the extras
        // into the data so that the extras will not be ignored
        remoteViewServiceIntent.setData(Uri.parse(remoteViewServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);
        if (mRecipe != null) {
            views.setTextViewText(R.id.baking_widget_recipe_name_tv, mRecipe.getName());
        } else {
            // If recipe is null, then display the Recipe Name of the widget from Shared Preferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String recipeNameJson = sharedPreferences.getString(RecipeDetailActivity.WIDGET_PREFERENCE_RECIPE_NAME_KEY, "");
            Gson gson = new Gson();
            String recipeTitle = gson.fromJson(recipeNameJson, String.class);
            views.setTextViewText(R.id.baking_widget_recipe_name_tv, recipeTitle);
        }
        views.setRemoteAdapter(R.id.baking_widget_list_view, remoteViewServiceIntent);
        // Handle empty Ingredients
        views.setEmptyView(R.id.baking_widget_list_view, R.id.baking_widget_empty_tv);

        // Set the intent to launch the MainActivity when clicked
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.baking_widget_recipe_name_tv, appPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.baking_widget_list_view);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getApplicationContext(), BakingWidgetProvider.class));
        //Get the recipe from the broadcast
        if (RecipeDetailActivity.ACTION_UPDATE_WIDGET.equals(intent.getAction())) {
            mRecipe = intent.getParcelableExtra(RecipeDetailActivity.WIDGET_RECIPE_KEY);
        } else {
            if (mRecipe != null) {
                mIngredientsList = mRecipe.getIngredientsList();
            }
        }
        onUpdate(context, appWidgetManager, appWidgetIds);
        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }


}

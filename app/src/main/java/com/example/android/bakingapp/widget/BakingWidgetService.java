package com.example.android.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeDetailActivity;
import com.example.android.bakingapp.models.Ingredients;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class BakingWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class BakingRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<Ingredients> mIngredientsList;
    private Context mContext;

    public BakingRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mIngredientsList = intent.getParcelableArrayListExtra(BakingWidgetProvider.INGREDIENTS_LIST_KEY);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        // Display the data from Shared Preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String ingredientsJson = sharedPreferences.getString(RecipeDetailActivity.WIDGET_PREFERENCE_INGREDIENTS_KEY, "");
        Gson gson = new Gson();
        if (ingredientsJson != null) {
            mIngredientsList = gson.fromJson(ingredientsJson, new TypeToken<ArrayList<Ingredients>>() {
            }.getType());
        }
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if (mIngredientsList == null) return 0;
        return mIngredientsList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.baking_widget_list_item);
        views.setTextViewText(R.id.widget_list_recipe_ingredients_tv, mIngredientsList.get(position).getIngredient());
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

package com.example.android.bakingapp.utilities;

import android.net.Uri;

import com.example.android.bakingapp.models.Ingredients;
import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.models.Steps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import timber.log.Timber;

public final class NetworkUtils {

    private static final String RECIPE_LIST_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    public static ArrayList<Recipe> fetchRecipeList() {
        Uri builtUri = Uri.parse(RECIPE_LIST_URL).buildUpon().build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Timber.d("Url for the recipe list from Udacity is " + url.toString());

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Timber.e("Error closing input stream", e);
        }

        // Extract the recipes from jsonResponse
        try {
            return extractFeatureFromJson(jsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        //If the url is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputstream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.connect();

             /*If the request was successful (response code 200),
             then read the input stream and parse the response.*/
            if (urlConnection.getResponseCode() == 200) {
                inputstream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputstream);
            } else {
                Timber.e("Error response code" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Timber.e("Problem retrieving the Recipe JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputstream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies that an IOException
                // could be thrown.
                try {
                    inputstream.close();
                } catch (final IOException e) {
                    Timber.e("Error closing input stream", e);
                }
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputstream) throws IOException {
        StringBuilder stringArray = new StringBuilder();

        InputStreamReader inputStreamReader = new InputStreamReader(inputstream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            stringArray.append(line);
            line = bufferedReader.readLine();
        }
        return stringArray.toString();
    }

    private static ArrayList<Recipe> extractFeatureFromJson(String jsonResponse)
            throws JSONException {
        final String RECIPE_ID = "id";
        final String RECIPE_NAME = "name";
        final String RECIPE_INGREDIENTS = "ingredients";
        final String INGREDIENTS_QUANTITY = "quantity";
        final String INGREDIENTS_MEASURE = "measure";
        final String INGREDIENTS_INGREDIENT = "ingredient";
        final String RECIPE_STEPS = "steps";
        final String STEPS_ID = "id";
        final String STEPS_SHORT_DESCRIPTION = "shortDescription";
        final String STEPS_DESCRIPTION = "description";
        final String STEPS_VIDEO_PATH = "videoURL";
        final String STEPS_THUMBNAIL = "thumbnailURL";
        final String RECIPE_SERVINGS = "servings";
        final String RECIPE_IMAGE = "image";

        ArrayList<Recipe> recipeArrayList = new ArrayList<>();

        //Get the root recipe array
        JSONArray recipeRootArray = new JSONArray(jsonResponse);

        for (int i = 0; i < recipeRootArray.length(); i++) {
            JSONObject recipeJsonObject = recipeRootArray.optJSONObject(i);

            // Get the recipe details
            String recipeId = recipeJsonObject.getString(RECIPE_ID);
            String recipeName = recipeJsonObject.getString(RECIPE_NAME);

            ArrayList<Ingredients> ingredientsArrayList = new ArrayList<>();

            //Get the ingredients Json Array
            JSONArray ingredientsJsonArray = recipeJsonObject.optJSONArray(RECIPE_INGREDIENTS);

            for (int j = 0; j < ingredientsJsonArray.length(); j++) {
                JSONObject ingredientsJsonObject = ingredientsJsonArray.getJSONObject(j);

                String ingredientsQuantity = ingredientsJsonObject.getString(INGREDIENTS_QUANTITY);
                String ingredientsMeasure = ingredientsJsonObject.getString(INGREDIENTS_MEASURE);
                String ingredientsIngredient = ingredientsJsonObject.getString(INGREDIENTS_INGREDIENT);

                ingredientsArrayList.add(new Ingredients(ingredientsQuantity, ingredientsMeasure,
                        ingredientsIngredient));
            }

            ArrayList<Steps> stepsArrayList = new ArrayList<>();

            //Get the steps Json Array
            JSONArray stepsJsonArray = recipeJsonObject.optJSONArray(RECIPE_STEPS);

            for (int k = 0; k < stepsJsonArray.length(); k++) {
                JSONObject stepsJsonObject = stepsJsonArray.getJSONObject(k);

                String stepsId = stepsJsonObject.getString(STEPS_ID);
                String stepsShortDescription = stepsJsonObject.getString(STEPS_SHORT_DESCRIPTION);
                String stepsDescription = stepsJsonObject.getString(STEPS_DESCRIPTION);
                String stepsVideoPath = stepsJsonObject.getString(STEPS_VIDEO_PATH);
                String stepsThumbnail = stepsJsonObject.getString(STEPS_THUMBNAIL);

                stepsArrayList.add(new Steps(stepsId, stepsShortDescription, stepsDescription,
                        stepsVideoPath, stepsThumbnail));
            }

            String recipeServings = recipeJsonObject.getString(RECIPE_SERVINGS);
            String recipeImage = recipeJsonObject.getString(RECIPE_IMAGE);

            recipeArrayList.add(new Recipe(recipeId, recipeName, ingredientsArrayList,
                    stepsArrayList, recipeServings, recipeImage));
        }

        return recipeArrayList;

    }

}

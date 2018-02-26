package com.example.android.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Recipe implements Parcelable {

    private String id;
    private String name;
    private ArrayList<Ingredients> ingredientsList;
    private ArrayList<Steps> stepsList;
    private String servings;
    private String image;

    public Recipe(String id, String name, ArrayList<Ingredients> ingredientsList,
                  ArrayList<Steps> stepsList, String servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredientsList = ingredientsList;
        this.stepsList = stepsList;
        this.servings = servings;
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeTypedList(this.ingredientsList);
        dest.writeTypedList(this.stepsList);
        dest.writeString(this.servings);
        dest.writeString(this.image);
    }

    protected Recipe(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.ingredientsList = in.createTypedArrayList(Ingredients.CREATOR);
        this.stepsList = in.createTypedArrayList(Steps.CREATOR);
        this.servings = in.readString();
        this.image = in.readString();
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Ingredients> getIngredientsList() {
        return ingredientsList;
    }

    public ArrayList<Steps> getStepsList() {
        return stepsList;
    }

    public String getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }
}

package com.example.android.bakingapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.models.Ingredients;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsAdapterViewHolder> {

    private ArrayList<Ingredients> mIngredientsArrayList;

    public IngredientsAdapter(ArrayList<Ingredients> ingredientsArrayList) {
        mIngredientsArrayList = ingredientsArrayList;
    }

    @Override
    public IngredientsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredients_list_item, parent, false);
        return new IngredientsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientsAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (null == mIngredientsArrayList) return 0;
        return mIngredientsArrayList.size();
    }

    public class IngredientsAdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ingredient_tv)
        TextView ingredientTextView;
        @BindView(R.id.ingredient_quantity_tv)
        TextView ingredientQuantityTextView;
        @BindView(R.id.ingredient_quantity_measure_tv)
        TextView ingredientQuantityMeasureTextView;

        public IngredientsAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            ingredientTextView.setText(mIngredientsArrayList.get(position).getIngredient());
            ingredientQuantityTextView.setText(mIngredientsArrayList.get(position).getQuantity());
            ingredientQuantityMeasureTextView.setText(mIngredientsArrayList.get(position).getMeasure());
        }
    }
}

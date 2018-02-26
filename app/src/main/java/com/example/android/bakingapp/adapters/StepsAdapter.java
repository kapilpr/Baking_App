package com.example.android.bakingapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.models.Steps;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsAdapterViewHolder> {

    public interface StepsAdapterOnClickHandler {
        void onClick(int clickedPosition);
    }

    private ArrayList<Steps> mStepsArrayList;
    private StepsAdapterOnClickHandler mClickHandler;

    public StepsAdapter(StepsAdapterOnClickHandler clickHandler, ArrayList<Steps> stepsArrayList) {
        mStepsArrayList = stepsArrayList;
        mClickHandler = clickHandler;
    }

    @Override
    public StepsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.steps_list_item, parent, false);
        return new StepsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepsAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (null == mStepsArrayList) return 0;
        return mStepsArrayList.size();
    }

    public class StepsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.steps_description_tv)
        TextView stepsDescriptionTextView;

        public StepsAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            stepsDescriptionTextView.setText(mStepsArrayList.get(position).getShortDescription());
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }
}
